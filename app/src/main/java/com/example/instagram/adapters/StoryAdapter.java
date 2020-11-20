package com.example.instagram.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.R;
import com.example.instagram.activities.StoryActivity;
import com.example.instagram.activities.UploadActivity;
import com.example.instagram.data.Story;
import com.example.instagram.data.User;
import com.example.instagram.presenters.PostListPresenter;

import java.util.ArrayList;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryHolder> {

    private ArrayList<Story> storyies;
    private Context context;
    private PostListPresenter presenter;
    private Callback callback;

    public StoryAdapter(ArrayList<Story> storyies, Context context, PostListPresenter presenter, Callback callback) {
        this.storyies = storyies;
        this.context = context;
        this.presenter = presenter;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0: return 0;
            default: return 1;
        }
    }

    @NonNull
    @Override
    public StoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(context).inflate(R.layout.story_upload, parent, false);
            break;
            default:
                v = LayoutInflater.from(context).inflate(R.layout.story_item, parent, false);
        }
        return new StoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryHolder holder, int position) {
        holder.Bind(position);
    }

    @Override
    public int getItemCount() {
        return (storyies.size() + 1);
    }

    public class StoryHolder extends RecyclerView.ViewHolder {

        private ImageView avatar;
        private TextView username;

        public StoryHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.story_preview);
            itemView.setVisibility(View.GONE);
        }

        public void Bind(final int position) {
            switch (position) {
                case 0: // Личная история
                    presenter.loadUser(presenter.getUserId(), new PostListPresenter.onUserLoaded() {
                        @Override
                        public void onSuccess(User user) {
                            Glide.with(context)
                                .load(user.getImage())
                                .apply(new RequestOptions()
                                .placeholder(R.color.colorGrey)
                                .error(R.drawable.default_avatar))
                                .into(avatar);
                            itemView.setVisibility(View.VISIBLE);
                        }
                    });
                    itemView.findViewById(R.id.upload_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            callback.uploadStory();
                        }
                    });
                    /*avatar.setOnClickListener(new View.OnClickListener() { // Временный upload на аватаре
                        @Override
                        public void onClick(View view) {
                            callback.uploadStory();

                        }
                    });*/
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(StoryActivity.newInstance(context, storyies, position, true));
                        }
                    });
                break;
                default: // Остальные
                    username = itemView.findViewById(R.id.username_label);
                    presenter.loadUser(storyies.get(position-1).getUser_id(), new PostListPresenter.onUserLoaded() {
                        @Override
                        public void onSuccess(User user) {
                            Glide.with(context)
                                .load(user.getImage())
                                .apply(new RequestOptions()
                                .placeholder(R.color.colorGrey)
                                .error(R.drawable.default_avatar))
                                .into(avatar);
                            username.setText(user.getUsername());
                            ImageView border = itemView.findViewById(R.id.story_border);
                            if (storyies.get(position-1).isViewed()) {
                                border.setBackground(context.getDrawable(R.drawable.story_border_viewed));
                            } else {
                                border.setBackground(context.getDrawable(R.drawable.story_border_new));
                            }
                            itemView.setVisibility(View.VISIBLE);
                        }
                    });
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            context.startActivity(StoryActivity.newInstance(context, storyies, position-1, false));
                        }
                    });


            }
        }

    }

    public interface Callback {
        void uploadStory();
    }
}
