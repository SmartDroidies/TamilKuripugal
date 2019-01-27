package droid.smart.com.tamilkuripugal.extensions

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int
) {
    ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

fun Fragment.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this.context!!, permission)

