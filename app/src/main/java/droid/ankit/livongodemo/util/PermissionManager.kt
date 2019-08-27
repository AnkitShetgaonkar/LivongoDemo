package droid.ankit.livongodemo.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import droid.ankit.livongodemo.base.BaseActivity

class PermissionManager(private val context:Context) {

    fun checkFitnessPermission(permissionCallback:PermissionCallback) {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                permissionCallback.getActivity()!!,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(context),
                fitnessOptions
            )
        } else {
            permissionCallback.permissionGranted()
        }
    }

    fun onRequestPermissionResult(requestCode:Int,resultCode:Int,data: Intent?,permissionCallback: PermissionCallback) {
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                permissionCallback.permissionGranted()
            }else{
                permissionCallback.permissionDenied()
            }
        }
    }

    fun isPermissionGranted():Boolean {
        val fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .build()

        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(context), fitnessOptions)
    }

    companion object {
        private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1001
    }
}

interface PermissionCallback {
    fun permissionGranted()
    fun getActivity():BaseActivity?
    fun permissionDenied()
}