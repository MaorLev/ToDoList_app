package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Calendar;

public class EditActivity extends AppCompatActivity implements CheckBox.OnCheckedChangeListener, View.OnClickListener {
    Button btDeleteTask, btDeadLine,btCancel,btUpdate;
    EditText etTaskText;
    CheckBox chDeadLine,chHiPriority;
int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        btDeleteTask = findViewById(R.id.btDeleteTask);

        etTaskText = findViewById(R.id.etTaskText);
        btDeadLine = findViewById(R.id.btDeadLine);
        btCancel = findViewById(R.id.btCancel);
        btUpdate = findViewById(R.id.btUpdate);
        chDeadLine = findViewById(R.id.chDeadLine);
        chHiPriority = findViewById(R.id.chHiPriority);

        chDeadLine.setOnCheckedChangeListener(this);
        chHiPriority.setOnCheckedChangeListener(this);

        btDeadLine.setOnClickListener(this);
        btCancel.setOnClickListener(this);
        btUpdate.setOnClickListener(this);

        String date = "";
        String content;
        Boolean priority;
        Intent intent = getIntent();
        if(intent.getExtras()!=null )
        {
            index = intent.getIntExtra("key_index",0);
//            if(index != -1)
//            {
//
//            }
            content = intent.getStringExtra("key_content");
            date += intent.getStringExtra("key_date");
            priority = intent.getBooleanExtra("key_priority",false);

            etTaskText.setText(content);
            chHiPriority.setChecked(priority);
        }
        if (date != "")
        {
            chDeadLine.setChecked(true);
            btDeadLine.setVisibility(View.VISIBLE);
            btDeadLine.setText(date);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view == chDeadLine)
        {
            if (isChecked)
            {
                btDeadLine.setVisibility(View.VISIBLE);
            }
            else
                {
                    btDeadLine.setVisibility(View.INVISIBLE);
                    btDeadLine.setText("");
                }
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btDeadLine)
        {
            Calendar calander = Calendar.getInstance();
            int year = calander.get(calander.YEAR);
            int month = calander.get(calander.MONTH);
            int day = calander.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(this ,new SetDate() ,year,month,day);
            picker.show();
        }
        else if(v == btCancel)
        {
//            Intent intent = new Intent(EditActivity.this, MainActivity.class);
//            startActivity(intent);
            setResult(RESULT_CANCELED,null);
            finish();
        }
        else if(v == btUpdate)
        {
            Intent intent=new Intent(EditActivity.this,MainActivity.class);
            intent.putExtra("kback_content",etTaskText.getText().toString());
            if (!chDeadLine.isChecked())
                intent.putExtra("kback_date","");
            else
                intent.putExtra("kback_date",btDeadLine.getText().toString());
            intent.putExtra("kback_priority",chHiPriority.isChecked());
            intent.putExtra("kback_index",index);
            setResult(RESULT_OK,intent);
            finish();
        }

    }
    private class SetDate implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String str = dayOfMonth + "/" + (month +1) + "/" + year;
            btDeadLine.setText(str);
        }
    }
}