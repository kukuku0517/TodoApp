package com.example.lazytodoapp.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lazytodoapp.databinding.ActivityMainBinding
import com.example.lazytodoapp.main.adapter.PlanAdapter
import com.example.lazytodoapp.plan.PlanBottomSheetDialogFragment
import com.example.lazytodoapp.plan.PlanViewModel
import com.example.lazytodoapp.tag


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val model = MainModel(RemotePlanRepository())
    val viewModel = MainViewModel(model)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.example.lazytodoapp.R.layout.activity_main)
        binding.vm = viewModel
        binding.mRvPlan.adapter = PlanAdapter(model)
        binding.mRvPlan.layoutManager = LinearLayoutManager(this)
        binding.mFabCreatePlan.setOnClickListener {
            startPlanDialog(null)
        }
        refreshPlans()
    }

    fun startPlanDialog(plan: Plan?) {
        val mySheetDialog = PlanBottomSheetDialogFragment()
        val fm = supportFragmentManager
        plan?.let {
            mySheetDialog.arguments =
                Bundle().apply { putParcelable(PlanViewModel.PLAN_TO_EDIT, it) }
        }
        mySheetDialog.show(fm, "modalSheetDialog")
    }

    override fun onResume() {
        super.onResume()
        Log.i(tag(), "onResume")

    }

    fun refreshPlans(){
        viewModel.getPlans()
    }
}
