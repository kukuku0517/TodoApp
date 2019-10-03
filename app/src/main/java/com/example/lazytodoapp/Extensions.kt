package com.example.lazytodoapp

fun Any.tag(): String {
    return this.javaClass.simpleName
}