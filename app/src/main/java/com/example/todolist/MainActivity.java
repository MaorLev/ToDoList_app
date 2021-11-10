package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lvTasks;
    ArrayList<Task> tasks;
    TaskAdapter taskAdapter;
    Task lastSelected;
    Button btNewTask;//עצירה ביצירת דף הוספת משימה
    static final int EDIT_CODE =1;
    static final int ADD_CODE =0;
    BatteryReceiver br;
    IncomingSmsReceiver smsR;
    SoundPool sp;
    int sms;
    boolean soundFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btNewTask = findViewById(R.id.btNewTask);
        btNewTask.setOnClickListener(this);
        lvTasks = findViewById(R.id.lvTasks);
        tasks = new ArrayList<>();
        br = new BatteryReceiver(20);
        smsR = new IncomingSmsReceiver();
        createSoundManager();
//        tasks.add(0,new Task("mmm", "12/12/21",true));
//        tasks.add(0,new Task("ido", "11/06/21",true));
//        tasks.add(0,new Task("hhd", "14/02/19",false));
//        tasks.add(0,new Task("dsc", "",false));

//        Toast.makeText(getApplicationContext(),lastSelected.getContent(),Toast.LENGTH_SHORT).show();
//        Intent intent = getIntent();
//        if (intent.getExtras() != null)
//        {
//            String content = intent.getStringExtra("kback_content");
//            String date = intent.getStringExtra("kback_date");
//            Boolean priority = intent.getBooleanExtra("kback_priority",false);
//            int index = intent.getIntExtra("kback_index",-1);
//            lastSelected = tasks.get(index);
//            lastSelected.setContent(content);
//            lastSelected.setDate(date);
//            lastSelected.setHiPriority(priority);
//        }

        taskAdapter = new TaskAdapter(this,tasks);

        lvTasks.setAdapter(taskAdapter);

        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lastSelected = tasks.get(i);
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                intent.putExtra("key_content", lastSelected.getContent());
                intent.putExtra("key_date",lastSelected.getDate());
                intent.putExtra("key_priority",lastSelected.isHiPriority());
                intent.putExtra("key_index",i);
                startActivityForResult(intent,1);
//                finish();
            }
        });

        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                tasks.remove(position);
                saveTasks();;
                taskAdapter.notifyDataSetChanged();
                return true;
            }
        });
        //אם משהו משתבש להחזיר (אמור לסנכרן בין הנתונים שהשתנו כשיש מעבר דף)
//        taskAdapter.notifyDataSetChanged();
        loadTasks();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Ask for permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this,EditActivity.class);
//        intent.putExtra("key_index",-1);
        startActivityForResult(intent,0);
//        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK)
        {
            String content = intent.getStringExtra("kback_content");
            String date = intent.getStringExtra("kback_date");
            Boolean priority = intent.getBooleanExtra("kback_priority",false);
            int index = intent.getIntExtra("kback_index",-1);
            if(requestCode == 1)
            {

                lastSelected = tasks.get(index);
                lastSelected.setContent(content);
                lastSelected.setDate(date);
                lastSelected.setHiPriority(priority);

            }
            else if(requestCode == 0)
            {
                lastSelected = new Task(content,date,priority);
                tasks.add(0,lastSelected);
            }
            saveTasks();
            taskAdapter.notifyDataSetChanged();//check without this row, if we have a bug
        }

    }


    // save task list
    public void saveTasks()
    {
        String str = "";
        for(Task  t : tasks)
        {
            str += t.getContent() + "##" + t.getDate() + "##" + t.isHiPriority() + "\n";
        }
        saveToFile("tasks.txt", str);


    }

    /**
     * Saves str to filename internal file
     * Returns true if the save is successfull
     * @param fileName
     * @param str
     */
    private boolean saveToFile(String fileName, String str)
    {
        try {
            FileOutputStream fos = openFileOutput(fileName, Context.MODE_PRIVATE);
            try {
                fos.write(str.getBytes());
                fos.close();

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    // load the task list
    public void loadTasks()
    {
        String str = readFromFile("tasks.txt");
        if (str.length() > 0) {
            String[] arr = str.split("\n");
            tasks.clear();
            for (String s : arr) {
                String[] farr = s.split("##");
                // The current task
                Task task = new Task(farr[0], farr[1], Boolean.valueOf(farr[2]));
                tasks.add(task);
            }
            taskAdapter.notifyDataSetChanged();
        }
    }

    public String readFromFile(String fileName)
    {

        StringBuffer retBuf = new StringBuffer();

        try {
            FileInputStream fstream = openFileInput(fileName);
            int i;
            while ((i = fstream.read())!= -1){
                retBuf.append((char)i);
            }
            fstream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return retBuf.toString();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("key_check","on Create");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
                Log.d("key_check","on Destroy");
    }

    @Override
    protected void onResume() {//
        super.onResume();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br,filter);
        IntentFilter filterSms = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsR,filterSms);
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        soundFlag = sp.getBoolean("key_sound",false);
        Log.d("key_check","on resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
        unregisterReceiver(smsR);
        SharedPreferences sp = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("key_sound",soundFlag);
        edit.apply();
        Log.d("key_check","on pause");
    }
    public  class IncomingSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages = null;
            String strMessage = "";

            if (myBundle != null)
            {
                Object[] pdus = (Object[]) myBundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    String format = myBundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    strMessage += messages[i].getMessageBody();

                }
                strMessage = strMessage.trim();
//                if(strMessage.substring(0,4).equalsIgnoreCase("MS0-"))
                if (strMessage.toUpperCase().startsWith("MS0-")) {
                    if (soundFlag)
                    sp.play(sms,1,1,0,0,1);

                    strMessage = strMessage.substring(4);
                    Task t = new Task(strMessage, "", false);
                    tasks.add(0, new Task(strMessage, "", false));
                    taskAdapter.notifyDataSetChanged();
                    }
                //check this how to resolve add field while this app turned off
//                if (runFlag) {
//                    tasks.add(0, new Task(strMessage, "", false));
//                    taskAdapter.notifyDataSetChanged();
//                }
//                else{
//                    tasks = new ArrayList<>();
//                    String str = readFromFile("tasks.txt");
//                    if (str.length() > 0) {
//                        String[] arr = str.split("\n");
//                        for (String s : arr) {
//                            String[] farr = s.split("##");
//                            // The current task
//                            Task task = new Task(farr[0], farr[1], Boolean.valueOf(farr[2]));
//                            tasks.add(task);
//                        }
//                        tasks.add(0, new Task(strMessage, "", false));
//                        saveTasks();
//                    }


                }

        }

    }
    public  void createSoundManager()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
        sp = new SoundPool.Builder().setMaxStreams(20).build();
        }
        else
            sp = new SoundPool(20, AudioManager.STREAM_MUSIC,1);
        sms = sp.load(this,R.raw.door,1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        MenuItem mnuSound = menu.findItem(R.id.mnuSound);
        if (soundFlag)
            mnuSound.setTitle("Sound On");
        else
            mnuSound.setTitle("Sound Off");
        Log.d("key_check","menu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.mnuDeleteAll:
               tasks.clear();
               taskAdapter.notifyDataSetChanged();
               saveTasks();
                break;
            case R.id.mnuExit:
                finishAffinity();
                break;
            case R.id.mnuSound:
                soundFlag = !soundFlag;
                if (soundFlag)
                    item.setTitle("sound on");
                else
                    item.setTitle("sound off");
                break;

        }
        return true;
    }


}