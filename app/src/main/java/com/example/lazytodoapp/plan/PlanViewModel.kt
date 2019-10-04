package com.example.lazytodoapp.plan

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.main.Repeat
import com.example.lazytodoapp.tag
import java.util.*

class PlanViewModel(
    val context: Context,
    val model: PlanModel
) {
    val plan = ObservableField(
        Plan(
            dueDate = Calendar.getInstance().time
        )
    )

    fun onSubmit() {
        Toast.makeText(context, plan.get().toString(), Toast.LENGTH_LONG).show()
    }

    fun setDayOfWeek(dayOfWeek: Int) {
        val _plan = plan.get()!!
        val prev = _plan.repeat.everyDayOfWeek.toInt()
        Log.i(tag(), "setDayOfWeek ${prev} $dayOfWeek ${prev xor dayOfWeek}")
        _plan.repeat = _plan.repeat.copy(everyDayOfWeek = (prev xor (1 shl dayOfWeek)).toString())
        plan.set(_plan)
    }
}