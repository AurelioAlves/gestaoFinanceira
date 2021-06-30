package com.example.gestaofinanceira.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaofinanceira.R
import com.example.gestaofinanceira.model.Financy

class FinancyAdapter(val financys:List<Financy>):RecyclerView.Adapter<FinancyAdapter.FinancyViewHolder>() {

    private var listener:FinancyItemListener? = null

    class FinancyViewHolder(itemView: View, listener: FinancyItemListener?):RecyclerView.ViewHolder(itemView) {
        val name:TextView = itemView.findViewById(R.id.item_textview_name)
        val price:TextView = itemView.findViewById(R.id.item_textview_price)
        val isReceita:View = itemView.findViewById(R.id.item_view_isreceita)

        init {
            itemView.setOnClickListener{
                listener?.onFinancyItemClick( it, adapterPosition)
            }

            itemView.setOnLongClickListener{
                listener?.onFinancyItemLongClick( it, adapterPosition )
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinancyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_financy, parent, false)
        return FinancyViewHolder(itemView, listener)
    }

    override fun onBindViewHolder(holder: FinancyViewHolder, position: Int) {
        holder.name.text = financys[position].name
        holder.price.text = financys[position].price.toString()

        if(financys[position].receita) {

            holder.isReceita.setBackgroundColor(Color.GREEN)
        } else {
            holder.isReceita.setBackgroundColor(Color.RED)
        }
    }

    override fun getItemCount(): Int {
        return financys.size
    }

    fun setFinancyItemListener(listener: FinancyItemListener) {
        this.listener = listener
    }
}