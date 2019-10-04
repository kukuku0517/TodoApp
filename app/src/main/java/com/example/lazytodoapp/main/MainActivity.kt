package com.example.lazytodoapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ActivityMainBinding
import com.example.lazytodoapp.main.adapter.PlanAdapter
import com.example.lazytodoapp.plan.PlanActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.extensions.LayoutContainer

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val viewModel = MainViewModel(MainModel(RemotePlanRepository()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.mRvPlan.adapter = PlanAdapter()
        binding.mRvPlan.layoutManager = LinearLayoutManager(this)
        binding.mFabCreatePlan.setOnClickListener {
            startActivity(Intent(this, PlanActivity::class.java))
        }
        viewModel.getPlans()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlans()
    }
}

@BindingAdapter("bindItem")
fun RecyclerView.bindItem(plans: ObservableList<Plan>) {
    (this.adapter as PlanAdapter?)?.setItem(plans)
}