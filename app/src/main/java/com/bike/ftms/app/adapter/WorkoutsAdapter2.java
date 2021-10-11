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
import com.bike.ftms.app.bean.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import org.w3c.dom.Text;

import java.text.BreakIterator;
import java.util.List;

import butterknife.BindView;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsAdapter2 extends RecyclerView.Adapter<WorkoutsAdapter2.WorkoutsViewHolder> {
    private List<RowerDataBean2> rowerDataBean2List;

    public WorkoutsAdapter2(List<RowerDataBean2> rowerDataBean2List) {
        this.rowerDataBean2List = rowerDataBean2List;
    }

    @NonNull
    @Override
    public WorkoutsAdapter2.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts2, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsAdapter2.WorkoutsViewHolder holder, int position) {
        RowerDataBean2 bean = rowerDataBean2List.get(position);

        switch (bean.getMode()) {
            case MyConstant.NORMAL:
            case MyConstant.GOAL_TIME:
            case MyConstant.GOAL_DISTANCE:
            case MyConstant.GOAL_CALORIES:
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                break;
            case MyConstant.INTERVAL_TIME:
            case MyConstant.INTERVAL_DISTANCE:
            case MyConstant.INTERVAL_CALORIES:
                holder.tvInfoMeters.setText(bean.getSetDistance() + "M");
                break;
            default:
                break;
        }

        holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));

        holder.tvInfoFiveHundred.setText(TimeStringUtil.getSToMinSecValue(bean.getFive_hundred()));
        holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));

        holder.tvInfoWatts.setText(String.valueOf(bean.getWatts()));
        holder.tvInfoCalHr.setText(String.valueOf(bean.getCalories_hr()));
        holder.tvInfoSm.setText(String.valueOf(bean.getSm()));
    }

    @Override
    public int getItemCount() {
        return rowerDataBean2List.size();
    }

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInfoTime;
        public TextView tvInfoMeters;
        public TextView tvInfoFiveHundred;
        public TextView tvInfoCals;
        public TextView tvInfoWatts;
        public TextView tvInfoCalHr;
        public TextView tvInfoSm;

        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfoTime = itemView.findViewById(R.id.tv_info_time);
            tvInfoMeters = itemView.findViewById(R.id.tv_info_meters);
            tvInfoFiveHundred = itemView.findViewById(R.id.tv_info_five_hundred);
            tvInfoCals = itemView.findViewById(R.id.tv_info_cals);
            tvInfoWatts = itemView.findViewById(R.id.tv_info_watts);
            tvInfoCalHr = itemView.findViewById(R.id.tv_info_cal_hr);
            tvInfoSm = itemView.findViewById(R.id.tv_info_sm);
        }
    }
}
