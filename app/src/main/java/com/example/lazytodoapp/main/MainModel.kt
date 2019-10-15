package com.example.lazytodoapp.main

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.library.baseAdapters.BR
import com.example.lazytodoapp.tag
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Single
import java.lang.NullPointerException
import java.text.SimpleDateFormat
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

    fun checkPlan(plan: Plan): Completable {
        return remotePlanRepository.checkPlan(plan)
    }
}

interface PlanRepository {
    fun getPlans(date: Date): Single<List<Plan>>
    fun createPlan(plan: Plan): Single<Plan>
    fun checkPlan(plan: Plan): Completable
}

class RemotePlanRepository : PlanRepository {
    override fun checkPlan(plan: Plan): Completable {
        Log.i(tag(), "planId : ${plan.id}")
        return db.collection(USER).document(getUser().uid).collection(PLAN).document(plan.id)
            .set(plan, SetOptions.merge()).rx()
    }

    override fun createPlan(plan: Plan): Single<Plan> {
        return db.collection(USER).document(getUser().uid).collection(PLAN).add(plan)
            .rx(plan) { id, plan ->
                plan.copy(id = id)
            }
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
    val id: String = "",
    val createdAt: Date? = null,
    val priority: Float = Float.MAX_VALUE,
    var selectedDate: Date? = null,
    var title: String = "",
    var description: String = "",
    val duration: Duration = Duration(),
    var _isChecked: Boolean = false,

    var _dueDate: Date? = null,
    var _mandatory: Float = WANT,
    var _repeat: Repeat = Repeat()
) : BaseObservable() {

    var mandatory: Float
        @Bindable get() = _mandatory
        set(value) {
            this._mandatory = value
            notifyPropertyChanged(BR.mandatory)
        }
    var dueDate: Date?
        @Bindable get() = _dueDate
        set(value) {
            this._dueDate = value
            notifyPropertyChanged(BR.dueDate)
        }
    var repeat: Repeat
        @Bindable get() = _repeat
        set(value) {
            this._repeat = value
            notifyPropertyChanged(BR.repeat)
        }
    var isChecked: Boolean
        @Bindable get() = _isChecked
        set(value) {
            this._isChecked = value
            notifyPropertyChanged(BR.checked)
        }

    fun dueDateToString(): String {
        val now = Calendar.getInstance()
        val due = Calendar.getInstance().also {
            it.time = dueDate
        }
        val DAY_IN_MILLIS = 1000 * 60 * 60 * 24
        return if (due.timeInMillis - now.timeInMillis < DAY_IN_MILLIS) {
            "오늘까지"
        } else if (due.timeInMillis - now.timeInMillis < DAY_IN_MILLIS * 2) {
            "내일까지"
        } else {
            SimpleDateFormat("MM/dd까지", Locale.getDefault()).format(due.time)
        }
    }

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
    var everyNDay: String = "0",
    var _everyDayOfWeek: List<Boolean> = listOf(false, false, false, false, false, false, false)
) : BaseObservable() {
    companion object {
        const val MON = (1 shl 0).toString()
        const val TUE = (1 shl 1).toString()
        const val WED = (1 shl 2).toString()
        const val THU = (1 shl 3).toString()
        const val FRI = (1 shl 4).toString()
        const val SAT = (1 shl 5).toString()
        const val SUN = (1 shl 6).toString()
    }

    var everyDayOfWeek: List<Boolean>
        @Bindable get() = _everyDayOfWeek
        set(value) {
            this._everyDayOfWeek = value
            notifyPropertyChanged(BR.everyDayOfWeek)
        }

    //
//
//    fun isDayOfWeek(dayOfWeek: Int): Boolean {
//        val prev = everyDayOfWeek.toInt()
//        val isDayOfWeek = (1 shl prev and (1 shl dayOfWeek)) == 1 shl dayOfWeek
//        Log.i(tag(), "isDayOfWeek $dayOfWeek ${isDayOfWeek}")
//        return isDayOfWeek
//
//    }
//
    fun setDayOfWeek(dayOfWeek: Int) {
        everyDayOfWeek = everyDayOfWeek.toMutableList().also { it[dayOfWeek] = !it[dayOfWeek] }

    }
}

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
            it.id
            emitter.onSuccess(item)
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}


inline fun <reified T : Any> Task<DocumentReference>.rx(
    item: T,
    crossinline processId: (String, T) -> T
): Single<T> {
    return Single.create { emitter ->
        this.addOnSuccessListener {
            emitter.onSuccess(processId(it.id, item))
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}

fun Task<Void>.rx(): Completable {
    return Completable.create { emitter ->
        this.addOnSuccessListener {
            emitter.onComplete()
        }.addOnFailureListener {
            emitter.onError(it)
        }
    }
}
