package com.ddoobbo.fathersms;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddoobbo.fathersms.databinding.ItemSmsListBinding
import com.ddoobbo.fathersms.model.SmsInfo
import com.ddoobbo.fathersms.model.StSms
import java.text.SimpleDateFormat
import java.util.*

class StoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var binding = ItemSmsListBinding.bind(itemView)
}

class SmsAdapter : RecyclerView.Adapter<StoreViewHolder>() {

    private var smsList: List<StSms> = ArrayList()

    fun updateItems(items: ArrayList<StSms>) {
        smsList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sms_list, parent, false)
        return StoreViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val stSms = smsList[position]
        holder.binding.address.text = stSms.smsList[0].address
        holder.binding.time.text = getDateTime(stSms.smsList[0].timestamp)
        holder.binding.body.text = stSms.smsList[0].text
    }

    override fun getItemCount() = smsList.size

    private fun getDateTime(s: Long): String? {
        try {
            val sdf = SimpleDateFormat("yy년 M월 d일 hh:mm")
            return sdf.format(s)
        } catch (e: Exception) {
            return e.toString()
        }
    }
}