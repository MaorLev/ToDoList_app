package com.example.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

public class BatteryReceiver extends BroadcastReceiver {

    private int criticalLevel;
    private boolean entried;
    public  BatteryReceiver(int criticalLevel)
    {
        entried = false;
        this.criticalLevel = criticalLevel;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
        
        if(batteryLevel <= criticalLevel && entried == false){
            Toast.makeText(context, "Low Battery:" + batteryLevel + "%" , Toast.LENGTH_LONG).show();
            entried = true;
        }
        else if (batteryLevel > 20)
            entried = false;

    }
}
