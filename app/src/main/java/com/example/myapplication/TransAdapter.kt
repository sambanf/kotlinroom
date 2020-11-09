package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.room.Trans
import kotlinx.android.synthetic.main.adapter_trans.view.*

class TransAdapter (private val tran: ArrayList<Trans>, private val listener:OnAdapterListener) :
    RecyclerView.Adapter<TransAdapter.TransViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransViewHolder {
        return TransViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_trans, parent, false)
        )
    }

    override fun getItemCount() = tran.size

    override fun onBindViewHolder(holder: TransViewHolder, position: Int) {
        val tra = tran[position]
        holder.view.text_title.text = tra.code
        holder.view.text2.text = tra.qty.toString()
        holder.view.text_title.setOnClickListener {
            listener.onClick(tra)
        }
        holder.view.text2.setOnClickListener {
            listener.onClick(tra)
        }
    }

    class TransViewHolder(val view: View) :RecyclerView.ViewHolder(view)
    fun setdata(list:List<Trans>){
        tran.clear()
        tran.addAll(list)
        notifyDataSetChanged()
    }

    interface OnAdapterListener{
        fun onClick(tran: Trans)
    }
}

private fun View.setOnClickListener(onClick: Unit) {

}
