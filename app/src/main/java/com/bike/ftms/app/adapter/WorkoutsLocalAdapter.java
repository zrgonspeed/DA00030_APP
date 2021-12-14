package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.fragment.workout.WorkoutsLocalFragment;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean2;
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


//        if (vector.get(position)) {
//            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.workouts_item_uploaded));
//        } else {
//            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));
//        }

        if (bean.getStatus() == 1) {
            holder.tv_server_id.setText(((HttpRowerDataBean1) bean).getWorkout_id() + "");
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.workouts_item_uploaded));
        } else {
            holder.tv_server_id.setText(bean.getId() + "");
            holder.ll_item.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.white));
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
        private ImageView ivDelete;
        private TextView tvDate;
        private TextView tvDistance;
        private TextView tvTime;
        private ImageView ivNote;
        private LinearLayout ll_item;
        private TextView tv_server_id;

        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDelete = itemView.findViewById(R.id.iv_delete);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvTime = itemView.findViewById(R.id.tv_time);
            ivNote = itemView.findViewById(R.id.iv_note);
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_server_id = itemView.findViewById(R.id.tv_server_id);
        }
    }
}
