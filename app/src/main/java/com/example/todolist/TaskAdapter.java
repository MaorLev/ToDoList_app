package com.example.todolist;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class TaskAdapter extends BaseAdapter {
    ArrayList<Task> objects;
    Context context;

    public TaskAdapter(Context context, ArrayList<Task> objects){
        this.objects = objects;
        this.context = context;
    }
    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.task_item,parent,false);
        }

        TextView tvTaskText = view.findViewById(R.id.tvTaskText);
        TextView tvDeadLine = view.findViewById(R.id.tvDeadLine);
        TextView tvPriority = view.findViewById(R.id.tvPriority);

        Task t = objects.get(i);
        tvTaskText.setText(t.getContent());
        tvDeadLine.setText((t.getDate()));

        if (t.isHiPriority()){
            tvPriority.setText("!");
            tvPriority.setBackgroundColor(Color.RED);
        }
        else{
            tvPriority.setText("");
            tvPriority.setBackgroundColor(Color.parseColor("#50A4FA"));
        }
        return  view;


    }
}
