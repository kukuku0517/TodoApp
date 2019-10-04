package com.example.lazytodoapp.main

import android.util.Log
import androidx.databinding.ObservableArrayList
import com.example.lazytodoapp.tag
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class MainViewModel(
    val model: MainModel
) {
    val _plans = ObservableArrayList<Plan>()
    val _date = Calendar.getInstance().time

    fun getPlans() {

        model.getPlans(_date)
            .subscribeBy(
                onSuccess = {
                    Log.i(tag(), "getPlans Suc")
                    _plans.addAll(it)
                },
                onError = {
                    Log.i(tag(), it.message)
                })
    }

    fun createPlan() {
        model.createDummyPlan()
            .subscribeBy(
                onSuccess = {
                    Log.i(tag(), "getPlans Suc")
                    _plans.clear()
                    _plans.addAll(it)
                },
                onError = {
                    Log.i(tag(), it.message)
                })
    }
}