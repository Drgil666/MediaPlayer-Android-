package com.example.demo1;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class Main2Activity extends AppCompatActivity
{
    private Button register_submit_button;
    private EditText register_username;
    private EditText register_password;
    private Button register_back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        register_submit_button = (Button) findViewById(R.id.register_submit_button);
        register_username = (EditText) findViewById(R.id.register_username);
        register_password = (EditText) findViewById(R.id.register_password);
        register_back_button = (Button) findViewById(R.id.register_back_button);
        register_submit_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String username = register_username.getText().toString();
                String password = register_password.getText().toString();
                if ("".equals(username) || "".equals(password))
                {
                    toast_message("Username or Password can't be empty!", 0);
                } else if (!check_invaild(username))
                {
                    toast_message("invaild username!", 0);
                } else if (!check_invaild(password) || !(password.length() >= 8 && password.length() <= 12))
                {
                    toast_message("invailed password!", 0);
                } else
                {
                    Intent intent1 = new Intent(Main2Activity.this, Main4Activity.class);
                    //服务器上传数据,调用，生成新的uid
                    Bundle bundle = new Bundle();
                    bundle.putInt("uid", 1);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                }
            }
        });
        register_back_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
                overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
//            overridePendingTransition(R.anim.back_top_in,R.anim.back_buttom_out);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            this.finish();
            overridePendingTransition(R.anim.back_left_in, R.anim.back_right_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    void toast_message(String msg, int time)//0 is short,1 is long
    {
        time = (time % 2 + 2) % 2;
        Toast toast = Toast.makeText(Main2Activity.this, msg, time);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout linearLayout = (LinearLayout) toast.getView();
        TextView messageTextView = (TextView) linearLayout.getChildAt(0);
        messageTextView.setTextSize(15);
        messageTextView.setTextColor(Color.BLACK);
        toast.show();
    }
    boolean check_number(char s)
    {
        if (s >= '0' && s <= '9')
            return true;
        else
            return false;
    }
    boolean check_BigLetter(char s)
    {
        if (s >= 'A' && s <= 'Z')
            return true;
        else
            return false;
    }
    boolean check_SmallLetter(char s)
    {
        if (s >= 'a' && s <= 'z')
            return true;
        else
            return false;
    }
    boolean check_invaild(String s)
    {
        for (int i = 0; i < s.length(); i++)
        {
            System.out.println(s.charAt(i));
            if (!(check_BigLetter(s.charAt(i)) || check_SmallLetter(s.charAt(i)) || check_number(s.charAt(i))))
            {
                return false;
            }
        }
        return true;
    }
}
