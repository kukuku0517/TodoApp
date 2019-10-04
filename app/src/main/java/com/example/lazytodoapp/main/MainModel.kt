package com.example.lazytodoapp.main

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Single
import java.lang.NullPointerException
import java.util.*

class MainModel(
    val remotePlanRepository: RemotePlanRepository
) {
    var _cachedPlan = mutableListOf<Plan>()

    fun getPlans(date: Date): Single<List<Plan>> {
        return remotePlanRepository.getPlans(date)
            .doOnSuccess { this._cachedPlan = it.toMutableList() }
    }

    fun createDummyPlan(): Single<MutableList<Plan>> {
        return remotePlanRepository.createPlan(
            Plan(
                createdAt = Calendar.getInstance().time,
                priority = 0f,
                title = "Auto Generated ${Math.random()}"
            )
        ).map {
            _cachedPlan.add(it)
            _cachedPlan
        }
    }
}

interface PlanRepository {
    fun getPlans(date: Date): Single<List<Plan>>
    fun createPlan(plan: Plan): Single<Plan>
}

class RemotePlanRepository : PlanRepository {
    override fun createPlan(plan: Plan): Single<Plan> {
        return db.collection(USER).document(getUser().uid).collection(PLAN).add(plan).rx(plan)
    }

    companion object {
        val USER = "USER"
        val PLAN = "PLAN"
    }

    val db = FirebaseFirestore.getInstance()

    var _user: FirebaseUser? = null

    private fun getUser(): FirebaseUser {
        return _user ?: run {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                _user = user
                user
            } else {
                throw NullPointerException()
            }
        }
    }

    override fun getPlans(date: Date): Single<List<Plan>> {
        val uid = getUser().uid
        return db.collection(USER).document(uid).collection(PLAN).get().rx()
    }
}

data class Plan(
    val createdAt: Date? = null,
    val priority: Float = Float.MAX_VALUE,
    var dueDate: Date? = null,
    val title: String = "",
    val description: String = "",
    val mandatory: Float = WANT,
    val duration: Duration = Duration(),
    val repeat: Repeat = Repeat(0, 0)
) {
    companion object {
        const val MUST = 1f
        const val SHOULD = 0.5f
        const val WANT = 0f
    }
}

data class Duration(
    val day: Int = 0,
    val hour: Int = 0
)

data class Repeat(
    val everyNDay: Int,
    val everyDayOfWeek: Int
)

inline fun <reified T : Any> Task<QuerySnapshot>.rx(): Single<List<T>> {
    return Single.create { emitter ->
        this.addOnSuccessListener { snapshot ->
            val items = snapshot.documents.mapNotNull { it.toObject(T::class.java) as T }
            emitter.onSuccess(items)
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}


inline fun <reified T : Any> Task<DocumentReference>.rx(item: T): Single<T> {
    return Single.create { emitter ->
        this.addOnSuccessListener {
            emitter.onSuccess(item)
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}