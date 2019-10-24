package com.example.lazytodoapp.plan

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.example.lazytodoapp.UiActions
import com.example.lazytodoapp.databinding.ActivityPlanBinding
import com.example.lazytodoapp.main.MainActivity
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.main.RemotePlanRepository
import com.example.lazytodoapp.tag
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_plan.view.*
import java.text.SimpleDateFormat
import java.util.*

class PlanBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val viewModel = PlanViewModel(
        object : UiActions {
            override fun startActivity(intent: Intent) {
                startActivity(intent)
            }

            override fun finish() {
                (activity as MainActivity?)?.refreshPlans()
                dialog?.dismiss()
            }

        },
        PlanModel(RemotePlanRepository())
    )

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val binding = ActivityPlanBinding.inflate(LayoutInflater.from(context))
        binding.vm = viewModel
        val view = binding.root
        dialog.setContentView(view)

        arguments?.let { arguments ->
            arguments.getParcelable<Plan>(PlanViewModel.PLAN_TO_EDIT)?.let { planToEdit ->
                viewModel.plan = (planToEdit)
            }
        }


        view.mTvPlanDueDate.setOnClickListener {
            val cal = Calendar.getInstance()

            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    TimePickerDialog(
                        context,
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


        view.mChipGroupDueDate.setOnCheckedChangeListener { chipGroup, i ->
            val index = i - 1
            val prev = viewModel.dueDateSelection.indexOf(true)
            val prevDate = viewModel.plan.dueDate
            Log.i(tag(), "mChipGroupDueDate $index prev: $prev ${prevDate.toString()}")

            if (prev == index) return@setOnCheckedChangeListener

            if (index == PlanViewModel.DUE_CUSTOM) {
                val cal = Calendar.getInstance()
                DatePickerDialog(

                    requireContext(),
                    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                        TimePickerDialog(
                            context,
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
                            setOnCancelListener {
                                Log.i(tag(), "chipgroup setOnCancelListener")
                                viewModel.onSelectDueDate(prev, prevDate)
                            }
                        }.show()
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    setOnCancelListener {
                        Log.i(tag(), "chipgroup setOnCancelListener")
                        viewModel.onSelectDueDate(prev, prevDate)
                    }
                }.show()
            } else {
                viewModel.onSelectDueDate(index, null)
            }


        }

        return dialog
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
