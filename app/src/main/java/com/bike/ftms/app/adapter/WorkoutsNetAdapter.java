package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.RowerDataBean1;
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.bean.RunDataResult;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsNetAdapter extends RecyclerView.Adapter<WorkoutsNetAdapter.WorkoutsViewHolder> {
    private OnItemClickListener onItemClickListener;
    private OnItemDeleteListener onItemDeleteListener;
    private boolean isShowDelete = false;
    private List<RunDataResult> runDataResults;

    public WorkoutsNetAdapter(List<RunDataResult> runDataResults) {
        this.runDataResults = runDataResults;
    }

    @NonNull
    @Override
    public WorkoutsNetAdapter.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsNetAdapter.WorkoutsViewHolder holder, int position) {
        RunDataResult bean = runDataResults.get(position);
        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClickListener(position));
        holder.ivDelete.setOnClickListener(v -> onItemDeleteListener.onItemDeleteListener(position));
        if (isShowDelete) {
            holder.ivDelete.setVisibility(View.VISIBLE);
        } else {
            holder.ivDelete.setVisibility(View.GONE);
        }
        if (bean.getRemarks() != null && !"".equals(bean.getRemarks())) {
            holder.ivNote.setVisibility(View.VISIBLE);
        } else {
            holder.ivNote.setVisibility(View.GONE);
        }

        holder.tvDate.setText(bean.getDate());
        holder.tvDistance.setText(bean.getType());
        holder.tvTime.setText(bean.getResult());

        /*switch (bean.getRunMode()) {
            case MyConstant.NORMAL:
                holder.tvDistance.setText(bean.getDistance() + "M");
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_TIME:
                holder.tvDistance.setText(TimeStringUtil.getSToMinSecValue(bean.getSetGoalTime()));
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.GOAL_DISTANCE:
                holder.tvDistance.setText(bean.getSetGoalDistance() + "M");
                holder.tvTime.setText(TimeStringUtil.getSToMinSecValue(bean.getTime()));
                break;
            case MyConstant.GOAL_CALORIES:
                holder.tvDistance.setText(bean.getSetGoalCalorie() + "C");
                holder.tvTime.setText(bean.getDistance() + "M");
                break;
            case MyConstant.INTERVAL_TIME:
                holder.tvDistance.setText((bean.getInterval() + "x:" + bean.getSetIntervalTime() + "/:" + bean.getReset_time() + "R"));
                // 总距离
            {
                List<RowerDataBean2> list = bean.getList();
                if (list.size() > 1) {
                    long totalMeter = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalMeter += bean2.getDistance();
                    }
                    holder.tvTime.setText(totalMeter + "M");
                } else {
                    holder.tvTime.setText(bean.getDistance() + "M");
                }
            }
            break;
            case MyConstant.INTERVAL_DISTANCE:
                holder.tvDistance.setText((bean.getInterval() + "x" + bean.getSetIntervalDistance() + "M" + "/:" + bean.getReset_time() + "R"));
            {
                // 总时间
                List<RowerDataBean2> list = bean.getList();
                if (list.size() > 1) {
                    long totalTime = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalTime += bean2.getTime();
                    }
                    holder.tvTime.setText(TimeStringUtil.getSToMinSecValue(totalTime));
                } else {
                    holder.tvTime.setText(TimeStringUtil.getSToMinSecValue(bean.getTime()));
                }
            }
            break;
            case MyConstant.INTERVAL_CALORIES:
                holder.tvDistance.setText((bean.getInterval() + "x" + bean.getSetIntervalCalorie() + "C" + "/:" + bean.getReset_time() + "R"));
                // 总距离
            {
                List<RowerDataBean2> list = bean.getList();
                if (list.size() > 1) {
                    long totalMeter = 0;
                    for (RowerDataBean2 bean2 : list) {
                        totalMeter += bean2.getDistance();
                    }
                    holder.tvTime.setText(totalMeter + "M");
                } else {
                    holder.tvTime.setText(bean.getDistance() + "M");
                }
            }
            break;
            default:
                break;
        }*/
    }

    @Override
    public int getItemCount() {
        return runDataResults.size();
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
