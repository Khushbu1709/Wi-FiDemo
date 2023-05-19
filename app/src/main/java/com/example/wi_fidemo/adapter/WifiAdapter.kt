package com.example.wi_fidemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wi_fidemo.model.wifiModel
import com.example.wi_fidemo.databinding.ItemRecyclerBinding

class WifiAdapter(private val context: Context, private var bluetoothModel:ArrayList<wifiModel>, var callback:(position:Int)->Unit):RecyclerView.Adapter<WifiAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding= ItemRecyclerBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.name.text=bluetoothModel[position].name
        holder.binding.cardView.setOnClickListener {
            callback.invoke(position)
        }
    }

    override fun getItemCount(): Int=bluetoothModel.size
    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
        return super.getItemViewType(position)
    }

    inner class MyViewHolder(val binding: ItemRecyclerBinding):RecyclerView.ViewHolder(binding.root)
}
