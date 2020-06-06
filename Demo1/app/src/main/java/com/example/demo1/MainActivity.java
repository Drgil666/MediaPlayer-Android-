package com.example.demo1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity
{
    private Button register_button;
    private Button log_in_button;
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        register_button = (Button)findViewById(R.id.register_button);
        log_in_button = (Button)findViewById(R.id.log_in_button);
        register_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                Intent intent1 = new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
            }
        });
        log_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v)
            {
                Intent intent2=new Intent(MainActivity.this,Main4Activity.class);
                startActivity(intent2);
                overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
            }
        });
    }
    public boolean onKeyDown (int keyCode,KeyEvent event)
    {
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            this.finish();
            overridePendingTransition(R.anim.back_left_in,R.anim.back_right_out);
            return true;
        }

        return super.onKeyDown(keyCode,event);
    }
    void toast_message (String msg,int time)//0 is short,1 is long
    {
        time = (time%2+2)%2;
        Toast toast = Toast.makeText(MainActivity.this,msg,time);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(15);
        messageTextView.setTextColor(Color.BLACK);
        toast.show();
    }
}
