package com.example.lazytodoapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ActivityMainBinding
import com.example.lazytodoapp.main.adapter.PlanAdapter
import com.example.lazytodoapp.plan.PlanActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val model = MainModel(RemotePlanRepository())
    val viewModel = MainViewModel(model)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.mRvPlan.adapter = PlanAdapter(model)
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
