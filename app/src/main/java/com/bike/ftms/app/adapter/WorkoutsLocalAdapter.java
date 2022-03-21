package com.bike.ftms.app.adapter;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
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
import com.bike.ftms.app.activity.OnOrientationChanged;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.ble.bean.rundata.raw.RowerDataBean1;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.List;
import java.util.Vector;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsLocalAdapter extends RecyclerView.Adapter<WorkoutsLocalAdapter.WorkoutsViewHolder> implements OnOrientationChanged {
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
        // Logger.i("onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsLocalAdapter.WorkoutsViewHolder holder, int position) {
        // Logger.i("onBindViewHolder ori == " + ori);

        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            holder.ll_workouts_item_type.setPadding(getIntDimen(R.dimen.dp_50), 0, 0, 0);
            holder.tv_distance.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_12));
            holder.tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_12));
            holder.tv_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_12));
            holder.tv_workouts_device_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_12));

            {
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.ll_workouts_item_result.getLayoutParams();
                params2.weight = 0.7f;
                holder.ll_workouts_item_result.setLayoutParams(params2);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.ll_workouts_item_type.getLayoutParams();
                params3.weight = 1.3f;
                holder.ll_workouts_item_type.setLayoutParams(params3);
            }

        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            holder.ll_workouts_item_type.setPadding(0, 0, 0, 0);
            holder.tv_distance.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_11));
            holder.tv_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_11));
            holder.tv_date.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_11));
            holder.tv_workouts_device_name.setTextSize(TypedValue.COMPLEX_UNIT_PX, getIntDimen(R.dimen.sp_8));

            {
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) holder.ll_workouts_item_result.getLayoutParams();
                params2.weight = 1;
                holder.ll_workouts_item_result.setLayoutParams(params2);
                LinearLayout.LayoutParams params3 = (LinearLayout.LayoutParams) holder.ll_workouts_item_type.getLayoutParams();
                params3.weight = 1;
                holder.ll_workouts_item_type.setLayoutParams(params3);
            }
        } else {
        }

        RowerDataBean1 bean1 = rowerDataBean1List.get(position);
        HttpRowerDataBean1 bean = ((HttpRowerDataBean1) (bean1));

        holder.itemView.setOnClickListener(v -> {
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.workouts_item_click, null));
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                onItemClickListener.onItemClickListener(position);
            }, 150);
        });
        holder.iv_delete.setOnClickListener(v -> onItemDeleteListener.onItemDeleteListener(position));
        if (isShowDelete) {
            holder.iv_delete.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tv_date.getLayoutParams();
            params.leftMargin = 0;
            holder.tv_date.setLayoutParams(params);
        } else {
            holder.iv_delete.setVisibility(View.GONE);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.tv_date.getLayoutParams();
            if (ori == Configuration.ORIENTATION_LANDSCAPE) {
                params.leftMargin = getIntDimen(R.dimen.dp_50);
            } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
                params.leftMargin = getIntDimen(R.dimen.dp_10);
            }
            holder.tv_date.setLayoutParams(params);
        }
        if (bean.getNote() != null && !"".equals(bean.getNote())) {
            holder.iv_note.setVisibility(View.VISIBLE);
        } else {
            holder.iv_note.setVisibility(View.GONE);
        }
        holder.tv_date.setText(TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd HH:mm:ss"));

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

        holder.tv_distance.setText(bean.getType());
        holder.tv_time.setText(bean.getResult());

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

    private int ori;

    @Override
    public void setPortLayout() {
        ori = 1;
        notifyDataSetChanged();
    }

    @Override
    public void setLandLayout() {
        ori = 2;
        notifyDataSetChanged();
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

        private ImageView iv_delete;
        private TextView tv_date;
        private TextView tv_distance;
        private ImageView iv_item_device;
        private TextView tv_workouts_device_name;
        private TextView tv_time;
        private ImageView iv_note;
        private LinearLayout ll_item;
        private TextView tv_server_id;

        private LinearLayout ll_workouts_item_date;
        private LinearLayout ll_workouts_item_type;
        private LinearLayout ll_workouts_item_result;

        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            tv_local_id = itemView.findViewById(R.id.tv_local_id);
            iv_item_device = itemView.findViewById(R.id.iv_item_device);
            tv_workouts_device_name = itemView.findViewById(R.id.tv_workouts_device_name);
            tv_time = itemView.findViewById(R.id.tv_time);
            iv_note = itemView.findViewById(R.id.iv_note);
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_server_id = itemView.findViewById(R.id.tv_server_id);

            ll_workouts_item_type = itemView.findViewById(R.id.ll_workouts_item_type);
            ll_workouts_item_result = itemView.findViewById(R.id.ll_workouts_item_result);
            ll_workouts_item_date = itemView.findViewById(R.id.ll_workouts_item_date);
        }
    }

    protected int getIntDimen(int id) {
        return (int) MyApplication.getContext().getResources().getDimension(id);
    }
}
