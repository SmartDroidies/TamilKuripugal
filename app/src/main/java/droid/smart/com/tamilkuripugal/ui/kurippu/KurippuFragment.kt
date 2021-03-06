package droid.smart.com.tamilkuripugal.ui.kurippu

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat.invalidateOptionsMenu
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.smart.droid.tamil.tips.R
import com.smart.droid.tamil.tips.databinding.KurippuFragmentBinding
import droid.smart.com.tamilkuripugal.AppExecutors
import droid.smart.com.tamilkuripugal.PERMISSION_EXTERNAL_WRITE_KURIPPU
import droid.smart.com.tamilkuripugal.binding.FragmentDataBindingComponent
import droid.smart.com.tamilkuripugal.di.Injectable
import droid.smart.com.tamilkuripugal.extensions.*
import droid.smart.com.tamilkuripugal.ui.common.KuripugalGestureListener
import droid.smart.com.tamilkuripugal.ui.common.RetryCallback
import droid.smart.com.tamilkuripugal.util.autoCleared
import timber.log.Timber
import javax.inject.Inject

class KurippuFragment : Fragment(), Injectable {

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

    lateinit var adFrame: FrameLayout

    @Inject
    lateinit var firebaseAnalytics: FirebaseAnalytics

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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.kurippu = kurippuViewModel.kurippu

        kurippuViewModel.favourite.observe(viewLifecycleOwner, Observer {
            Timber.d("Is Favourite : %s", it.data)
            invalidateOptionsMenu(activity)
        })

        firebaseAnalytics.kurippuView(params.kurippuId)

        binding.fabShare.setOnClickListener {
            Timber.d("Android Version : %s", Build.VERSION.SDK_INT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    shareKurippu(true)
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_EXTERNAL_WRITE_KURIPPU
                    )
                }
            } else {
                shareKurippu(false)
            }
        }

        adFrame = binding.adContainer
        adFrame.loadAd()


        firebaseAnalytics.setCurrentScreen(activity!!, this.javaClass.simpleName, this.javaClass.simpleName)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.kurippu_overflow_menu, menu)
        val menuFavourite = menu.findItem(R.id.action_favourite)
        val menuUnFavourite = menu.findItem(R.id.action_unfavourite)
        val resourceFavourite = kurippuViewModel.favourite.value
        if(resourceFavourite?.data != null) {
            menuFavourite.isVisible = false
            menuUnFavourite.isVisible = true
        }  else {
            menuFavourite.isVisible = true
            menuUnFavourite.isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favourite -> {
                kurippuViewModel.favourite()
                firebaseAnalytics.favourite(kurippuViewModel.getKurippuId())
                Snackbar.make(
                    binding.adContainer,
                    "Added Kurippu to favourites",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            R.id.action_unfavourite -> {
                kurippuViewModel.unfavourite()
                firebaseAnalytics.unfavourite(kurippuViewModel.getKurippuId())
                Snackbar.make(
                    binding.adContainer,
                    "Removed Kurippu from favourites",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareKurippu(indImage: Boolean) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        val shareText = getString(R.string.app_name) + " : " +
                binding.kurippuTitle.text + " - " +
                "http://play.google.com/store/apps/details?id=" + this.context!!.packageName
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, shareText
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
            val bitmap = view.extractBitmap()
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
