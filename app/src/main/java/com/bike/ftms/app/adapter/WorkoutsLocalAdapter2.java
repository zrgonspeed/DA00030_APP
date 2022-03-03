package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.rundata.HttpRowerDataBean1;
import com.bike.ftms.app.bean.rundata.RowerDataBean2;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.TimeStringUtil;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsLocalAdapter2 extends RecyclerView.Adapter<WorkoutsLocalAdapter2.WorkoutsViewHolder> {
    private List<RowerDataBean2> rowerDataBean2List;

    public WorkoutsLocalAdapter2(List<RowerDataBean2> rowerDataBean2List) {
        this.rowerDataBean2List = rowerDataBean2List;
    }

    @NonNull
    @Override
    public WorkoutsLocalAdapter2.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts2, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsLocalAdapter2.WorkoutsViewHolder holder, int position) {
        RowerDataBean2 bean = rowerDataBean2List.get(position);
        HttpRowerDataBean1 rowerDataBean1 = (HttpRowerDataBean1) bean.getRowerDataBean1();

        // 服务器的数据
        if (rowerDataBean1.getStatus() == 1) {
            holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
            holder.tv_info_meters.setText(bean.getDistance() + "M");
            holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
            holder.tv_info_interval.setText(" ");

            holder.tv_info_interval.setText(String.valueOf(position));

            holder.tv_info_five_hundred.setText(TimeStringUtil.getSToMinSecValue(bean.getAve_five_hundred()));
            holder.tv_info_watts.setText(String.valueOf(bean.getAve_watts()));
            holder.tv_info_cal_hr.setText(String.valueOf(bean.getCalories_hr()));
            holder.tv_info_sm.setText(String.valueOf(bean.getSm()));

            if (position == 0) {
                holder.tv_info_interval.setText(" ");
            }
        } else {
            switch (bean.getRunMode()) {
                case MyConstant.NORMAL:
                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_interval.setText(" ");
                    break;
                case MyConstant.GOAL_TIME:
                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_interval.setText(String.valueOf(bean.getRunInterval() + 1));
                    break;
                case MyConstant.GOAL_DISTANCE:
                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_interval.setText(String.valueOf(bean.getRunInterval() + 1));
                    break;
                case MyConstant.GOAL_CALORIES:
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_interval.setText(String.valueOf(bean.getRunInterval() + 1));
                    break;
                case MyConstant.INTERVAL_TIME:
                    if (position == rowerDataBean2List.size() - 1) {
                        if (bean.getSetIntervalTime() == bean.getTime()) {
                            holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime()));
                        } else {
                            holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime() - bean.getTime()));
                        }
                    } else {
                        holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getSetIntervalTime()));
                    }


                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_interval.setText(String.valueOf(bean.getInterval()));
                    break;
                case MyConstant.INTERVAL_DISTANCE:
//                if (position == rowerDataBean2List.size() - 1) {
//                    if (bean.getSetIntervalDistance() == bean.getDistance()) {
//                        holder.tvInfoMeters.setText(bean.getSetIntervalDistance() + "M");
//                    } else {
//                        holder.tvInfoMeters.setText(bean.getSetIntervalDistance() - bean.getDistance() + "M");
//                    }
//                } else {
//                    holder.tvInfoMeters.setText(bean.getSetIntervalDistance() + "M");
//                }

                    holder.tv_info_meters.setText(bean.getDistance() + "M");

                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    holder.tv_info_interval.setText(String.valueOf(bean.getInterval()));
                    break;
                case MyConstant.INTERVAL_CALORIES:
                    if (position == rowerDataBean2List.size() - 1) {
                        holder.tv_info_cals.setText(String.valueOf(bean.getSetIntervalCalorie() - bean.getCalorie()));
                    } else {
                        holder.tv_info_cals.setText(String.valueOf(bean.getCalorie()));
                    }

                    holder.tv_info_meters.setText(bean.getDistance() + "M");
                    holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(bean.getTime()));
                    holder.tv_info_interval.setText(String.valueOf(bean.getInterval()));
                    break;
                default:
                    break;
            }
            if (bean.getInterval() == -1) {
                holder.tv_info_interval.setText(" ");
            }
            holder.tv_info_watts.setText(String.valueOf(bean.getAve_watts()));
            holder.tv_info_cal_hr.setText(String.valueOf(bean.getCalories_hr()));

            switch (rowerDataBean1.getCategoryType()) {
                case MyConstant.CATEGORY_BOAT: {
                    holder.tv_info_sm.setVisibility(View.VISIBLE);
                    holder.tv_info_five_hundred.setVisibility(View.VISIBLE);
                    holder.tv_info_inst_level.setVisibility(View.GONE);
                    holder.tv_info_ave_one_km.setVisibility(View.GONE);
                }
                break;
                case MyConstant.CATEGORY_BIKE: {
                    holder.tv_info_inst_level.setVisibility(View.VISIBLE);
                    holder.tv_info_ave_one_km.setVisibility(View.VISIBLE);
                    holder.tv_info_sm.setVisibility(View.GONE);
                    holder.tv_info_five_hundred.setVisibility(View.GONE);
                }
                break;
            }

            holder.tv_info_sm.setText(String.valueOf(bean.getSm()));
            holder.tv_info_five_hundred.setText(TimeStringUtil.getSToMinSecValue(bean.getAve_five_hundred()));

            holder.tv_info_inst_level.setText(String.valueOf(bean.getLevel()));
            holder.tv_info_ave_one_km.setText(TimeStringUtil.getSToMinSecValue(bean.getAveOneKmTime()));
        }
    }

    @Override
    public int getItemCount() {
        return rowerDataBean2List.size();
    }

    public static class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_info_time;
        public TextView tv_info_meters;
        public TextView tv_info_cals;
        public TextView tv_info_watts;
        public TextView tv_info_cal_hr;
        public TextView tv_info_interval;

        public TextView tv_info_five_hundred;
        public TextView tv_info_sm;

        public TextView tv_info_inst_level;
        public TextView tv_info_ave_one_km;


        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_info_time = itemView.findViewById(R.id.tv_info_time);
            tv_info_meters = itemView.findViewById(R.id.tv_info_meters);
            tv_info_cals = itemView.findViewById(R.id.tv_info_cals);
            tv_info_watts = itemView.findViewById(R.id.tv_info_watts);
            tv_info_cal_hr = itemView.findViewById(R.id.tv_info_cal_hr);
            tv_info_interval = itemView.findViewById(R.id.tv_info_interval);

            tv_info_sm = itemView.findViewById(R.id.tv_info_sm);
            tv_info_five_hundred = itemView.findViewById(R.id.tv_info_five_hundred);

            tv_info_inst_level = itemView.findViewById(R.id.tv_info_inst_level);
            tv_info_ave_one_km = itemView.findViewById(R.id.tv_info_ave_one_km);
        }
    }
}
