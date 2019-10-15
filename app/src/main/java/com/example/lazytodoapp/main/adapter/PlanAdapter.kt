package com.example.lazytodoapp.main.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.databinding.ItemPlanBinding
import com.example.lazytodoapp.main.MainModel
import com.example.lazytodoapp.main.Plan
import com.example.lazytodoapp.plan.PlanItemViewModel
import com.example.lazytodoapp.tag
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_plan.view.*


class PlanAdapter(val model: MainModel) : RecyclerView.Adapter<PlanViewHolder>() {

    private var _plans: List<Plan> = listOf()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val binding = ItemPlanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = PlanViewHolder(binding)

        holder.containerView.setOnClickListener {
            holder.onClick(holder.adapterPosition, _plans[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = _plans.size

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bindView(position, PlanItemViewModel(_plans[position], model))
    }

    fun setItem(plans: List<Plan>) {
        this._plans = plans
        notifyDataSetChanged()
    }

}

class PlanViewHolder(var binding: ItemPlanBinding) : RecyclerView.ViewHolder(binding.root), LayoutContainer {

    override val containerView: View = binding.root


    fun bindView(position: Int, viewModel: PlanItemViewModel) {

        binding.vm = viewModel
        binding.notifyChange()

//        val plan = viewModel.plan
//
//        containerView.mTvItemPlanTitle.text = plan.title
//        containerView.mTvItemPlanDescription.text = plan.description
//        containerView.mTvItemPlanDueDate.text = plan.dueDateToString()
//        containerView.mCbItemPlanCheck.isChecked = plan.isChecked
    }


    fun onClick(position: Int, item: Plan) {
        Toast.makeText(containerView.context, "$position clicked", Toast.LENGTH_SHORT).show()
    }
}