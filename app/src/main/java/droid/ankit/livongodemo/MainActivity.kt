package droid.ankit.livongodemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etiennelenhart.eiffel.state.peek
import com.etiennelenhart.eiffel.viewmodel.delegate.providedViewModel
import com.google.android.material.snackbar.Snackbar
import droid.ankit.googlefit.StepsDataResponse
import droid.ankit.livongodemo.base.BaseActivity
import droid.ankit.livongodemo.util.PermissionCallback
import droid.ankit.livongodemo.util.PermissionManager
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity(), PermissionCallback {

    private val permissionManager:PermissionManager by inject()
    private val TAG:String = MainActivity::class.java.name
    private val viewModel by providedViewModel<MainViewModel>()
    private lateinit var progressBar:ProgressBar
    private lateinit var refreshBtn:Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StepsAdapter
    private lateinit var totalStepsTv: TextView
    private lateinit var switch:Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressBar)
        refreshBtn = findViewById<Button>(R.id.btnRefresh)
        recyclerView = findViewById(R.id.recyclerView)
        totalStepsTv = findViewById(R.id.totalStepsTv)
        switch = findViewById(R.id.switch1)
        adapter = StepsAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        permissionManager.checkFitnessPermission(this)

        viewModel.observeState(this){
            it.event?.peek { event-> handleScreenEvent(event) }
            it.stepDataList?.observe(this, Observer {stepsDataResponse->
                populateStepsData(stepsDataResponse)
            })
        }

        lifecycle.addObserver(viewModel)
        refreshBtn.setOnClickListener { viewModel.getStepsCountForTwoWeeks() }
        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.showReverseChronoStepsData()
            } else {
                viewModel.showChronoStepsData()
            }
        }
    }

    private fun populateStepsData(stepsDataResponse: StepsDataResponse){
        totalStepsTv.text = String.format(resources.getString(R.string.total_steps),stepsDataResponse.totalSteps)
        adapter.setData(stepsDataResponse.stepsData,stepsDataResponse.totalSteps)
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
