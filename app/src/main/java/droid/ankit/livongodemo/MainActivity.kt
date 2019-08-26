package droid.ankit.livongodemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import com.etiennelenhart.eiffel.state.peek
import com.etiennelenhart.eiffel.viewmodel.delegate.providedViewModel
import com.google.android.material.snackbar.Snackbar
import droid.ankit.livongodemo.base.BaseActivity
import droid.ankit.livongodemo.util.PermissionCallback
import droid.ankit.livongodemo.util.PermissionManager
import droid.ankit.livongodemo.util.toast
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity(), PermissionCallback {

    private val permissionManager:PermissionManager by inject()
    private val TAG:String = MainActivity::class.java.name
    private val viewModel by providedViewModel<MainViewModel>()
    private lateinit var progressBar:ProgressBar
    private lateinit var refreshBtn:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        refreshBtn = findViewById<Button>(R.id.btnRefresh)
        permissionManager.checkFitnessPermission(this)

        viewModel.observeState(this){
            it.event?.peek { event-> handleScreenEvent(event) }
            it.fetchingFromNetwork?.observe(this, Observer { isFetching->
                showLoader(isFetching)
            })
        }
        lifecycle.addObserver(viewModel)
    }

    private fun showLoader(isFetching:Boolean) {
        Log.d(TAG,"is fetching points from google fit")
    }

    private fun handleScreenEvent(event: MainViewEvent): Boolean {
        return when(event) {
            is MainViewEvent.PermissionGranted->{
                viewModel.getStepsCountForTwoWeeks()
                true
            }

            is MainViewEvent.PermissionDenied->{
                Snackbar.make(
                    findViewById(R.id.parentLayout),
                    event.message,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok){
                    permissionManager.checkFitnessPermission(this)
                }.show()
                true
            }
            else->false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        permissionManager.onRequestPermissionResult(requestCode,resultCode,intent,this)
    }


    override fun permissionGranted() {
        Log.d(TAG,"permission was granted")
        viewModel.permissionGranted()
    }

    override fun getActivity(): BaseActivity? {
        return this
    }

    override fun permissionDenied() {
        Log.e(TAG,"Permission was denied")
        viewModel.permissionDenied()
    }
}
