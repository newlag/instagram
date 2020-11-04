package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.instagram.presenters.PostPresenterDetails;
import com.example.instagram.R;


public class PostActivityDetails extends AppCompatActivity implements PostPresenterDetails.connectionError {

    private static final int GET_CODE = 10;

    private Toolbar toolbar;

    private TextView next;
    private ImageView cancel;
    private EditText description;
    private ImageView image;

    private PostPresenterDetails presenter;
    private ProgressBar progress;

    private static final String FILE_EXTRA = "FILE";

    public static Intent newInstance(Context context, String file) {
        Intent intent = new Intent(context, PostActivityDetails.class);
        intent.putExtra(FILE_EXTRA, file);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Bundle args = getIntent().getExtras();
        if (args != null) {
            final Uri uri = Uri.parse(args.getString(FILE_EXTRA));
            toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (progress.getVisibility() == View.GONE) {
                        onBackPressed();
                    }
                }
            });
            toolbar.inflateMenu(R.menu.gallery_menu);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_next) {
                        if (progress.getVisibility() == View.GONE) {
                            progress.setVisibility(View.VISIBLE);
                            description.setEnabled(false);
                            presenter.uploadPost(uri, description.getText().toString(), new PostPresenterDetails.onPostUploaded() {
                                @Override
                                public void onSuccess() {
                                    setResult(RESULT_OK);
                                    onBackPressed();
                                }
                            });
                        }
                    }
                    return true;
                }
            });
            description = findViewById(R.id.description_text);
            image = findViewById(R.id.post_preview);
            presenter = new PostPresenterDetails(this);
            progress = findViewById(R.id.upload_progress);
            progress.setVisibility(View.GONE);
            image.setImageURI(uri);
        } else {
            finish();
        }
    }


    @Override
    public void showConnectionError() {
        progress.setVisibility(View.GONE);
        description.setEnabled(true);
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }
}
