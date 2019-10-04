package com.example.lazytodoapp.plan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.*
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ActivityPlanBinding
import com.example.lazytodoapp.main.Plan
import kotlinx.android.synthetic.main.activity_plan.*
import org.w3c.dom.Text
import java.util.*

class PlanActivity : AppCompatActivity() {

    lateinit var binding: ActivityPlanBinding
    val viewModel = PlanViewModel(this, PlanModel())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_plan)
        binding.vm = viewModel
        mTvPlanDueDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        this,
                        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                            val plan = viewModel.plan.get()
                            val newCal = Calendar.getInstance()
                            newCal.set(Calendar.YEAR, year)
                            newCal.set(Calendar.MONTH, month)
                            newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            newCal.set(Calendar.MINUTE, minute)
                            plan?.let {
                                viewModel.plan.set(it.copy(dueDate = newCal.time))
                            }
                        },
                        0,
                        0,
                        true
                    ).show()
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }
}

@BindingAdapter("onDateChange")
fun TextView.onDateChange(date: Date?) {
    this.text = date.toString()
}

@BindingAdapter("android:textStyle")
fun TextView.setTypeface(style: String) {
    when (style) {
        "bold" ->
            this.setTypeface(null, Typeface.BOLD)
        else ->
            this.setTypeface(null, Typeface.NORMAL)
    }
}


@BindingAdapter("android:text")
fun TextView.setText(number: Number) {
    this.text = number.toString()
}

@BindingAdapter("selectedValue")
fun SeekBar.setSelectedValue(selectedValue: Float) {
    this.progress = selectedValue.toInt()
}

@BindingAdapter("selectedValueAttrChanged")
fun SeekBar.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
    this.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
        val cachedProgress = -1

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            inverseBindingListener?.onChange()

        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }

    })
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun SeekBar.getSelectedValue(): Float {
    Log.i("SeekBar", "getSelectedValue ${this.progress.toFloat()}")
    return this.progress.toFloat()
}
