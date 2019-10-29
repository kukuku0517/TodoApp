package com.example.lazytodoapp.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lazytodoapp.UiActions
import com.example.lazytodoapp.databinding.ActivityMainBinding
import com.example.lazytodoapp.main.adapter.PlanAdapter
import com.example.lazytodoapp.plan.PlanBottomSheetDialogFragment
import com.example.lazytodoapp.plan.PlanModel
import com.example.lazytodoapp.plan.PlanViewModel
import com.example.lazytodoapp.tag


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val model = MainModel(RemotePlanRepository())
    val viewModel = MainViewModel(model)

    private val planViewModel = PlanViewModel(
        object : UiActions {
            override fun toast(msg: String) {
                Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show()
            }

            override fun startActivity(intent: Intent) {
                startActivity(intent)
            }

            override fun finish() {
                refreshPlans()
            }

        },
        PlanModel(RemotePlanRepository())
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, com.example.lazytodoapp.R.layout.activity_main)
        binding.vm = viewModel
        binding.planViewModel = planViewModel
        binding.mRvPlan.adapter = PlanAdapter(model)
        binding.mRvPlan.layoutManager = LinearLayoutManager(this)
        binding.mFabCreatePlan.setOnClickListener {
            startPlanDialog(null)
        }
        refreshPlans()
    }

    fun startPlanDialog(plan: Plan?) {
//        val mySheetDialog = PlanBottomSheetDialogFragment()
//        val fm = supportFragmentManager
//
        plan?.let {
            planViewModel.plan = it
//            mySheetDialog.arguments =
//                Bundle().apply { putParcelable(PlanViewModel.PLAN_TO_EDIT, it) }
        }
//        mySheetDialog.show(fm, "modalSheetDialog")


    }

    override fun onResume() {
        super.onResume()
        Log.i(tag(), "onResume")

    }

    fun refreshPlans() {
        viewModel.getPlans()
    }
}
