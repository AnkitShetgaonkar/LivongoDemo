package droid.ankit.googlefit


data class StepsDataResponse(val totalSteps:Long,
                             val stepsData: List<StepsData>,
                             val reverse: Boolean=false)