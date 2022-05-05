package com.demo.madcalculator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.demo.madcalculator.CalculationResult
import com.demo.madcalculator.R
import com.demo.madcalculator.databinding.ItemHistoryBinding

class HistoryAdapter(val context: Context) : RecyclerView.Adapter<HistoryAdapter.HistoryHolder>() {

    private val listOfHistory = mutableListOf<CalculationResult>()

    fun updateList(newList: MutableList<CalculationResult>) {
        this.listOfHistory.clear()
        this.listOfHistory.addAll(newList);
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
        return HistoryHolder(
            LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
        var result = listOfHistory[position];
        holder.binding.txtInput.text = result.input;
        holder.binding.txtResult.text = result.result;
    }

    override fun getItemCount(): Int {
        return listOfHistory.size;
    }

    class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var binding = ItemHistoryBinding.bind(itemView)
    }

}