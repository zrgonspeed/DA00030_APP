package com.bike.ftms.app.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @Description
 * @Author YYH
 * @Date 2021/4/12
 */
public class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsAdapter.WorkoutsViewHolder> {
    @NonNull
    @Override
    public WorkoutsAdapter.WorkoutsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutsAdapter.WorkoutsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        public WorkoutsViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
