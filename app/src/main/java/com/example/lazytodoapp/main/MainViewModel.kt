package com.example.lazytodoapp.main

import android.util.Log
import androidx.databinding.ObservableArrayList
import com.example.lazytodoapp.Time
import com.example.lazytodoapp.`is`
import com.example.lazytodoapp.plus
import com.example.lazytodoapp.tag
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class MainViewModel(
    val model: MainModel
) {
    val _plansLate = ObservableArrayList<Plan>()
    val _plansToday = ObservableArrayList<Plan>()
    val _plansTomorrow = ObservableArrayList<Plan>()
    val _plansLater = ObservableArrayList<Plan>()


    val _date = Calendar.getInstance().time
    var isLoading = false

    fun getPlans() {

        if (!isLoading) {
            model.getPlans(_date)
                .doOnSubscribe { isLoading = true }
                .subscribeBy(
                    onSuccess = {
                        Log.i(tag(), "getPlans Suc")
                        clearPlans()

                        allocatePlans(it)

                        isLoading = false
                    },
                    onError = {
                        isLoading = false
                        Log.i(tag(), it.message)
                    })
        }
    }

    private fun clearPlans() {
        _plansToday.clear()
        _plansTomorrow.clear()
        _plansLater.clear()
        _plansLate.clear()
    }

    private fun allocatePlans(plans: List<Plan>) {
        val now = Time()


        plans.forEach { plan ->
            plan.dueDate?.let { date ->
                val time = Time(date)

                when {
                    time.isSameDate(now) -> {
                        Log.i(tag(), "allocatePlans today ${time.toDate()} ${now.toDate()}")
                        _plansToday.add(plan)
                    }
                    time.isSameDate(now + 1) -> {
                        Log.i(tag(), "allocatePlans tom ${time.toDate()} ${now.toDate()}")
                        _plansTomorrow.add(plan)
                    }
                    time > now + 1 -> {
                        Log.i(tag(), "allocatePlans later ${time.toDate()} ${now.toDate()}")
                        _plansLater.add(plan)
                    }
                    else -> {
                        _plansLate.add(plan)
                    }
                }
            } ?: run {
                Log.i(tag(), "allocatePlans later2")
                _plansLater.add(plan)
            }
        }

        Log.i(tag(), "plan size : ${_plansToday.size}/${_plansTomorrow.size}/${_plansLater.size}")
    }

    fun createPlan() {
        model.createDummyPlan()
            .subscribeBy(
                onSuccess = {
                    Log.i(tag(), "getPlans Suc")
                    clearPlans()

                    allocatePlans(it)
                },
                onError = {
                    Log.i(tag(), it.message)
                })
    }
}