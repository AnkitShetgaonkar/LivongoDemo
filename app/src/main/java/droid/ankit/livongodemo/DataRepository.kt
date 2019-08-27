package droid.ankit.livongodemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import droid.ankit.googlefit.GoogleFitServiceImpl
import droid.ankit.googlefit.StepsData
import droid.ankit.googlefit.StepsDataResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class DataRepository:KoinComponent,DataService {

    private val googleFitServiceImpl:GoogleFitServiceImpl by inject()
    override fun getStepsCountForTwoWeeks
                (liveData: MutableLiveData<StepsDataResponse>?,reverse:Boolean): MutableLiveData<StepsDataResponse>? {
        GlobalScope.launch {
            liveData!!.postValue(googleFitServiceImpl.getStepsCountForTwoWeeks(reverse))
        }
        return liveData
    }
}

interface DataService {
    fun getStepsCountForTwoWeeks(liveData: MutableLiveData<StepsDataResponse>?,reverse:Boolean): MutableLiveData<StepsDataResponse>?
}