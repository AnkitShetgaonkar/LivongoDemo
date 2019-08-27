package droid.ankit.livongodemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import droid.ankit.googlefit.StepsData
import kotlinx.android.synthetic.main.steps_item.view.*
import java.math.RoundingMode
import java.text.DateFormat
import java.text.DateFormat.getDateInstance
import java.text.DecimalFormat

class StepsAdapter:RecyclerView.Adapter<StepsViewHolder>(){

    private var items:List<StepsData> = emptyList()
    private var totalSteps:Long = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepsViewHolder {
        return StepsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.steps_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: StepsViewHolder, position: Int) {
        holder.dateTxt.text = getDateInstance().format(items[position].timeInMillis)
        holder.stepsTxt.text = items[position].steps.toString()
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        val percent = items[position].steps/totalSteps*100
        holder.worthTxt.text =
            String.format(holder.worthTxt.context.resources.getString(R.string.percent_worth),df.format(percent))
    }

    fun setData(list:List<StepsData>,totalSteps:Long) {
        this.items = list
        // safe check to avoid divide by zero errors
        this.totalSteps = if (totalSteps.compareTo(0) == 0) 1 else totalSteps
        notifyDataSetChanged()
    }
}

class StepsViewHolder(view: View):RecyclerView.ViewHolder(view){
    val dateTxt = view.dateTxt!!
    val stepsTxt = view.stepsCountTxt!!
    val worthTxt = view.percentWorthTxt!!
}