package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.rundata.RunDataInfoDTO;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsNetAdapter2 extends RecyclerView.Adapter<WorkoutsNetAdapter2.WorkoutsViewHolder> {
    private List<RunDataInfoDTO> runDataInfoDTOS;

    public WorkoutsNetAdapter2(List<RunDataInfoDTO> runDataInfoDTOS) {
        this.runDataInfoDTOS = runDataInfoDTOS;
    }

    @NonNull
    @Override
    public WorkoutsNetAdapter2.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts2, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsNetAdapter2.WorkoutsViewHolder holder, int position) {
        RunDataInfoDTO bean = runDataInfoDTOS.get(position);

        /*switch (bean.getRunMode()) {
            case MyConstant.NORMAL:
                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoInterval.setText(" ");
                break;
            case MyConstant.GOAL_TIME:
                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoInterval.setText(String.valueOf(bean.getRunInterval() + 1));
                break;
            case MyConstant.GOAL_DISTANCE:
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoInterval.setText(String.valueOf(bean.getRunInterval() + 1));
                break;
            case MyConstant.GOAL_CALORIES:
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoInterval.setText(String.valueOf(bean.getRunInterval() + 1));
                break;
            case MyConstant.INTERVAL_TIME:
                if (position == rowerDataBean2List.size() - 1) {
                    if (bean.getSetIntervalTime() == bean.getTime()) {
                        holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime()));
                    } else {
                        holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime() - bean.getTime()));
                    }
                } else {
                    holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime()));
                }
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoInterval.setText(String.valueOf(bean.getInterval()));
                break;
            case MyConstant.INTERVAL_DISTANCE:
                if (position == rowerDataBean2List.size() - 1) {
                    if (bean.getSetIntervalDistance() == bean.getDistance()) {
                        holder.tvInfoMeters.setText(bean.getSetIntervalDistance() + "M");
                    } else {
                        holder.tvInfoMeters.setText(bean.getSetIntervalDistance() - bean.getDistance() + "M");
                    }
                } else {
                    holder.tvInfoMeters.setText(bean.getSetIntervalDistance() + "M");
                }

                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoCals.setText(String.valueOf(bean.getCalorie()));
                holder.tvInfoInterval.setText(String.valueOf(bean.getInterval()));
                break;
            case MyConstant.INTERVAL_CALORIES:
                if (position == rowerDataBean2List.size() - 1) {
                    if (bean.getSetIntervalDistance() == bean.getDistance()) {
                        holder.tvInfoCals.setText(String.valueOf(bean.getSetIntervalCalorie()));
                    } else {
                        holder.tvInfoCals.setText(String.valueOf(bean.getSetIntervalCalorie() - bean.getCalorie()));
                    }
                } else {
                    holder.tvInfoCals.setText(String.valueOf(bean.getSetIntervalCalorie()));
                }
                holder.tvInfoMeters.setText(bean.getDistance() + "M");
                holder.tvInfoTime.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                holder.tvInfoInterval.setText(String.valueOf(bean.getInterval()));
                break;
            default:
                break;
        }*/

//        holder.tvInfoFiveHundred.setText(TimeStringUtil.getSToMinSecValue(bean.getFive_hundred()));
//        holder.tvInfoWatts.setText(String.valueOf(bean.getWatts()));
//        holder.tvInfoCalHr.setText(String.valueOf(bean.getCalories_hr()));
//        holder.tvInfoSm.setText(String.valueOf(bean.getSm()));
//
//        if (bean.getInterval() == -1) {
//            holder.tvInfoInterval.setText(" ");
//        }
    }

    @Override
    public int getItemCount() {
        return runDataInfoDTOS.size();
    }

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        public TextView tvInfoTime;
        public TextView tvInfoMeters;
        public TextView tvInfoFiveHundred;
        public TextView tvInfoCals;
        public TextView tvInfoWatts;
        public TextView tvInfoCalHr;
        public TextView tvInfoSm;
        public TextView tvInfoInterval;


        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInfoTime = itemView.findViewById(R.id.tv_info_time);
            tvInfoMeters = itemView.findViewById(R.id.tv_info_meters);
            tvInfoFiveHundred = itemView.findViewById(R.id.tv_info_five_hundred);
            tvInfoCals = itemView.findViewById(R.id.tv_info_cals);
            tvInfoWatts = itemView.findViewById(R.id.tv_info_watts);
            tvInfoCalHr = itemView.findViewById(R.id.tv_info_cal_hr);
            tvInfoSm = itemView.findViewById(R.id.tv_info_sm);
            tvInfoInterval = itemView.findViewById(R.id.tv_info_interval);
        }
    }
}
