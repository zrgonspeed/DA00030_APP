package com.bike.ftms.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bike.ftms.app.R
import com.bike.ftms.app.adapter.BleAdapter.BleViewHolder
import com.bike.ftms.app.base.MyApplication
import com.bike.ftms.app.ble.BaseBleManager
import com.bike.ftms.app.ble.bean.MyScanResult
import com.bike.ftms.app.utils.Logger

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
class BleAdapter(private val list: MutableList<MyScanResult>?) :
    RecyclerView.Adapter<BleViewHolder>() {
    var bleManager: BaseBleManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleViewHolder {
        return BleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_ble, parent, false)
        )
    }

    @SuppressLint("MissingPermission")
    override fun onBindViewHolder(holder: BleViewHolder, position: Int) {
        val myScanResult = list!![position]
        Logger.d("onBindViewHolder() ConnectState  == " + myScanResult.connectState + "  name == " + myScanResult.scanResult.device.name)
        holder.tvState.setOnClickListener {
            Logger.d("getConnectState()  == " + list[position].connectState + "   " + list[position].scanResult.device.name)
            if (myScanResult.connectState == 2 || myScanResult.connectState == 3) {
                return@setOnClickListener
            }
            onItemClickListener!!.onItemClickListener(myScanResult)
        }
        holder.tvName.text = myScanResult.scanResult.device.name
        holder.tvAddress.text = myScanResult.scanResult.device.address
        if (myScanResult.connectState == 1) {
            holder.tvState.text =
                MyApplication.getContext().resources.getString(R.string.disconnect)
        } else if (myScanResult.connectState == 2 || myScanResult.connectState == 3) {
            holder.tvState.text =
                MyApplication.getContext().resources.getString(R.string.connecting)
        } else {
            holder.tvState.text = MyApplication.getContext().resources.getString(R.string.connect)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    interface OnItemClickListener {
        fun onItemClickListener(clickScanResult: MyScanResult?)
    }

    private var onItemClickListener: OnItemClickListener? = null
    fun addItemClickListener(listener: OnItemClickListener?) {
        onItemClickListener = listener
    }

    fun clear() {
        if (list == null || list.size == 0) {
            return
        }
        list.clear()
        notifyDataSetChanged()
    }

    inner class BleViewHolder(itemView: View) : ViewHolder(itemView) {
        var tvName: TextView = itemView.findViewById(R.id.tv_name)
        var tvState: TextView = itemView.findViewById(R.id.tv_state)
        var tvAddress: TextView = itemView.findViewById(R.id.tv_address)
    }

    companion object {
        private val TAG = BleAdapter::class.java.simpleName
    }
}
