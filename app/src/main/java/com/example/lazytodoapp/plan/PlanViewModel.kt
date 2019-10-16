package com.example.lazytodoapp.plan

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.example.lazytodoapp.UiActions
import com.example.lazytodoapp.end
import com.example.lazytodoapp.main.MainModel
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.main.adapter.PlanWrapper
import com.example.lazytodoapp.plus
import io.reactivex.rxkotlin.subscribeBy
import java.util.*

class PlanViewModel(
    val uiActions: UiActions,
    val model: PlanModel
) {

    companion object {

        const val DUE_TODAY = 0
        const val DUE_TOMORROW = 1
        const val DUE_DAY_AFTER = 2
        const val DUE_NEXT_WEEK = 3
        const val DUE_CUSTOM = 4


    }

    val plan = Plan(
        _dueDate = Calendar.getInstance().time
    )
    //    var dueDateSelection = ObservableField(0)
    var dueDateSelection = ObservableArrayList<Boolean>().apply {
        this.addAll(List(5) { false })
    }

    var isLoading = ObservableField(false)


    fun onSelectDueDate(index: Int, date: Date?) {
        val prev = dueDateSelection.indexOf(true)
        if (prev != -1) {
            dueDateSelection[prev] = false
        }
        dueDateSelection[index] = true

        when (index) {
            DUE_TODAY -> {
                plan.dueDate = Calendar.getInstance().end().time
            }
            DUE_TOMORROW -> {
                plan.dueDate = (Calendar.getInstance() + 1).end().time
            }
            DUE_DAY_AFTER -> {
                plan.dueDate = (Calendar.getInstance() + 2).end().time
            }
            DUE_NEXT_WEEK -> {
                plan.dueDate = (Calendar.getInstance() + 7).end().time
            }
            DUE_CUSTOM -> {
                plan.dueDate = date
            }
        }

    }

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
    val planWrapper : PlanWrapper,
    val model: MainModel
) {
    val plan = planWrapper.plan

    fun onCheck(isChecked: Boolean) {
        if (plan.isChecked != isChecked){
            model.checkPlan(plan.copy(_isChecked = isChecked))
                .subscribeBy(onComplete = {
                    plan.isChecked = isChecked
                })
        }
    }

}