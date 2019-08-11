package droid.smart.com.tamilkuripugal.ui


import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.smart.droid.tamil.tips.R

class AppExitDialogFragment : DialogFragment() {

    // Use this instance of the interface to deliver action events
    internal lateinit var appExitDialogListener: AppExitDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the Builder class for convenient dialog construction
        val builder = AlertDialog.Builder(activity!!)
        builder.setMessage(R.string.exit_message)
            .setTitle(R.string.exit_title)
            .setPositiveButton(R.string.action_exit) { dialog, id ->
                // User confirmed to exit
                appExitDialogListener.onExitConfirm(this@AppExitDialogFragment)
            }
            .setNegativeButton(R.string.action_cancel) { dialog, id ->
                // User cancelled the dialog
                appExitDialogListener.onExitCancel(this@AppExitDialogFragment)
            }
            .setNeutralButton(R.string.action_rateme) { dialog, id ->
                // User choose to rate app
                appExitDialogListener.onExitRateme(this@AppExitDialogFragment)
            }
        // Create the AlertDialog object and return it
        return builder.create()
    }

    // Override the Fragment.onAttach() method to instantiate the AppExitDialogListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AppExitDialogListener so we can send events to the host
            appExitDialogListener = context as AppExitDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException("$context must implement AppExitDialogListener")
        }

    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    interface AppExitDialogListener {
        fun onExitConfirm(dialog: DialogFragment)

        fun onExitCancel(dialog: DialogFragment)

        fun onExitRateme(dialog: DialogFragment)
    }
}
