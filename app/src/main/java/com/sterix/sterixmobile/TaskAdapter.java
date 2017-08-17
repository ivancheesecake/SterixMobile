package com.sterix.sterixmobile;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{

    private List<Task> tasks;
    private Context context;

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public TextView taskTextView;
        public TextView statusTextView;
        public Button statusButton;


        public TaskViewHolder(View view) {
            super(view);
            timeTextView = (TextView) view.findViewById(R.id.tasks_time);
            taskTextView = (TextView) view.findViewById(R.id.tasks_task);
            statusTextView = (TextView) view.findViewById(R.id.tasks_status);
            statusButton = (Button) view.findViewById(R.id.tasks_status_button);

        }
    }

    public TaskAdapter(List<Task> tasks, Context context) {
        this.tasks = tasks;
        this.context = context;
    }


    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_row, parent, false);

        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task t = tasks.get(position);
        holder.timeTextView.setText(t.getTime());
        holder.taskTextView.setText(t.getTask());
        holder.taskTextView.setTag(t);
        holder.statusTextView.setText(t.getStatus()); //remove this later
        holder.statusButton.setTag(t.getId());

        if(t.getStatus().equals("0"))
            holder.statusButton.setBackgroundColor(this.context.getResources().getColor(R.color.red));
        else if(t.getStatus().equals("1"))
            holder.statusButton.setBackgroundColor(this.context.getResources().getColor(R.color.yellow));
        else if(t.getStatus().equals("2"))
            holder.statusButton.setBackgroundColor(this.context.getResources().getColor(R.color.green));

    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }



}
