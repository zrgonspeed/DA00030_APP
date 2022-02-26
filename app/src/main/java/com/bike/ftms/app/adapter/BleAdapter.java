package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.BleAdapter.BleViewHolder;
import com.bike.ftms.app.bean.bluetooth.MyScanResult;
import com.bike.ftms.app.manager.ble.BleManager;
import com.bike.ftms.app.utils.Logger;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class BleAdapter extends RecyclerView.Adapter<BleViewHolder> {

    private static final String TAG = BleAdapter.class.getSimpleName();
    private List<MyScanResult> list;
    private OnItemClickListener onItemClickListener;

    public BleAdapter(List<MyScanResult> list) {
        this.list = list;
    }

    public void clear() {
        if (list == null || list.size() == 0) {
            return;
        }

        list.clear();
        notifyDataSetChanged();
    }

    /**
     * 设置新的数据
     */
    public void setData(@Nullable List<MyScanResult> data) {
        list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BleViewHolder bleAdapter = new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false));
        return bleAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull BleViewHolder holder, int position) {
        MyScanResult myScanResult = list.get(position);
        // Logger.e("ConnectState  == " + myScanResult.getConnectState() + "  name == " + myScanResult.getScanResult().getDevice().getName());

        holder.tvState.setOnClickListener(v -> {
//            Logger.e("list.get(position).getConnectState()  == " + list.get(position).getConnectState());
            if (myScanResult.getConnectState() == 2) {
                return;
            }
            onItemClickListener.onItemClickListener(position, v, myScanResult.getConnectState());
        });
        holder.tvName.setText(myScanResult.getScanResult().getDevice().getName());
        holder.tvAddress.setText(myScanResult.getScanResult().getDevice().getAddress());
        if (myScanResult.getConnectState() == 1) {
            BleManager.getInstance().mPosition = position;
            holder.tvState.setText("Disconnect");
        } else if (myScanResult.getConnectState() == 2) {
            holder.tvState.setText("Connecting");
        } else {
            holder.tvState.setText("Connect");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position, View v, int connectState);
    }

    public void addItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public class BleViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvState;
        TextView tvAddress;

        public BleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvState = itemView.findViewById(R.id.tv_state);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }
}
