package com.example.lazytodoapp.plan

import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.main.RemotePlanRepository
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

class PlanModel(
    val remotePlanRepository: RemotePlanRepository
) {
    fun postPlan(plan: Plan): Single<Plan> {
        val createdAt = Calendar.getInstance().time
        return remotePlanRepository.createPlan(plan.copy(createdAt = createdAt))
    }

    fun deletePlan(plan: Plan): Completable {
        return remotePlanRepository.deletePlan(plan)
    }

}
