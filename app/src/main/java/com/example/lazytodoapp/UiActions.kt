package com.example.lazytodoapp

import android.content.Intent

interface UiActions {
    fun startActivity(intent: Intent)
    fun finish()
}