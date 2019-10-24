package com.example.lazytodoapp.main.adapter

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ItemPlanBinding
import com.example.lazytodoapp.main.MainActivity
import com.example.lazytodoapp.main.MainModel
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.plan.PlanItemViewModel
import com.example.lazytodoapp.tag
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_plan_title.view.*


class PlanAdapter(val model: MainModel) : RecyclerView.Adapter<PlanBaseViewHolder>() {

    private var plans: List<BasePlan> = listOf()

    private var plansTod: List<BasePlan> = listOf()
    private var plansTom: List<BasePlan> = listOf()
    private var plansLater: List<BasePlan> = listOf()
    private var plansLate: List<BasePlan> = listOf()


    companion object {
        const val PLANS_TODAY = 0
        const val PLANS_TOMORROW = 1
        const val PLANS_LATER = 2
        const val PLANS_LATE = 3
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
            PLANS_TODAY -> plansTod = newPlans.map { PlanWrapper(it, PLANS_TODAY) }
            PLANS_TOMORROW -> plansTom = newPlans.map { PlanWrapper(it, PLANS_TOMORROW) }
            PLANS_LATER -> plansLater = newPlans.map { PlanWrapper(it, PLANS_LATER) }
            PLANS_LATE -> plansLate = newPlans.map { PlanWrapper(it, PLANS_LATE) }
        }

        val newPlansSectioned = mutableListOf<BasePlan>().apply {
            if (plansTod.isNotEmpty()) {
                add(PlanTitle("Today".toUpperCase()))
                addAll(plansTod)
            }
            if (plansTom.isNotEmpty()) {
                add(PlanTitle("Tomorrow".toUpperCase()))
                addAll(plansTom)
            }
            if (plansLater.isNotEmpty()) {
                add(PlanTitle("Later".toUpperCase()))
                addAll(plansLater)
            }
            if (plansLate.isNotEmpty()) {
                add(PlanTitle("Late".toUpperCase()))
                addAll(plansLate)
            }
        }.toList()

        Log.i(tag(), "plans setItem ${plans.size}")

        val oldPlans = mutableListOf<BasePlan>().apply { addAll(plans) }

        plans = mutableListOf<BasePlan>().apply { addAll(newPlansSectioned) }

        var removeIndex = 0
        oldPlans.forEachIndexed { index, basePlan ->
            if (!newPlansSectioned.contains(basePlan)) {
                notifyItemRemoved(index - removeIndex++)
                Log.i(tag(), "notify item removed $index")
            }
        }

        newPlansSectioned.forEachIndexed { index, basePlan ->
            if (!oldPlans.contains(basePlan)) {
                notifyItemInserted(index)
                Log.i(tag(), "notify item inserted $index")
            }
        }
    }
}

open class BasePlan


class PlanTitle(
    val title: String
) : BasePlan() {
    override fun equals(other: Any?): Boolean {
        return if (other is PlanTitle) {
            this.title == other.title
        } else {
            super.equals(other)
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

data class PlanWrapper(
    val plan: Plan,
    val listKey: Int
) : BasePlan()


abstract class PlanBaseViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    abstract fun bindView(position: Int, item: BasePlan)
}

class PlanTitleViewHolder(override val containerView: View) :
    PlanBaseViewHolder(containerView) {
    override fun bindView(position: Int, item: BasePlan) {
        containerView.mTvPlanTitle.text = (item as PlanTitle).title
    }


}

class PlanViewHolder(var binding: ItemPlanBinding, val model: MainModel) :
    PlanBaseViewHolder(binding.root) {

    override val containerView: View = binding.root
    var viewModel: PlanItemViewModel? = null

    override fun bindView(position: Int, item: BasePlan) {
        viewModel = PlanItemViewModel(item as PlanWrapper, model)
        binding.vm = viewModel
        binding.notifyChange()
    }

    fun onClick(position: Int, item: Plan) {
        binding.root.context.let {
            (it as MainActivity).startPlanDialog(item)
        }

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

@BindingAdapter("bindItem3")
fun RecyclerView.bindItem3(plans: ObservableList<Plan>) {
    (this.adapter as PlanAdapter?)?.setItem(plans, PlanAdapter.PLANS_LATE)
}


@BindingAdapter("strikeThrough")
fun TextView.bindStrikeThrough(enable: Boolean) {
    if (enable) {
        this.paintFlags = this.paintFlags or STRIKE_THRU_TEXT_FLAG
    } else {
        this.paintFlags = this.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }
}