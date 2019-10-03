package com.example.lazytodoapp.main

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Single
import java.lang.NullPointerException
import java.util.*

class MainModel(
    val remotePlanRepository: RemotePlanRepository
) {
    fun getPlans(date: Date): Single<List<Plan>> {
        return remotePlanRepository.getPlans(date)
    }
}

interface PlanRepository {
    fun getPlans(date: Date): Single<List<Plan>>
}

class RemotePlanRepository : PlanRepository {
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
    val createdAt: Date,
    val priority: Float,
    val title: String = "",
    val description: String = "",
    val dueDate: Date
)

inline fun <reified T:Any> Task<QuerySnapshot>.rx(): Single<List<T>> {
    return Single.create { emitter ->
        this.addOnSuccessListener { snapshot ->
            val items = snapshot.documents.mapNotNull { it.toObject(T::class.java) as T}
            emitter.onSuccess(items)
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}