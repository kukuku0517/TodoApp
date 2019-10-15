package com.example.lazytodoapp.plan

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.*
import com.example.lazytodoapp.R
import com.example.lazytodoapp.UiActions
import com.example.lazytodoapp.databinding.ActivityPlanBinding
import com.example.lazytodoapp.main.RemotePlanRepository
import com.example.lazytodoapp.tag
import kotlinx.android.synthetic.main.activity_plan.*
import java.text.SimpleDateFormat
import java.util.*

class PlanActivity : AppCompatActivity(), UiActions {

    lateinit var binding: ActivityPlanBinding
    val viewModel = PlanViewModel(
        this,
        PlanModel(RemotePlanRepository())
    )

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
                            //                            val plan = viewModel.plan
                            val newCal = Calendar.getInstance()
                            newCal.set(Calendar.YEAR, year)
                            newCal.set(Calendar.MONTH, month)
                            newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            newCal.set(Calendar.MINUTE, minute)
                            viewModel.plan.let {
                                viewModel.plan.dueDate = newCal.time
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

        mChipGroupDueDate.setOnCheckedChangeListener { chipGroup, i ->
            val index = i - 1
            val prev = viewModel.dueDateSelection.indexOf(true)
            val prevDate = viewModel.plan.dueDate
            Log.i(tag(), "mChipGroupDueDate $index prev: $prev ${prevDate.toString()}")

            if (prev == index) return@setOnCheckedChangeListener

            if (index == PlanViewModel.DUE_CUSTOM) {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        TimePickerDialog(
                            this,
                            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                                //                            val plan = viewModel.plan
                                val newCal = Calendar.getInstance()
                                newCal.set(Calendar.YEAR, year)
                                newCal.set(Calendar.MONTH, month)
                                newCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                                newCal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                                newCal.set(Calendar.MINUTE, minute)

                                viewModel.onSelectDueDate(index, newCal.time)

                            },
                            0,
                            0,
                            true
                        ).apply {
                            this.setOnCancelListener {
                                Log.i(tag(), "chipgroup setOnCancelListener")
                                viewModel.onSelectDueDate(prev, prevDate)
                            }
                        }.show()
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    this.setOnCancelListener {

                        Log.i(tag(), "chipgroup setOnCancelListener")
                        viewModel.onSelectDueDate(prev, prevDate)
                    }
                }.show()
            } else {
                viewModel.onSelectDueDate(index, null)
            }


        }
    }
}

@BindingAdapter("onDateChange")
fun TextView.onDateChange(date: Date?) {
    date?.let { date ->
        this.text = SimpleDateFormat("MM/dd까지", Locale.getDefault()).format(date.time)
    }

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
