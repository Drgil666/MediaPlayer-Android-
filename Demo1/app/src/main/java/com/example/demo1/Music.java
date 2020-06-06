package com.example.demo1;
public class Music
{
    int id;
    String title;
    String anthor;
    int post;
    int time;
    public int getTime ()
    {
        return time;
    }
    public void setTime (int time)
    {
        this.time = time;
    }
    public Music ()
    {
        super();
    }
    public Music (int id,String title,String anthor,int post,int time)
    {
        this.id = id;
        this.post = post;
        this.anthor = anthor;
        this.title = title;
        this.time = time;
    }
    public int getId ()
    {
        return id;
    }
    public void setId (int id)
    {
        this.id = id;
    }
    public String getTitle ()
    {
        return title;
    }
    public void setTitle (String title)
    {
        this.title = title;
    }
    public String getAnthor ()
    {
        return anthor;
    }
    public void setAnthor (String anthor)
    {
        this.anthor = anthor;
    }
    public int getPost ()
    {
        return post;
    }
    public void setPost (int post)
    {
        this.post = post;
    }
}
