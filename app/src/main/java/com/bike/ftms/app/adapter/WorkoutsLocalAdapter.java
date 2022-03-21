package com.bike.ftms.app.adapter;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.Debug;
import com.bike.ftms.app.R;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.List;
import java.util.Vector;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsLocalAdapter extends RecyclerView.Adapter<WorkoutsLocalAdapter.WorkoutsViewHolder> {
    private static final String TAG = WorkoutsLocalAdapter.class.getSimpleName();

    private OnItemClickListener onItemClickListener;
    private OnItemDeleteListener onItemDeleteListener;
    private boolean isShowDelete = false;
    private final List<? extends RowerDataBean1> rowerDataBean1List;
    private Vector<Boolean> vector = new Vector<>();

    public WorkoutsLocalAdapter(List<? extends RowerDataBean1> rowerDataBean1List, Vector<Boolean> vector) {
        this.rowerDataBean1List = rowerDataBean1List;
        this.vector = vector;
    }

    @NonNull
    @Override
    public WorkoutsLocalAdapter.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsLocalAdapter.WorkoutsViewHolder holder, int position) {
        RowerDataBean1 bean1 = rowerDataBean1List.get(position);

        HttpRowerDataBean1 bean = ((HttpRowerDataBean1) (bean1));

        holder.itemView.setOnClickListener(v -> {
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.workouts_item_click, null));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                onItemClickListener.onItemClickListener(position);
            }, 150);
        });
        holder.ivDelete.setOnClickListener(v -> onItemDeleteListener.onItemDeleteListener(position));
        if (isShowDelete) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDelete.setVisibility(View.INVISIBLE);
        }
        if (bean.getNote() != null && !"".equals(bean.getNote())) {
            holder.ivNote.setVisibility(View.VISIBLE);
        } else {
            holder.ivNote.setVisibility(View.GONE);
        }
        holder.tvDate.setText(TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd HH:mm:ss"));

        // 本地数据才需要特别设置
        if (bean.getStatus() == 0) {
            bean.setTypeAndResult();
        }

        // 机型图标
        holder.iv_item_device.setImageDrawable(MyConstant.getCategoryImg(bean.getCategoryType()));

        if (Debug.canShowItemDeviceName) {
            holder.tv_workouts_device_name.setVisibility(View.VISIBLE);
        } else {
            holder.tv_workouts_device_name.setVisibility(View.GONE);
        }
        if (bean.getDeviceType() < 0 || bean.getDeviceType() > MyConstant.deviceNames.length - 1) {
            holder.tv_workouts_device_name.setText(MyConstant.deviceNames[0]);
        } else {
            holder.tv_workouts_device_name.setText(MyConstant.deviceNames[bean.getDeviceType()]);
        }

        if (Debug.canShowItemLocalId) {
            holder.tv_local_id.setVisibility(View.VISIBLE);
        } else {
            holder.tv_local_id.setVisibility(View.GONE);
        }
        holder.tv_local_id.setText(bean.getId() + "");

        holder.tvDistance.setText(bean.getType());
        holder.tvTime.setText(bean.getResult());

        if (bean.getStatus() == 1) {
            holder.tv_server_id.setText(((HttpRowerDataBean1) bean).getWorkout_id() + "");
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.workouts_item_uploaded, null));
        } else {
            holder.tv_server_id.setText(bean.getId() + "");
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white, null));
        }

    }

    @Override
    public int getItemCount() {
        return rowerDataBean1List.size();
    }

    public interface OnItemClickListener {
        void onItemClickListener(int position);
    }

    public void addItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemDeleteListener {
        void onItemDeleteListener(int position);
    }

    public void addItemDeleteClickListener(OnItemDeleteListener listener) {
        this.onItemDeleteListener = listener;
    }

    public void setShowDelete(boolean showDelete) {
        isShowDelete = showDelete;
    }

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_local_id;

        private ImageView ivDelete;
        private TextView tvDate;
        private TextView tvDistance;
        private ImageView iv_item_device;
        private TextView tv_workouts_device_name;
        private TextView tvTime;
        private ImageView ivNote;
        private LinearLayout ll_item;
        private TextView tv_server_id;

        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tv_local_id = itemView.findViewById(R.id.tv_local_id);
            iv_item_device = itemView.findViewById(R.id.iv_item_device);
            tv_workouts_device_name = itemView.findViewById(R.id.tv_workouts_device_name);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivNote = itemView.findViewById(R.id.iv_note);
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_server_id = itemView.findViewById(R.id.tv_server_id);
        }
    }
}
