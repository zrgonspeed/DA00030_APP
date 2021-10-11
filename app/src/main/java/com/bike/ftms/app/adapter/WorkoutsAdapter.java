package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.Logger;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemDeleteListener onItemDeleteListener;
    private boolean isShowDelete = false;
    private List<RowerDataBean> rowerDataBeanList;

    public WorkoutsAdapter(List<RowerDataBean> rowerDataBeanList) {
        this.rowerDataBeanList = rowerDataBeanList;
    }

    @NonNull
    @Override
    public WorkoutsAdapter.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsAdapter.WorkoutsViewHolder holder, int position) {
        RowerDataBean bean = rowerDataBeanList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClickListener(position);
            }
        });
        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDeleteListener.onItemDeleteListener(position);
            }
        });
        if (isShowDelete) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }
        if (bean.getNote() != null && !"".equals(bean.getNote())) {
            holder.ivNote.setVisibility(View.VISIBLE);
        } else {
            holder.ivNote.setVisibility(View.GONE);
        }
        holder.tvDate.setText(TimeStringUtil.getDate2String(bean.getDate(), "yyyy-MM-dd HH:mm:ss"));

        switch (bean.getMode()) {
            case MyConstant.NORMAL:
                holder.tvDistance.setText(bean.getDistance() + "M");
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                holder.tvDistance.setText(TimeStringUtil.getSToMinSecValue(bean.getSetTargetTime()));
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_DISTANCE:
                holder.tvDistance.setText(bean.getSetTargetDistance() + "M");
                holder.tvTime.setText(TimeStringUtil.getSToMinSecValue(bean.getTime()));
                break;
            case MyConstant.GOAL_CALORIES:
                holder.tvDistance.setText(bean.getSetTargetCalorie() + "C");
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.INTERVAL_TIME:
                holder.tvDistance.setText((bean.getInterval() + "x:" + bean.getSetTime() + "/:" + bean.getReset_time() + "R"));
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.INTERVAL_DISTANCE:
                holder.tvDistance.setText((bean.getInterval() + "x" + bean.getSetDistance() + "M" + "/:" + bean.getReset_time() + "R"));
                holder.tvTime.setText(TimeStringUtil.getSToMinSecValue(bean.getTime()));
                break;
            case MyConstant.INTERVAL_CALORIES:
                holder.tvDistance.setText((bean.getInterval() + "x" + bean.getSetCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            default:
                break;
        }

/*        if (bean.getSetDistance() == 0) {
            // 表示不是间歇运动
            holder.tvDistance.setText(bean.getDistance() + "M");
        } else {
//            holder.tvDistance.setText((bean.getSetDistance() - bean.getDistance()) + "M");
            holder.tvDistance.setText((bean.getInterval() + "x" + bean.getSetCalorie() + "c" + "/:" + bean.getReset_time()));
        }*/
/*        if (bean.getSetTime() == 0) {
            holder.tvTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
        } else {
            holder.tvTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetTime() - bean.getTime()));
        }*/
    }

    @Override
    public int getItemCount() {
        return rowerDataBeanList.size();
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
        private ImageView ivDelete;
        private TextView tvDate;
        private TextView tvDistance;
        private TextView tvTime;
        private ImageView ivNote;

        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivNote = itemView.findViewById(R.id.iv_note);
        }
    }
}
