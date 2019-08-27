package droid.ankit.googlefit

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.android.gms.fitness.data.DataSource.TYPE_DERIVED








class GoogleFitServiceImpl(private val context: Context) {

    private val TAG = GoogleFitServiceImpl::class.java.simpleName

    fun getStepsCountForTwoWeeks(reverse:Boolean): StepsDataResponse {
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -2)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        val startTime = cal.timeInMillis

        val dateFormat = getDateInstance()
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime))
        Log.i(TAG, "Range End: " + dateFormat.format(endTime))

        val ESTIMATED_STEP_DELTAS = DataSource.Builder()
            .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
            .setType(DataSource.TYPE_DERIVED)
            .setStreamName("estimated_steps")
            .setAppPackageName("com.google.android.gms")
            .build()

        val readRequest = DataReadRequest.Builder()
            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        val historyApi = Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context)!!)
        val response: Task<DataReadResponse> = historyApi.readData(readRequest)
        return updateData(Tasks.await(response),startTime,reverse)
    }

    private fun updateData(dataReadResult: DataReadResponse, startTime:Long,reverse:Boolean):StepsDataResponse {
        val cal = Calendar.getInstance()
        cal.timeInMillis = startTime
        var totalSteps:Long = 0
        val stepsDataList = mutableListOf<StepsData>()
        if (dataReadResult.buckets.size > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                val dataSets = bucket.dataSets
                Log.e(TAG, "dataSets size, should be 1 >>> " + dataSets.size)
                if(dataSets.size < 1){
                    continue
                }
                // keep adding a day
                val stepsData:StepsData = getStepData(dataSets,cal.timeInMillis)
                cal.add(Calendar.DAY_OF_MONTH, 1)
                totalSteps += stepsData.steps
                stepsDataList.add(stepsData)
            }
        }
        if(reverse){
            stepsDataList.reverse()
        }
        return StepsDataResponse(totalSteps = totalSteps,stepsData = stepsDataList,reverse =reverse)
    }

    private fun getStepData(dataSets: MutableList<DataSet>,currTime: Long):StepsData {
        Log.e(TAG,"size ${dataSets[0].dataPoints.size} ")
        if(dataSets[0].dataPoints.size < 1){
            return StepsData(0,currTime)
        }
        val dp = dataSets[0].dataPoints[0]
        val steps = dp.getValue(dp.dataType.fields[0]).asInt()
        val dateFormat = getDateInstance()
        Log.e(TAG, "important Steps: $steps day: ${dateFormat.format(currTime)}")
        return StepsData(steps,currTime)
    }
}