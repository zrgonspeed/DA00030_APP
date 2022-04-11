package com.bike.ftms.app.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.adapter.BleAdapter.BleViewHolder;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.BaseBleManager;
import com.bike.ftms.app.ble.bean.MyScanResult;
import com.bike.ftms.app.ble.BleManager;
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

    @NonNull
    @Override
    public BleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BleViewHolder bleAdapter = new BleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble, parent, false));
        return bleAdapter;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BleViewHolder holder, int position) {
        MyScanResult myScanResult = list.get(position);
        Logger.e("ConnectState  == " + myScanResult.getConnectState() + "  name == " + myScanResult.getScanResult().getDevice().getName());

        holder.tvState.setOnClickListener(v -> {
            Logger.d("getConnectState()  == " + list.get(position).getConnectState() + "   " + list.get(position).getScanResult().getDevice().getName());
            if (myScanResult.getConnectState() == 2 || myScanResult.getConnectState() == 3) {
                return;
            }
            onItemClickListener.onItemClickListener(position, v, myScanResult.getConnectState());
        });
        holder.tvName.setText(myScanResult.getScanResult().getDevice().getName());
        holder.tvAddress.setText(myScanResult.getScanResult().getDevice().getAddress());
        if (myScanResult.getConnectState() == 1) {
            getBleManager().setPosition(position);
            holder.tvState.setText(MyApplication.getContext().getResources().getString(R.string.disconnect));
        } else if (myScanResult.getConnectState() == 2 || myScanResult.getConnectState() == 3) {
            holder.tvState.setText(MyApplication.getContext().getResources().getString(R.string.connecting));
        } else {
            holder.tvState.setText(MyApplication.getContext().getResources().getString(R.string.connect));
        }
    }

    private BaseBleManager baseBleManager;

    public void setBleManager(BaseBleManager baseBleManager) {
        this.baseBleManager = baseBleManager;
    }

    private BaseBleManager getBleManager() {
        return baseBleManager;
    };

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
