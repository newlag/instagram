package com.example.instagram.data;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Post implements Parcelable {

    private String post_id;
    private String user_id;
    private String description;
    private long time;
    private String image;
    private String url;

    public Post() { }

    protected Post(Parcel in) {
        post_id = in.readString();
        user_id = in.readString();
        description = in.readString();
        time = in.readLong();
        image = in.readString();
        url = in.readString();
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPost_id() {
        return post_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public String getDescription() {
        return description;
    }
    public long getTime() {
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
        parcel.writeString(post_id);
        parcel.writeString(user_id);
        parcel.writeString(description);
        parcel.writeLong(time);
        parcel.writeString(image);
        parcel.writeString(url);
    }



    public static class Comments {
        private String comment_id;
        private String user_id;
        private String text;
        private long time;

        public String getComment_id() {
            return comment_id;
        }
        public String getUser_id() {
            return user_id;
        }
        public String getText() {
            return text;
        }
        public long getTime() {
            return time;
        }
    }

    public static class Likes {
        private String user_id;

        public String getUser_id() {
            return user_id;
        }
    }

}
