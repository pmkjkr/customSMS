package com.ddoobbo.fathersms;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddoobbo.fathersms.databinding.ItemSmsListBinding
import com.ddoobbo.fathersms.model.SmsInfo
import java.text.SimpleDateFormat
import java.util.*

class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = ItemSmsListBinding.bind(itemView)
}

class SmsAdapter : RecyclerView.Adapter<StoreViewHolder>() {

    private var smsList: List<SmsInfo> = ArrayList()

    fun updateItems(items: ArrayList<SmsInfo>) {
        smsList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sms_list, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val sms = smsList[position]
        holder.binding.address.text = sms.address
        holder.binding.time.text = getDateTime(sms.timestamp)
        holder.binding.body.text = sms.text
    }

    override fun getItemCount() = smsList.size

    private fun getDateTime(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("M월 d일 hh:mm")
            val netDate = Date(s * 1000)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}