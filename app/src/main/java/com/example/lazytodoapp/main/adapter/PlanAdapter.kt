package com.example.lazytodoapp.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.lazytodoapp.R
import com.example.lazytodoapp.main.Plan
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_plan.view.*


class PlanAdapter : RecyclerView.Adapter<PlanViewHolder>() {

    private var _plans: List<Plan> = listOf()

    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan, null)
        val holder = PlanViewHolder(view)

        holder.containerView.setOnClickListener {
            holder.onClick(holder.adapterPosition, _plans[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = _plans.size

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        holder.bindView(position, _plans[position])
    }

    fun setItem(plans: List<Plan>) {
        this._plans = plans
        notifyDataSetChanged()
    }

}

class PlanViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bindView(position: Int, item: Plan) {
        containerView.mTvItemPlanTitle.text = "$position : ${item.title} ${item.description}"
    }

    fun onClick(position: Int, item: Plan) {
        Toast.makeText(containerView.context, "$position clicked", Toast.LENGTH_SHORT).show()
    }
}