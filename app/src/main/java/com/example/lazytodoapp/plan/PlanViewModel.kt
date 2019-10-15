package com.example.lazytodoapp.plan

import androidx.databinding.ObservableField
import com.example.lazytodoapp.UiActions
import com.example.lazytodoapp.main.MainModel
import com.example.lazytodoapp.main.Plan
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class PlanViewModel(
    val uiActions: UiActions,
    val model: PlanModel
) {
    val plan = Plan(
        _dueDate = Calendar.getInstance().time
    )

    var isLoading = ObservableField(false)


    fun onSubmit() {

        model.postPlan(plan)
            .doOnSubscribe { isLoading.set(true) }
            .subscribeBy(
                onSuccess = {
                    isLoading.set(false)
                    uiActions.finish()
                },
                onError = {
                    isLoading.set(false)
                }
            )
    }
}

class PlanItemViewModel(
    var plan: Plan,
    val model: MainModel
) {

    fun onCheck(isChecked:Boolean) {
        model.checkPlan(plan.copy(_isChecked =isChecked))
            .subscribeBy(onComplete = {
                plan.isChecked = isChecked
            })
    }

}