package com.example.instagram.data;

public class Notification {

    private String notif_id;
    private Long time;
    private int type;
    private String user_id;
    private String post_id;
    private String comment_id;
    private boolean viewed;


    public int getType() {
        return type;
    }

    public String getPost_id() {
        return post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNotif_id() {
        return notif_id;
    }

    public Long getTime() {
        return time;
    }

    public String getComment_id() {
        return comment_id;
    }
}
