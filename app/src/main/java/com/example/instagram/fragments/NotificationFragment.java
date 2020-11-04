package com.example.instagram.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.DateUtil;
import com.example.instagram.R;
import com.example.instagram.activities.CommentsActivity;
import com.example.instagram.data.Notification;
import com.example.instagram.data.User;
import com.example.instagram.presenters.NotificationPresenter;

import java.util.ArrayList;

public class NotificationFragment extends BaseFragment implements NotificationPresenter.connectionError {


    private Toolbar toolbar;
    private ArrayList<Notification> notifications = new ArrayList<>();

    private NotificationPresenter presenter;

    private RecyclerView notificationView;

    private SwipeRefreshLayout refresh;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.fragment_list, container, false);
        init(v);
        refresh.setRefreshing(true);
        presenter = new NotificationPresenter(this);

        notificationView.setLayoutManager(new LinearLayoutManager(getActivity()));
        notificationView.setAdapter(new NotificationAdapter());
        loadNotification();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notifications = new ArrayList<>();
                notificationView.getAdapter().notifyDataSetChanged();
                loadNotification();
            }
        });
        return v;
    }

    private void init(View v) {
        toolbar = v.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.notification_title);
        notificationView = v.findViewById(R.id.items_list);
        refresh = v.findViewById(R.id.refresh);
    }

    private void loadNotification() {
        presenter.loadNotificatons(new NotificationPresenter.onNotificationsLoaded() {
            @Override
            public void onSuccess(final ArrayList<Notification> n) {
                refresh.setRefreshing(false);
                for (final Notification notification : n) {
                    presenter.checkNotification(notification, new NotificationPresenter.onNotificationChecked() {
                        @Override
                        public void isNotificationExist(boolean status) {
                            if (status) {
                                notifications.add(notification);
                                notificationView.getAdapter().notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    private class NotificationHolder extends RecyclerView.ViewHolder {

        private ImageView avatar, post;
        private TextView content;

        public NotificationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.notification_list_item, parent, false));
            avatar = itemView.findViewById(R.id.user_photo);
            post = itemView.findViewById(R.id.imageView);
            content = itemView.findViewById(R.id.content);
            itemView.setVisibility(View.GONE);
        }

        public void Bind(final Notification notification) {

            presenter.loadNotificationDetails(notification, new NotificationPresenter.onNotificationDetailsLoaded() {
                @Override
                public void onSuccess(User user, String... p) {
                    String colorGrey =  Integer.toHexString(ContextCompat.getColor(getActivity(), R.color.colorGrey) & 0x00ffffff);
                    Glide.with(getActivity())
                        .load(user.getImage())
                        .apply(new RequestOptions()
                        .placeholder(R.color.colorGrey)
                        .error(R.drawable.default_avatar))
                        .into(avatar);
                    switch (notification.getType()) {
                        case 0: // like
                            post.setVisibility(View.VISIBLE);
                            content.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + getString(R.string.like_title) +  " <font color=#" + colorGrey + ">" + DateUtil.getDate(notification.getTime()) + getString(R.string.days_ago) + "</font>"));
                            Glide.with(getActivity())
                                .load(p[0])
                                .into(post);
                        break;
                        case 1: // follow
                            content.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + getString(R.string.follow_title) +  " <font color=#" + colorGrey + ">" + DateUtil.getDate(notification.getTime()) + getString(R.string.days_ago) + "</font>"));
                        break;
                        case 2: // comment
                            post.setVisibility(View.VISIBLE);
                            content.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + getString(R.string.comment_title) + " " + p[1] +  " <font color=#" + colorGrey + ">" + DateUtil.getDate(notification.getTime()) + getString(R.string.days_ago) + "</font>"));
                            Glide.with(getActivity())
                                    .load(p[0])
                                    .into(post);
                        break;
                    }
                    itemView.setVisibility(View.VISIBLE);
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (notification.getType()) {
                                case 0:
                                    addFragment(PostListFragment.newInstance(presenter.getUserId()));
                                break;
                                case 1:
                                    addFragment(ProfileFragment.newInstance(notification.getUser_id()));
                                break;
                                case 2:
                                    startActivity(CommentsActivity.newInstance(getActivity(), notification.getPost_id()));
                                break;
                            }
                        }
                    });
                }
            });
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {

        @NonNull
        @Override
        public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NotificationHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
            holder.Bind(notifications.get(position));
        }

        @Override
        public int getItemCount() {
            return notifications.size();
        }
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(getActivity(), R.string.error_connection, Toast.LENGTH_SHORT).show();
    }
}
