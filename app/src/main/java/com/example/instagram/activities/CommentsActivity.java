package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.instagram.R;
import com.example.instagram.data.Post;
import com.example.instagram.data.User;
import com.example.instagram.presenters.CommentsPresenter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommentsActivity extends AppCompatActivity implements CommentsPresenter.connectionError {

    private static final String POST_EXTRA = "POST";

    private Post post;
    private ArrayList<Post.Comments> comments = new ArrayList<>();

    private CommentsPresenter presenter;

    private TextView send;
    private RecyclerView commentsView;
    private ProgressBar loading;
    private EditText commentInput;
    private String selectedComment;
    private Toolbar toolbar;
    private View delete;
    private SwipeRefreshLayout refresh;

    public static Intent newInstance(Context context, String post_id) {
        Intent intent = new Intent(context, CommentsActivity.class);
        intent.putExtra(POST_EXTRA, post_id);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            setContentView(R.layout.activity_comments);

            init();
            presenter = new CommentsPresenter(this);

            commentsView.setLayoutManager(new LinearLayoutManager(this));
            commentsView.setAdapter(new CommentsAdapter());

            presenter.loadPost(args.getString(POST_EXTRA), new CommentsPresenter.onPostLoaded() {
                @Override
                public void onSuccess(Post p) {
                    post = p;
                    updateComments();
                }
            });



            setSupportActionBar(toolbar);
            setTitle(R.string.comments_label);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });


            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (send.getText().length() > 0) {
                        sendComment();
                        commentInput.setText(null);
                    }
                }
            });

            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    updateComments();
                }
            });

        } else {
            finish();
        }
    }

    private void sendComment() {
        presenter.sendComment(commentInput.getText().toString(), post.getPost_id(), new CommentsPresenter.onCommentSended() {
            @Override
            public void onSuccess() {
                updateComments();
            }
        });
    }

    private void updateComments() {
        commentsView.removeAllViews();
        loading.setVisibility(View.VISIBLE);
        presenter.loadComments(post.getPost_id(), new CommentsPresenter.onCommentsLoaded() {
            @Override
            public void onSuccess(ArrayList<Post.Comments> result) {
                loading.setVisibility(View.GONE);
                comments = result;
                commentsView.getAdapter().notifyDataSetChanged();
                if (refresh.isRefreshing()) refresh.setRefreshing(false);
            }
        });
    }

    private void init() {
        refresh = findViewById(R.id.refresh);
        toolbar = findViewById(R.id.toolbar);
        send = findViewById(R.id.send_comment);
        commentsView = findViewById(R.id.comments_recycler);
        loading = findViewById(R.id.comments_progress);
        commentInput = findViewById(R.id.comment_input);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments_menu, menu);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                delete = findViewById(R.id.action_delete);
                delete.setVisibility(View.GONE);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                presenter.deleteComment(post.getPost_id(), selectedComment, new CommentsPresenter.onCommentDeleted() {
                    @Override
                    public void onSuccess() {
                        delete.setVisibility(View.GONE);
                        updateComments();
                    }
                });
            break;
        }
        return true;
    }

    private class CommentsHolder extends RecyclerView.ViewHolder {

        ImageView avatar;
        TextView text, date;

        public CommentsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.comment_info, parent, false));
            avatar = itemView.findViewById(R.id.comment_avatar);
            text = itemView.findViewById(R.id.content);
            date = itemView.findViewById(R.id.fullname_label);
            itemView.setVisibility(View.GONE);
        }

        public void Bind(final Post.Comments comment) {
            Date date = new Date(comment.getTime());
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            this.date.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " + calendar.get(Calendar.YEAR));
            presenter.loadUser(comment.getUser_id(), new CommentsPresenter.onUserLoaded() {
                @Override
                public void onSuccess(User user) {
                    text.setText(Html.fromHtml("<b>" + user.getUsername() + "</b> " + comment.getText()));
                    Glide.with(CommentsActivity.this)
                        .load(user.getImage())
                        .into(avatar);
                    itemView.setVisibility(View.VISIBLE);
                }

            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    String userId = presenter.getUserId();
                    if (userId.equals(comment.getUser_id()) || userId.equals(post.getUser_id())) {
                        if (delete.getVisibility() == View.VISIBLE && selectedComment.equals(comment.getComment_id())) {
                            view.setBackgroundColor(getResources().getColor(R.color.fui_transparent));
                            delete.setVisibility(View.GONE);
                        } else if (delete.getVisibility() == View.GONE) {
                            view.setBackgroundColor(getResources().getColor(R.color.colorBlueButton));
                            selectedComment = comment.getComment_id();
                            delete.setVisibility(View.VISIBLE);
                        }
                    }
                    return true;
                }
            });
        }
    }

    private class CommentsAdapter extends RecyclerView.Adapter<CommentsHolder> {

        @NonNull
        @Override
        public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CommentsHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CommentsHolder holder, int position) {
            holder.Bind(comments.get(position));
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT);
    }
}
