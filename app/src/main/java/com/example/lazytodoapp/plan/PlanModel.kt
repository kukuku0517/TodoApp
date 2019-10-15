package com.example.lazytodoapp.plan

import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.main.RemotePlanRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy
import java.lang.NullPointerException
import java.util.*

class PlanModel(
    val remotePlanRepository: RemotePlanRepository
) {
    fun postPlan(plan: Plan): Single<Plan> {
        return remotePlanRepository.createPlan(plan.copy(createdAt = Calendar.getInstance().time))

    }
}
