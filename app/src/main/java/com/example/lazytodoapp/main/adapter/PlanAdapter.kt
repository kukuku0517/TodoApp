package com.example.lazytodoapp.main.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ItemPlanBinding
import com.example.lazytodoapp.main.MainModel
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.plan.PlanItemViewModel
import com.example.lazytodoapp.tag
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_plan.view.*
import kotlinx.android.synthetic.main.item_plan_title.view.*


class PlanAdapter(val model: MainModel) : RecyclerView.Adapter<PlanBaseViewHolder>() {

    private var plans: List<BasePlan> = listOf()

    private var plansTod: List<BasePlan> = listOf()
    private var plansTom: List<BasePlan> = listOf()
    private var plansLat: List<BasePlan> = listOf()


    companion object {
        const val PLANS_TODAY = 0
        const val PLANS_TOMORROW = 1
        const val PLANS_LATER = 2
    }


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanBaseViewHolder {
        return when (viewType) {
            0 -> {
                PlanTitleViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.item_plan_title,
                        parent,
                        false
                    )
                )
            }
            else -> {
                val binding =
                    ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = PlanViewHolder(binding, model)
                holder.containerView.setOnClickListener {
                    holder.onClick(
                        holder.adapterPosition,
                        (plans[holder.adapterPosition] as PlanWrapper).plan
                    )
                }
                holder

            }


        }
    }

    override fun getItemCount() = plans.size

    override fun getItemViewType(position: Int): Int {
        return when (plans[position]) {
            is PlanTitle -> 0
            else -> 1
        }
    }

    override fun onBindViewHolder(holder: PlanBaseViewHolder, position: Int) {
        holder.bindView(position, plans[position])
    }

    fun setItem(newPlans: List<Plan>, key: Int) {
        when (key) {
            PLANS_TODAY -> plansTod = newPlans.map { PlanWrapper(it) }
            PLANS_TOMORROW -> plansTom = newPlans.map { PlanWrapper(it) }
            PLANS_LATER -> plansLat = newPlans.map { PlanWrapper(it) }
        }

        plans = mutableListOf<BasePlan>().apply {
            if (plansTod.isNotEmpty()) {
                add(PlanTitle("Today".toUpperCase()))
                addAll(plansTod)
            }
            if (plansTom.isNotEmpty()) {
                add(PlanTitle("Tomorrow".toUpperCase()))
                addAll(plansTom)
            }
            if (plansLat.isNotEmpty()) {
                add(PlanTitle("Later".toUpperCase()))
                addAll(plansLat)
            }
        }.toList()

        Log.i(tag(), "plans setItem ${plans.size}")
        notifyDataSetChanged()
    }
}

open class BasePlan


class PlanTitle(
    val title: String
) : BasePlan()

data class PlanWrapper(
    val plan: Plan
) : BasePlan()


abstract class PlanBaseViewHolder(override val containerView: View): RecyclerView.ViewHolder(containerView), LayoutContainer {
    abstract fun bindView(position: Int, item: BasePlan)
}

class PlanTitleViewHolder(override val containerView: View) :
    PlanBaseViewHolder(containerView) {
    override fun bindView(position: Int, item: BasePlan) {
        containerView.mTvPlanTitle.text = (item as PlanTitle).title
    }


}

class PlanViewHolder(var binding: ItemPlanBinding, val model: MainModel) : PlanBaseViewHolder(binding.root){

    override val containerView: View = binding.root

    override fun bindView(position: Int, item: BasePlan) {
        val viewModel = PlanItemViewModel((item as PlanWrapper).plan, model)
        binding.vm = viewModel
        binding.notifyChange()
    }

    fun onClick(position: Int, item: Plan) {
        Toast.makeText(containerView.context, "$position clicked", Toast.LENGTH_SHORT).show()
    }
}


@BindingAdapter("bindItem0")
fun RecyclerView.bindItem0(plans: ObservableList<Plan>) {
    (this.adapter as PlanAdapter?)?.setItem(plans, PlanAdapter.PLANS_TODAY)
}


@BindingAdapter("bindItem1")
fun RecyclerView.bindItem1(plans: ObservableList<Plan>) {
    (this.adapter as PlanAdapter?)?.setItem(plans, PlanAdapter.PLANS_TOMORROW)
}


@BindingAdapter("bindItem2")
fun RecyclerView.bindItem2(plans: ObservableList<Plan>) {
    (this.adapter as PlanAdapter?)?.setItem(plans, PlanAdapter.PLANS_LATER)
}