package com.bike.ftms.app.adapter;

import android.content.res.Configuration;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bike.ftms.app.R;
import com.bike.ftms.app.activity.OnOrientationChanged;
import com.bike.ftms.app.base.MyApplication;
import com.bike.ftms.app.ble.bean.rundata.view.RunInfoItem;
import com.bike.ftms.app.ble.bean.rundata.view.RunInfoVO;
import com.bike.ftms.app.common.MyConstant;

import java.util.List;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsLocalAdapter2 extends RecyclerView.Adapter<WorkoutsLocalAdapter2.WorkoutsViewHolder> implements OnOrientationChanged {
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

    private void setTextSize(WorkoutsViewHolder holder, int size) {
        holder.tv_info_time.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_ave_one_km.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_cal_hr.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_cals.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_five_hundred.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_inst_level.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_interval.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_meters.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_sm.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        holder.tv_info_watts.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsLocalAdapter2.WorkoutsViewHolder holder, int position) {
        RunInfoItem item = items.get(position);
        // HttpRowerDataBean1 rowerDataBean1 = (HttpRowerDataBean1) item.getRowerDataBean1();

        if (ori == Configuration.ORIENTATION_LANDSCAPE) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.tv_info_time.getLayoutParams();
            params.matchConstraintPercentWidth = 0.12f;
            holder.tv_info_time.setLayoutParams(params);

            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) holder.tv_info_meters.getLayoutParams();
            params2.horizontalBias = 0.22f;
            params2.matchConstraintPercentWidth = 0.14f;
            holder.tv_info_meters.setLayoutParams(params2);

            ConstraintLayout.LayoutParams params3 = (ConstraintLayout.LayoutParams) holder.tv_info_cals.getLayoutParams();
            params3.horizontalBias = 0.37f;
            holder.tv_info_cals.setLayoutParams(params3);

            ConstraintLayout.LayoutParams params4 = (ConstraintLayout.LayoutParams) holder.tv_info_five_hundred.getLayoutParams();
            params4.horizontalBias = 0.51f;
            holder.tv_info_five_hundred.setLayoutParams(params4);

            ConstraintLayout.LayoutParams params5 = (ConstraintLayout.LayoutParams) holder.tv_info_sm.getLayoutParams();
            params5.horizontalBias = 0.67f;
            holder.tv_info_sm.setLayoutParams(params5);

            ConstraintLayout.LayoutParams params6 = (ConstraintLayout.LayoutParams) holder.tv_info_ave_one_km.getLayoutParams();
            params6.horizontalBias = 0.51f;
            holder.tv_info_ave_one_km.setLayoutParams(params6);

            ConstraintLayout.LayoutParams params7 = (ConstraintLayout.LayoutParams) holder.tv_info_inst_level.getLayoutParams();
            params7.horizontalBias = 0.67f;
            holder.tv_info_inst_level.setLayoutParams(params7);

            setTextSize(holder, getIntDimen(R.dimen.sp_13));

        } else if (ori == Configuration.ORIENTATION_PORTRAIT) {
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder.tv_info_time.getLayoutParams();
            params.matchConstraintPercentWidth = 0.18f;
            holder.tv_info_time.setLayoutParams(params);

            ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) holder.tv_info_meters.getLayoutParams();
            params2.horizontalBias = 0.28f;
            params2.matchConstraintPercentWidth = 0.14f;
            holder.tv_info_meters.setLayoutParams(params2);

            ConstraintLayout.LayoutParams params3 = (ConstraintLayout.LayoutParams) holder.tv_info_cals.getLayoutParams();
            params3.horizontalBias = 0.42f;
            holder.tv_info_cals.setLayoutParams(params3);


            ConstraintLayout.LayoutParams params4 = (ConstraintLayout.LayoutParams) holder.tv_info_five_hundred.getLayoutParams();
            params4.horizontalBias = 0.55f;
            holder.tv_info_five_hundred.setLayoutParams(params4);

            ConstraintLayout.LayoutParams params5 = (ConstraintLayout.LayoutParams) holder.tv_info_sm.getLayoutParams();
            params5.horizontalBias = 0.68f;
            holder.tv_info_sm.setLayoutParams(params5);

            ConstraintLayout.LayoutParams params6 = (ConstraintLayout.LayoutParams) holder.tv_info_ave_one_km.getLayoutParams();
            params6.horizontalBias = 0.55f;
            holder.tv_info_ave_one_km.setLayoutParams(params6);

            ConstraintLayout.LayoutParams params7 = (ConstraintLayout.LayoutParams) holder.tv_info_inst_level.getLayoutParams();
            params7.horizontalBias = 0.68f;
            holder.tv_info_inst_level.setLayoutParams(params7);

            // 字体
            setTextSize(holder, getIntDimen(R.dimen.sp_12));
        }


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
                case MyConstant.CATEGORY_SKI: {
                    holder.tv_info_sm.setVisibility(View.VISIBLE);
                    holder.tv_info_five_hundred.setVisibility(View.VISIBLE);
                    holder.tv_info_inst_level.setVisibility(View.GONE);
                    holder.tv_info_ave_one_km.setVisibility(View.GONE);
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

    protected int getIntDimen(int id) {
        return (int) MyApplication.getContext().getResources().getDimension(id);
    }

    public void setOri(int ori) {
        this.ori = ori;
        notifyDataSetChanged();
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
