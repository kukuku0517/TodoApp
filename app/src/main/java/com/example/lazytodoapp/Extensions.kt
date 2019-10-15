package com.example.lazytodoapp

import java.util.*

fun Any.tag(): String {
    return this.javaClass.simpleName
}

operator fun Calendar.plus(day: Int): Calendar {
    this.add(Calendar.DATE, day)
    return this
}

fun Calendar.end():Calendar{
    return (this.clone() as Calendar).apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
    }
}