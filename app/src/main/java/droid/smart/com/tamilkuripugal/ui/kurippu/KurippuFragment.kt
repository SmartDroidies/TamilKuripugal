package droid.smart.com.tamilkuripugal.ui.kurippu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.KurippuFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.PERMISSION_EXTERNAL_WRITE_KURIPPU
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.extensions.checkSelfPermissionCompat
import droid.smart.com.tamilkuripugal.testing.OpenForTesting
import droid.smart.com.tamilkuripugal.ui.common.KuripugalGestureListener
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@OpenForTesting
class KurippuFragment : Fragment(), Injectable {

    private val SHARE_FOLDER_NAME = "Tamil_Kuripugal"

    private lateinit var mDetector: GestureDetectorCompat

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var kurippuViewModel: KurippuViewModel

    @Inject
    lateinit var appExecutors: AppExecutors

    private lateinit var layout: View

    // mutable for testing
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    var binding by autoCleared<KurippuFragmentBinding>()

    @Inject
    lateinit var adRequest: AdRequest

    lateinit var mAdView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val dataBinding = DataBindingUtil.inflate<KurippuFragmentBinding>(
            inflater,
            R.layout.kurippu_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                kurippuViewModel.retry()
            }
        }

        mDetector = GestureDetectorCompat(context, object : KuripugalGestureListener() {
            override fun onSwipeLeft(): Boolean {
                Timber.i("Left swipe")
                //naalViewModel.nextDay()
                return true
            }

            override fun onSwipeRight(): Boolean {
                Timber.i("Right swipe")
                //naalViewModel.prevDay()
                return true
            }
        })

        dataBinding.touchListener = View.OnTouchListener { _, event ->
            mDetector.onTouchEvent(event)
        }

        layout = dataBinding.tipContent

        binding = dataBinding
        return dataBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        kurippuViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(KurippuViewModel::class.java)
        val params = KurippuFragmentArgs.fromBundle(arguments!!)
        Timber.i("Display Kurippu details for : %s ", params.kurippuId)
        kurippuViewModel.setKurippuId(params.kurippuId)
        binding.setLifecycleOwner(viewLifecycleOwner)
        binding.kurippu = kurippuViewModel.kurippu

        binding.fabShare.setOnClickListener { view ->
            Timber.d("Android Version : %s", Build.VERSION.SDK_INT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    shareKurippu(true)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSION_EXTERNAL_WRITE_KURIPPU)
                }
            } else {
                shareKurippu(false)
            }
        }

//        mAdView = binding.adView
//        mAdView.loadAd(adRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.kurippu_overflow_menu, menu)
    }

    private fun shareKurippu(indImage: Boolean) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            binding.kurippuTitle.text
        //FIXME - Share the link to Kurippu here
        )
        if (indImage) {
            Timber.i("Store & Share image here")
            val imageKurippuUri = collectKurippuImage(binding.tipContent)
            shareIntent.type = "image/*"
            if (imageKurippuUri != null) {
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    imageKurippuUri
                )
            }
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)))
    }

    private fun collectKurippuImage(view: View): Uri? {
        var imageUri: Uri? = null
        try {

            val mediaStorageDir =
                File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), SHARE_FOLDER_NAME)

            // Create a storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                Timber.d("Can i write to %s - %s", mediaStorageDir.toURI() , mediaStorageDir.canWrite())
                Timber.d("Can i read from %s - %s", mediaStorageDir.toURI(), mediaStorageDir.canRead())
                if (!mediaStorageDir.mkdirs()) {
                    Timber.w("Failed to create share directory : %s ", mediaStorageDir.absolutePath)
                    if (!mediaStorageDir.canWrite()) {
                        Timber.i(">> We can't write! Do we have WRITE_EXTERNAL_STORAGE permission?")
                        if (context!!.checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED) {
                            Timber.i(">> We don't have permission to write - please add it.")
                        } else {
                            Timber.i("We do have permission - the problem lies elsewhere.")
                        }
                    }
                    return null
                }
            }

            // Create a media file name
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val imageName = "Kurippu_$timeStamp.jpg"

            val selectedOutputPath = mediaStorageDir.path + File.separator + imageName
            Timber.d("Image stored : $selectedOutputPath")

            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            var bitmap = Bitmap.createBitmap(view.drawingCache)

            val maxSize = 1080

            val bWidth = bitmap.width
            val bHeight = bitmap.height

            if (bWidth > bHeight) {
                val imageHeight = Math.abs(maxSize * (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, maxSize, imageHeight, true)
            } else {
                val imageWidth = Math.abs(maxSize * (bitmap.width.toFloat() / bitmap.height.toFloat())).toInt()
                bitmap = Bitmap.createScaledBitmap(bitmap, imageWidth, maxSize, true)
            }
            view.isDrawingCacheEnabled = false
            view.destroyDrawingCache()

            var fOut: OutputStream? = null
            try {
                val file = File(selectedOutputPath)
                fOut = FileOutputStream(file)

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            imageUri = Uri.parse(
                MediaStore.Images.Media.insertImage(this.context!!.contentResolver, bitmap, null, null)
            )
        } catch (e: NullPointerException) {
            Timber.e("Failed to store kurippu app image")
        }
        return imageUri
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

}
