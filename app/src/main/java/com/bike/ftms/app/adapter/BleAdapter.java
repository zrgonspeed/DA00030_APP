package com.bike.ftms.app.adapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.BleAdapter.BleViewHolder;
import com.bike.ftms.app.bean.MyScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/3/31
 */
public class BleAdapter extends RecyclerView.Adapter<BleViewHolder> {

    private List<MyScanResult> list;
    private OnItemClickListener onItemClickListener;

    public BleAdapter(List<MyScanResult> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BleViewHolder bleAdapter = new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false));
        return bleAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull BleViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (list.get(position).getConnectState()==1) {
                        return;
                }
                onItemClickListener.onItemClickListener(position);
            }
        });
        holder.tvName.setText(list.get(position).getScanResult().getDevice().getName());
        holder.tvAddress.setText(list.get(position).getScanResult().getDevice().getAddress());
        if (list.get(position).getConnectState()==1) {
            holder.tvState.setText("Connected");
        } else if (list.get(position).getConnectState()==2) {
            holder.tvState.setText("Connecting");
        }else {
            holder.tvState.setText("Disconnected");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
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
