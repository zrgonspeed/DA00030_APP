package com.bike.ftms.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.bean.rundata.view.RunInfoItem;
import com.bike.ftms.app.bean.rundata.view.RunInfoVO;
import com.bike.ftms.app.common.MyConstant;
import com.bike.ftms.app.utils.GsonUtil;
import com.bike.ftms.app.utils.Logger;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsLocalAdapter2 extends RecyclerView.Adapter<WorkoutsLocalAdapter2.WorkoutsViewHolder> {
    private List<RunInfoItem> items;
    private RunInfoVO runInfoVO;

    public WorkoutsLocalAdapter2(RunInfoVO runInfoVO) {
        this.runInfoVO = runInfoVO;
        this.items = runInfoVO.getItems();
    }

    @NonNull
    @Override
    public WorkoutsLocalAdapter2.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workouts2, parent, false);
        return new WorkoutsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsLocalAdapter2.WorkoutsViewHolder holder, int position) {
        RunInfoItem item = items.get(position);
        // HttpRowerDataBean1 rowerDataBean1 = (HttpRowerDataBean1) item.getRowerDataBean1();

        // 服务器的数据
        if (false) {
            /*holder.tv_info_time.setText(TimeStringUtil.getSToHourMinSecValue(item.getTime()));
            holder.tv_info_meters.setText(item.getDistance() + "M");
            holder.tv_info_cals.setText(String.valueOf(item.getCalorie()));
            holder.tv_info_interval.setText(" ");

            holder.tv_info_interval.setText(String.valueOf(position));

            holder.tv_info_five_hundred.setText(TimeStringUtil.getSToMinSecValue(item.getAve_five_hundred()));
            holder.tv_info_watts.setText(String.valueOf(item.getAve_watts()));
            holder.tv_info_cal_hr.setText(String.valueOf(item.getCalories_hr()));
            holder.tv_info_sm.setText(String.valueOf(item.getSm()));

            if (position == 0) {
                holder.tv_info_interval.setText(" ");
            }*/
        } else {
            holder.tv_info_time.setText(item.getTime());
            holder.tv_info_meters.setText(item.getMeters());
            holder.tv_info_cals.setText(item.getCals());
            if ("-1".equals(item.getInterval()) || runInfoVO.getRunModeNum() == MyConstant.NORMAL) {
                holder.tv_info_interval.setText("");
            } else {
                holder.tv_info_interval.setText(item.getInterval());
            }

            holder.tv_info_watts.setText(item.getAve_watts());
            holder.tv_info_cal_hr.setText(item.getCal_hr());

            switch (runInfoVO.getCategoryType()) {
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

            holder.tv_info_sm.setText(item.getSm());
            holder.tv_info_five_hundred.setText(item.getAve_500());

            holder.tv_info_inst_level.setText(item.getLevel());
            holder.tv_info_ave_one_km.setText(item.getAve_one_km());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
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
