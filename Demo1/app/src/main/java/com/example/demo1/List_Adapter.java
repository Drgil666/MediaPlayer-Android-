package com.example.demo1;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;
public class List_Adapter extends ArrayAdapter<Music>
{
    private int resourceId;
    View view;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    public List_Adapter (@NonNull Context context,int textViewResourceId,@NonNull Vector<Music> objects)
    {
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }
//    public boolean isEnabled (int position)
//    {
//        if (position==0)
//            return false;
//        return true;
//    }
    @NonNull
    public View getView (final int position,@Nullable final View convertView,@NonNull ViewGroup parent)
    {
        final Music music_list = (Music)getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        ImageView icon = (ImageView)view.findViewById(R.id.list_image);
        final TextView title = (TextView)view.findViewById(R.id.list_title);
        TextView anthor = (TextView)view.findViewById(R.id.list_anthor);
        Button list_button = (Button)view.findViewById(R.id.list_button);
//        if (position==0)
//        {
//            icon.setVisibility(View.INVISIBLE);
//            title.setVisibility(View.INVISIBLE);
//            anthor.setVisibility(View.INVISIBLE);
//            list_button.setVisibility(View.INVISIBLE);
//        }
        icon.setImageResource(music_list.getPost());
        title.setText(music_list.getTitle());
        anthor.setText(music_list.getAnthor());
        list_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("是否删除"+"\""+title.getText()+"\"?");
                builder.setTitle("提示");
                builder.setPositiveButton("取消",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface dialog,int which)
                    {
                    }
                });
                builder.setNegativeButton("删除",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick (DialogInterface dialog,int which)
                    {
                        sp = getContext().getSharedPreferences("data",Context.MODE_PRIVATE);
                        editor = sp.edit();
                        Log.v("list_view_delete",position+"");
                        remove(music_list);
                        notifyDataSetChanged();
                        editor.putString("delete_title",music_list.getTitle());
                        editor.commit();
                    }
                });
                builder.show();
            }
        });
        return view;
    }
}
