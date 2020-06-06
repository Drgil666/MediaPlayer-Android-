package com.example.demo1;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Vector;
public class Main5Activity extends AppCompatActivity
{
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Button btn_play;//播放按钮
    private SearchView search_view;
    private Button account_button;//登录账户按钮
    private Button change_button;//更换界面
    private String log_user;//用户名
    private ImageView vinyl;//黑胶片
    private Button btn_pre;//上一首
    private Button btn_next;//下一首
    private TextView title;//歌曲题目
    private TextView anthor;//歌曲作者
    int music_length_total = 0;//播放列表歌曲数
    private TextView played_time;//播放时间
    private SeekBar seekBar;//拖动条
    int play_mode = 1;//播放模式
    private Button mode;//播放模式按钮
    private boolean is_played = false;//是否在播放
    private MediaPlayer mPlayer = new MediaPlayer();//音乐器
    private ImageView post_view;//海报
    private ImageView post_background_view;//海报背景
    private TextView total_time;//总播放时间
    private int start_degree = 0;//开始角度
    private long time = 0;//时间戳
    private boolean isRelease = true;//判断是否MediaPlayer是否释放的标志
    public int played_id = 0;//正在播放的歌曲序号
    public Vector<Music> play_list = new Vector<>();//播放列表
    public Vector<Music> music_list = new Vector<>();//用户音乐列表（歌单)
    public Vector<Music> music_list_view = new Vector<>();//list_view用列表(搜索用)
    public Vector<Music> All_music_list = new Vector<>();//歌曲库
    private Runnable runnable;//子进程
    private Handler handler = new Handler();//子进程
    private int CHANGE = 1;//页面模式切换
    private ListView list_view;
    ArrayAdapter<Music> adapter;
    final Animation rotateAnimation = new RotateAnimation(start_degree,start_degree+360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
    //旋转动画
    @Override
    protected void onCreate (Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        showExitDialog01("Update ver 1.4","目前基本功能:\n音乐基本播放、进度条、搜索功能、播放方式");
        Main5Activity.this.getSharedPreferences("data",Context.MODE_PRIVATE);
        account_button = (Button)findViewById(R.id.account_button);
        change_button = (Button)findViewById(R.id.change_button);
        btn_play = (Button)findViewById(R.id.btn_play);
        vinyl = (ImageView)findViewById(R.id.vinyl);
        post_view = (ImageView)findViewById(R.id.post_view);
        played_time = (TextView)findViewById(R.id.played_time);
        total_time = (TextView)findViewById(R.id.total_time);
        title = (TextView)findViewById(R.id.music_title);
        anthor = (TextView)findViewById(R.id.music_anthor);
        mode = (Button)findViewById(R.id.play_mode);
        search_view = (SearchView)findViewById(R.id.search_view);
        list_view = (ListView)findViewById(R.id.list_view);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        post_background_view = (ImageView)findViewById(R.id.post_background_view);
        btn_pre = (Button)findViewById(R.id.btn_pre);
        btn_next = (Button)findViewById(R.id.btn_next);
        init();//可以利用bundle处理
        init2();//子线程
        adapter = new List_Adapter(Main5Activity.this,R.layout.list_view,music_list_view);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick (AdapterView<?> parent,View view,int position,long id)
            {
                Music music = music_list_view.get(position);
                toast_message(music.getTitle(),0);
                int flag = 1;
                for (int z = 0;z<music_list_view.size();z++)
                {
                    if (music.getId()==play_list.elementAt(z).getId())
                    {
                        flag = 0;
                        break;
                    }
                }
                if (flag==1)
                {
                    play_list.add(music);
                    music_length_total++;
                    played_id = play_list.size()-1;
                    Log.v("add",played_id+"");
                    mPlayer.reset();
                    isRelease = true;
                    is_played = false;
                    time = 0;
                    init3(play_list.elementAt(played_id).getPost());
                    played_time.setText(changetostring(0));
                    total_time.setText(changetostring(play_list.elementAt(played_id).getTime()));
                    title.setText(play_list.elementAt(played_id).getTitle());
                    anthor.setText(play_list.elementAt(played_id).getAnthor());
                    seekBar.setProgress(0);
                    btn_play.callOnClick();
                } else
                {
                    if (play_list.elementAt(played_id).getId()!=music_list_view.elementAt(position).getId())
                    {
                        for (int z = 0;z<music_list_view.size();z++)
                        {
                            if (play_list.elementAt(z).getId()==music_list_view.elementAt(position).getId())
                                played_id = z;
                        }
                        Log.v("change",played_id+"");
                        mPlayer.reset();
                        isRelease = true;
                        is_played = false;
                        time = 0;
                        init3(play_list.elementAt(played_id).getPost());
                        played_time.setText(changetostring(0));
                        total_time.setText(changetostring(play_list.elementAt(played_id).getTime()));
                        title.setText(play_list.elementAt(played_id).getTitle());
                        anthor.setText(play_list.elementAt(played_id).getAnthor());
                        seekBar.setProgress(0);
                        btn_play.callOnClick();
                    }
                }
            }
        });
        change_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                CHANGE = 1-CHANGE;
                if (CHANGE==1)//播放模式
                {
                    if (play_list.size()==0)
                    {
                        toast_message("列表为空！",0);
                    }
                    btn_pre.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.VISIBLE);
                    btn_play.setVisibility(View.VISIBLE);
                    mode.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    played_time.setVisibility(View.VISIBLE);
                    if (is_played)
                        Play_Spin(1);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX,70);
                    title.setText(play_list.elementAt(played_id).getTitle());
                    total_time.setVisibility(View.VISIBLE);
                    vinyl.setVisibility(View.VISIBLE);
                    post_view.setVisibility(View.VISIBLE);
                    anthor.setVisibility(View.VISIBLE);
                    list_view.setVisibility(View.INVISIBLE);
                    search_view.setVisibility(View.INVISIBLE);
                    account_button.setVisibility(View.INVISIBLE);
                } else//列表模式
                {
                    btn_pre.setVisibility(View.INVISIBLE);
                    btn_next.setVisibility(View.INVISIBLE);
                    btn_play.setVisibility(View.INVISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    mode.setVisibility(View.INVISIBLE);
                    played_time.setVisibility(View.INVISIBLE);
                    total_time.setVisibility(View.INVISIBLE);
                    vinyl.setVisibility(View.INVISIBLE);
                    search_view.setVisibility(View.VISIBLE);
                    post_view.setVisibility(View.INVISIBLE);
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX,60);
                    title.setText("Welcome Back,"+log_user+".");
                    anthor.setVisibility(View.INVISIBLE);
                    account_button.setVisibility(View.VISIBLE);
                    list_view.setVisibility(View.VISIBLE);
                    Play_Spin(0);
                }
            }
        });
        account_button.setBackgroundResource(R.drawable.ui17);
        account_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                //                        Intent intent1 = new Intent(Main5Activity.this,Main4Activity.class);
                //                        startActivity(intent1);
                //                        overridePendingTransition(R.anim.push_right_in,R.anim.push_left_out);
                showExitDialog01("Update ver 1.4","目前基本功能:\n音乐基本播放、进度条、搜索功能、播放方式");
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged (final SeekBar seekBar,int progress,boolean fromUser)
            {
                Log.e("percent",1.0*seekBar.getProgress()/seekBar.getMax()+"");
                int new_time = play_list.elementAt(played_id).getTime()*seekBar.getProgress()/seekBar.getMax();
                played_time.setText(changetostring(new_time));
            }
            @Override
            public void onStartTrackingTouch (final SeekBar seekBar)
            {
            }
            @Override
            public void onStopTrackingTouch (SeekBar seekBar)
            {
                Log.v("skip to",1.0*seekBar.getProgress()/seekBar.getMax()+"");
                if (isRelease)
                {
                    mPlayer = MediaPlayer.create(Main5Activity.this,play_list.elementAt(played_id).getId());
                    mPlayer.start();
                    btn_play.setBackgroundResource(R.drawable.ui11);
                    isRelease = false;
                    if (post_view.getVisibility()==View.VISIBLE)
                        Play_Spin(1);
                }
                seekBar.setProgress((int)(seekBar.getMax()*Math.min(1.0*seekBar.getProgress()/seekBar.getMax(),0.99)));
                mPlayer.seekTo((int)(mPlayer.getDuration()*Math.min(1.0*seekBar.getProgress()/seekBar.getMax(),0.99)));
            }
        });
        mode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                Log.v("list length",music_length_total+"");
                if (play_mode==1)
                {
                    for (int z = 0;z<music_list.size();z++)
                    {
                        if (music_list.elementAt(z).getId()==play_list.elementAt(played_id).getId())
                        {
                            played_id = z;
                            break;
                        }
                    }
                    Log.v("mode","2");
                    Log.v("played_id",played_id+"");
                    play_mode = 2;
                    toast_message("已切换成列表循环!",0);
                    mode.setBackgroundResource(R.drawable.ui15);
                    play_list.clear();
                    play_list.addAll(music_list);
                    music_list_view.clear();
                    music_list_view.addAll(music_list);
                } else if (play_mode==2)
                {
                    Log.v("mode","3");
                    play_mode = 3;
                    toast_message("已切换至随机播放!",0);
                    mode.setBackgroundResource(R.drawable.ui16);
                    int[] a = new int[music_length_total];
                    play_list.clear();
                    for (int i = 0;i<music_length_total;i++)
                    {
                        a[i] = 1;
                    }
                    String permutation = "["+played_id+",";
                    play_list.add(music_list.elementAt(played_id));
                    a[played_id] = -1;
                    played_id = 0;
                    for (int i = 2;i<=music_length_total;i++)
                    {
                        int index = (int)(Math.random()*(music_length_total));
                        Log.v("index",""+index);
                        while (a[index]!=1)
                        {
                            index = (int)(Math.random()*(music_length_total));
                            Log.v("index",""+index);
                        }
                        a[index] = -1;
                        permutation = permutation+""+index+",";
                        play_list.add(music_list.elementAt(index));
                    }
                    permutation = permutation+"]";
                    Log.v("permutation",permutation);
                } else if (play_mode==3)
                {
                    Log.v("mode","1");
                    play_mode = 1;
                    for (int z = 0;z<music_list.size();z++)
                    {
                        if (music_list.elementAt(z).getId()==play_list.elementAt(played_id).getId())
                        {
                            played_id = z;
                            break;
                        }
                    }
                    Log.v("played_id",played_id+"");
                    toast_message("已切换至单曲循环！",0);
                    mode.setBackgroundResource(R.drawable.ui14);
                    play_list.clear();
                    play_list.addAll(music_list);
                    music_list_view.clear();
                    music_list_view.addAll(music_list);
                }
                Log.v("List",play_list.toString());
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                if (!is_played)
                {
                    Log.v("title","play "+played_id);
                    is_played = true;
                    btn_play.setBackgroundResource(R.drawable.ui11);
                    if (isRelease)
                    {
                        mPlayer = MediaPlayer.create(Main5Activity.this,play_list.elementAt(played_id).getId());
                        isRelease = false;
                    }
                    if (post_view.getVisibility()==View.VISIBLE)
                        Play_Spin(1);
                    mPlayer.start();
                    //开始播放
                } else
                {
                    Log.v("title","pause");
                    is_played = false;
                    Play_Spin(0);
                    btn_play.setBackgroundResource(R.drawable.ui5);
                    mPlayer.pause();//停止播放
                }
            }
        });
        btn_pre.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                seekBar.setProgress(0);
                Log.v("title","pre");
                if (played_id==0)
                {
                    played_id = play_list.size()-1;
                } else
                {
                    played_id--;
                }
                Log.v("played_id",Integer.toString(played_id));
                if (CHANGE==1)
                    title.setText(play_list.elementAt(played_id).getTitle());
                anthor.setText("<"+play_list.elementAt(played_id).getAnthor()+">");
                mPlayer.reset();
                mPlayer = MediaPlayer.create(Main5Activity.this,play_list.elementAt(played_id).getId());
                time = 0;
                is_played = true;
                total_time.setText(changetostring(play_list.elementAt(played_id).getTime()));
                init3(play_list.elementAt(played_id).getPost());
                btn_play.setBackgroundResource(R.drawable.ui11);
                if (post_view.getVisibility()==View.VISIBLE)
                    Play_Spin(1);
                mPlayer.start();
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            {
                Log.v("title","next");
                seekBar.setProgress(0);
                if (played_id==play_list.size()-1)
                {
                    played_id = 0;
                } else
                {
                    played_id++;
                }
                mPlayer.reset();
                mPlayer = MediaPlayer.create(Main5Activity.this,play_list.elementAt(played_id).getId());
                is_played = true;
                time = 0;
                Log.v("played_id",Integer.toString(played_id));
                total_time.setText(changetostring(play_list.elementAt(played_id).getTime()));
                if (CHANGE==1)
                    title.setText(play_list.elementAt(played_id).getTitle());
                anthor.setText("<"+play_list.elementAt(played_id).getAnthor()+">");
                init3(play_list.elementAt(played_id).getPost());
                btn_play.setBackgroundResource(R.drawable.ui11);
                if (post_view.getVisibility()==View.VISIBLE)
                    Play_Spin(1);
                mPlayer.start();
            }
        });
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit (String query)
            {
                return false;
            }
            @Override
            public boolean onQueryTextChange (String newText)
            {
                toast_message(newText,0);
                music_list_view.clear();
                for (int i = 0;i<music_list.size();i++)
                {
                    System.out.println(music_list.elementAt(i).getTitle());
                    if (music_list.elementAt(i).getTitle().contains(newText))
                    {
                        music_list_view.add(music_list.elementAt(i));
                    }
                }
                System.out.println("---------------");
                for (int i = 0;i<music_list_view.size();i++)
                {
                    System.out.println(music_list_view.elementAt(i).getTitle());
                }
                System.out.println("---------------");
                adapter.notifyDataSetChanged();
                return true;
            }
        });
    }
    void Play_Spin (int i)//海报旋转
    {
        LinearInterpolator lin = new LinearInterpolator();//旋转动画匀速
        rotateAnimation.setInterpolator(lin);
        rotateAnimation.setDuration(18000);
        rotateAnimation.setFillAfter(false);
        rotateAnimation.setRepeatCount(-1);
        if (i%2==1)
        {
            if (rotateAnimation!=null)
            {
                post_view.startAnimation(rotateAnimation);
            } else
            {
                post_view.setAnimation(rotateAnimation);
                post_view.startAnimation(rotateAnimation);
            }
        } else
            post_view.clearAnimation();
    }
    void init3 (int id)//海报、背景高斯模糊
    {
        post_view.setImageResource(id);
        post_background_view.setImageResource(id);
        post_background_view.setImageBitmap(fastBlur(post_background_view,1,75));
    }
    void init ()
    {
        sp = getSharedPreferences("data",MODE_PRIVATE);
        editor = sp.edit();
        editor.putString("delete_title","***");
        editor.apply();
        CHANGE = 1;
        log_user = "DrGilbert";
        is_played = false;
        start_degree = 0;
        time = 0;
        played_id = 0;
        play_mode = 1;
        music_length_total = 0;
        //        Intent intent0 = getIntent();
        //        Bundle bundle0 = intent0.getExtras();
        //        String username =null;
        //        Object ob1=bundle0.getString("log_user");
        //        if(ob1!=null)
        //        username=(String)ob1;
        {
            Music x = new Music(R.raw.music1,"Voyage of Promise","山根ミチル",R.mipmap.post1,346);
            play_list.add(x);
            music_list.add(x);
            music_list_view.add(x);
            All_music_list.add(x);
            x = new Music(R.raw.music2,"Luxurious Overture","山根ミチル",R.mipmap.post2,265);
            play_list.add(x);
            music_list.add(x);
            music_list_view.add(x);
            All_music_list.add(x);
            x = new Music(R.raw.music3,"THE STORY","大橋トリオ",R.mipmap.post3,223);
            play_list.add(x);
            music_list.add(x);
            music_list_view.add(x);
            All_music_list.add(x);
        }
        music_length_total = play_list.size();
        total_time.setText(changetostring(play_list.elementAt(played_id).getTime()));
        title.setText(play_list.elementAt(played_id).getTitle());
        anthor.setText("<"+play_list.elementAt(played_id).getAnthor()+">");
        init3(play_list.elementAt(played_id).getPost());
    }
    void toast_message (String msg,int time)//0 is short,1 is long
    {
        time = (time%2+2)%2;
        Toast toast = Toast.makeText(Main5Activity.this,msg,time);
        toast.setGravity(Gravity.CENTER,0,0);
        LinearLayout linearLayout = (LinearLayout)toast.getView();
        TextView messageTextView = (TextView)linearLayout.getChildAt(0);
        messageTextView.setTextSize(15);
        messageTextView.setTextColor(Color.BLACK);
        toast.show();
    }
    private static Bitmap fastBlur (ImageView source,float scale,int radius)//高斯模糊
    {
        Bitmap sentBitmap = ((BitmapDrawable)source.getDrawable()).getBitmap();
        int width = Math.round(sentBitmap.getWidth()*scale);
        int height = Math.round(sentBitmap.getHeight()*scale);
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap,width,height,false);
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(),true);
        if (radius<1)
        {
            return (null);
        }
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        int[] pix = new int[w*h];
        Log.e("pix",w+" "+h+" "+pix.length);
        bitmap.getPixels(pix,0,w,0,0,w,h);
        int wm = w-1;
        int hm = h-1;
        int wh = w*h;
        int div = radius+radius+1;
        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w,h)];
        int divsum = (div+1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256*divsum];
        for (i = 0;i<256*divsum;i++)
        {
            dv[i] = (i/divsum);
        }
        yw = yi = 0;
        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius+1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;
        for (y = 0;y<h;y++)
        {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius;i<=radius;i++)
            {
                p = pix[yi+Math.min(wm,Math.max(i,0))];
                sir = stack[i+radius];
                sir[0] = (p&0xff0000) >> 16;
                sir[1] = (p&0x00ff00) >> 8;
                sir[2] = (p&0x0000ff);
                rbs = r1-Math.abs(i);
                rsum += sir[0]*rbs;
                gsum += sir[1]*rbs;
                bsum += sir[2]*rbs;
                if (i>0)
                {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else
                {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;
            for (x = 0;x<w;x++)
            {
                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer-radius+div;
                sir = stack[stackstart%div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (y==0)
                {
                    vmin[x] = Math.min(x+radius+1,wm);
                }
                p = pix[yw+vmin[x]];
                sir[0] = (p&0xff0000) >> 16;
                sir[1] = (p&0x00ff00) >> 8;
                sir[2] = (p&0x0000ff);
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer+1)%div;
                sir = stack[(stackpointer)%div];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi++;
            }
            yw += w;
        }
        for (x = 0;x<w;x++)
        {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius*w;
            for (i = -radius;i<=radius;i++)
            {
                yi = Math.max(0,yp)+x;
                sir = stack[i+radius];
                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];
                rbs = r1-Math.abs(i);
                rsum += r[yi]*rbs;
                gsum += g[yi]*rbs;
                bsum += b[yi]*rbs;
                if (i>0)
                {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else
                {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
                if (i<hm)
                {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0;y<h;y++)
            {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000&pix[yi])|(dv[rsum]<<16)|(dv[gsum]<<8)|dv[bsum];
                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;
                stackstart = stackpointer-radius+div;
                sir = stack[stackstart%div];
                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];
                if (x==0)
                {
                    vmin[y] = Math.min(y+r1,hm)*w;
                }
                p = x+vmin[y];
                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;
                stackpointer = (stackpointer+1)%div;
                sir = stack[stackpointer];
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];
                yi += w;
            }
        }
        Log.e("pix",w+" "+h+" "+pix.length);
        bitmap.setPixels(pix,0,w,0,0,w,h);
        return (bitmap);
    }
    private static Bitmap circleBitmapByShader (ImageView source,int edgeWidth,int radius)
    {
        Bitmap bitmap = ((BitmapDrawable)source.getDrawable()).getBitmap();
        if (bitmap==null)
        {
            throw new NullPointerException("Bitmap can't be null");
        }
        float btWidth = bitmap.getWidth();
        float btHeight = bitmap.getHeight();
        // 水平方向开始裁剪的位置
        float btWidthCutSite = 0;
        // 竖直方向开始裁剪的位置
        float btHeightCutSite = 0;
        // 裁剪成正方形图片的边长，未拉伸缩放
        float squareWidth = 0f;
        if (btWidth>btHeight)
        { // 如果矩形宽度大于高度
            btWidthCutSite = (btWidth-btHeight)/2f;
            squareWidth = btHeight;
        } else
        { // 如果矩形宽度不大于高度
            btHeightCutSite = (btHeight-btWidth)/2f;
            squareWidth = btWidth;
        }
        squareWidth = 1000;
        // 设置拉伸缩放比
        float scale = edgeWidth*1.0f/squareWidth;
        Matrix matrix = new Matrix();
        matrix.setScale(scale,scale);
        // 将矩形图片裁剪成正方形并拉伸缩放到控件大小
        Bitmap squareBt = Bitmap.createBitmap(bitmap,(int)btWidthCutSite,(int)btHeightCutSite,(int)squareWidth,(int)squareWidth,matrix,true);
        // 初始化绘制纹理图
        BitmapShader bitmapShader = new BitmapShader(squareBt,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        // 初始化目标bitmap
        Bitmap targetBitmap = Bitmap.createBitmap(edgeWidth,edgeWidth,Bitmap.Config.ARGB_8888);
        // 初始化目标画布
        Canvas targetCanvas = new Canvas(targetBitmap);
        // 初始化画笔
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(bitmapShader);
        // 利用画笔绘制圆形图
        targetCanvas.drawRoundRect(new RectF(0,0,edgeWidth,edgeWidth),radius,radius,paint);
        return targetBitmap;
    }
    String changetostring (long x)
    {
        String s = null;
        long a = x/60L, b = x%60L;
        if (a<10)
        {
            s = ("0"+a).toString();
        } else
        {
            s = (a+"").toString();
        }
        s = s+":";
        if (b<10)
        {
            s = s+("0"+b).toString();
        } else
        {
            s = s+b+"";
        }
        return s;
    }
    private void init2 ()//计时器1s
    {
        runnable = new Runnable()
        {
            @Override
            public void run ()
            {
                sp = getSharedPreferences("data",MODE_PRIVATE);
                String delete_id = sp.getString("delete_title","***");
                if (!"***".equals(delete_id) && play_list.size()!=0)
                {
                    editor = sp.edit();
                    editor.putString("delete_id","***");
                    editor.apply();
                    Log.v("delete",delete_id);
                    if (delete_id.equals(play_list.elementAt(played_id).getTitle()))
                    {
                        for (int i = 0;i<play_list.size();i++)
                        {
                            if (play_list.elementAt(i).getTitle().equals(delete_id))
                            {
                                Log.v("msg","playlist delete "+delete_id);
                                play_list.remove(i);
                                break;
                            }
                        }
                        for (int i = 0;i<music_list.size();i++)
                        {
                            if (music_list.elementAt(i).getTitle().equals(delete_id))
                            {
                                Log.v("msg","musiclist delete "+delete_id);
                                music_list.remove(i);
                                break;
                            }
                        }
                        btn_next.callOnClick();
                        played_id = 0;
                    } else
                    {
                        for (int i = 0;i<play_list.size();i++)
                        {
                            if (play_list.elementAt(i).getTitle().equals(delete_id))
                            {
                                Log.v("msg","playlist delete "+delete_id);
                                play_list.remove(i);
                                break;
                            }
                        }
                        for (int i = 0;i<music_list.size();i++)
                        {
                            if (music_list.elementAt(i).getTitle().equals(delete_id))
                            {
                                Log.v("msg","musiclist delete "+delete_id);
                                music_list.remove(i);
                                break;
                            }
                        }
                    }
                    delete_id = "***";
                    music_length_total--;
                }
                if (play_list.size()==0)
                {
                    played_id = 0;
                    mPlayer.release();
                    isRelease = true;
                }
                if (is_played && play_list.size()!=0)
                {
                    //                if(time==music_length.elementAt(played_id))
                    if (time==play_list.elementAt(played_id).getTime())
                    {
                        if (play_mode!=1)
                        {
                            btn_next.callOnClick();
                        } else
                        {
                            time = 0;
                            played_time.setText("00:00");
                            seekBar.setProgress(0);
                            mPlayer.seekTo(0);
                            mPlayer.start();
                        }
                    }
                    time = Math.min(mPlayer.getCurrentPosition()/1000,play_list.elementAt(played_id).getTime());
                    if (!seekBar.isPressed())
                        seekBar.setProgress((int)(seekBar.getMax()*time/play_list.elementAt(played_id).getTime()));
                    if (!seekBar.isPressed())
                        played_time.setText(changetostring(time));
                }
                Log.v("time",Long.toString(time));
                handler.postDelayed(this,500);//延迟1秒执行
            }
        };
        handler.post(runnable);
    }
    private void showExitDialog01 (String title,String msg)//消息框
    {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setPositiveButton("OK",null).show();
    }
    //    @Override
    //    protected void onDestroy ()
    //    {
    //        if (mPlayer!=null)
    //        {
    //            mPlayer.stop();
    //            mPlayer.release();
    //            mPlayer = null;
    //        }
    //        super.onDestroy();
    //    }
}
