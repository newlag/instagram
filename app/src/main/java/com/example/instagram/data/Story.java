package com.example.instagram.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Story implements Parcelable {

    private String user_id;
    private String story_id;
    private Long time;
    private String url;
    private boolean viewed;
    private int views_counter;


    protected Story(Parcel in) {
        user_id = in.readString();
        if (in.readByte() == 0) {
            time = null;
        } else {
            time = in.readLong();
        }
        url = in.readString();
        viewed = (in.readInt() == 1);
        views_counter = in.readInt();
    }

    public Story() { }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    public String getUser_id() {
        return user_id;
    }

    public Long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(user_id);
        if (time == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(time);
        }
        parcel.writeString(url);
        parcel.writeInt(viewed ? 1 : 0);
        parcel.writeInt(views_counter);
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setViews_counter(int views_counter) {
        this.views_counter = views_counter;
    }

    public int getViews_coutnter() {
        return views_counter;
    }
}
