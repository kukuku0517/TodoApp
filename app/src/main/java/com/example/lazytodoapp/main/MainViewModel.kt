package com.example.lazytodoapp.main

import android.util.Log
import androidx.databinding.ObservableField
import com.example.lazytodoapp.tag
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class MainViewModel(
    val model: MainModel
) {
    val _plans = ObservableField<List<Plan>>()
    val date = Calendar.getInstance().time

    fun getPlans() {
        model.getPlans(date)
            .subscribeBy(
                onSuccess = {
                    Log.i(tag(),"getPlans Suc")
                    _plans.set(it)
                },
                onError = {
                    Log.i(tag(), it.message)
                })
    }
}