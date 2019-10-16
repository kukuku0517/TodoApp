package com.example.lazytodoapp

import java.util.*

fun Any.tag(): String {
    return this.javaClass.simpleName
}

operator fun Calendar.plus(day: Int): Calendar {
    this.add(Calendar.DATE, day)
    return this
}


operator fun Date.plus(day: Int): Date {
    return Calendar.getInstance().copy().also {
        time = this.time
        it.add(Calendar.DATE, day)
    }.time
}


fun Date.`is`(other: Date): Boolean {
    val calendar = Calendar.getInstance()
    val a = calendar.copy().also {
        time = this.time

    }.end()

    val b = calendar.copy().also {
        time = other.time

    }.end()

    return a.timeInMillis == b.timeInMillis
}

fun Calendar.copy():Calendar{
    return this.clone() as Calendar
}

fun Calendar.end(): Calendar {
    return this.copy().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 0)
    }
}