package droid.ankit.livongodemo

import androidx.lifecycle.LiveData
import droid.ankit.googlefit.StepsData
import org.koin.standalone.KoinComponent

class DataRepository:KoinComponent,DataService {
    override fun getStepsCountForTwoWeeks(): LiveData<List<StepsData>>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

interface DataService {
    fun getStepsCountForTwoWeeks(): LiveData<List<StepsData>>?
}