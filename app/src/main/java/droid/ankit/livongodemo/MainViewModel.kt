package droid.ankit.livongodemo

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import com.etiennelenhart.eiffel.state.ViewEvent
import com.etiennelenhart.eiffel.state.ViewState
import droid.ankit.googlefit.StepsData
import droid.ankit.livongodemo.base.BaseActivity
import droid.ankit.livongodemo.base.BaseViewModel
import droid.ankit.livongodemo.util.PermissionCallback
import org.koin.standalone.inject

class MainViewModel : BaseViewModel<MainViewState>(), LifecycleObserver, PermissionCallback {

    private val mDataRepository:DataRepository by inject()

    init {
        state.value = MainViewState()
    }

    override fun permissionGranted() {
        updateState { it.copy(event=MainViewEvent.PermissionGranted()) }
    }

    override fun getActivity(): BaseActivity? {
        //Do nothing
        return null
    }

    override fun permissionDenied() {
        updateState { it.copy(event=MainViewEvent.PermissionDenied(R.string.user_denied_message)) }
    }

    fun getStepsCountForTwoWeeks(){
        updateState { it.copy(stepDataList = mDataRepository.getStepsCountForTwoWeeks()) }
    }

}

sealed class MainViewEvent: ViewEvent(){
    class PermissionDenied(val message:Int):MainViewEvent()
    class PermissionGranted:MainViewEvent()
}

data class MainViewState(val stepDataList :LiveData<List<StepsData>>?=null,
                         val fetchingFromNetwork: LiveData<Boolean>?=null,
                         val event:MainViewEvent?=null): ViewState
