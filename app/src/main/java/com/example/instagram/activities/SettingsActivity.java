package com.example.instagram.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.AuthActivity;
import com.example.instagram.R;
import com.example.instagram.data.User;
import com.example.instagram.presenters.SettingsPresenter;

public class SettingsActivity extends AppCompatActivity implements SettingsPresenter.connectionError {

    private static final int PHOTO_UPLOAD_CODE = 1;
    private static final String USER_EXTRA = "USER_ID";
    private String userId;
    private User userProfile;
    private EditText fullname, username, bio;
    private ImageView photo;
    private SettingsPresenter presenter;
    private TextView upload, logout;

    private Toolbar toolbar;

    private ConstraintLayout profile;

    private SwipeRefreshLayout refresh;

    public static Intent newInstance(Context context, String userId) {
        Intent intent = new Intent(context, SettingsActivity.class);
        intent.putExtra(USER_EXTRA, userId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
        Bundle args = getIntent().getExtras();
        if (!args.isEmpty()) {
            userId = args.getString(USER_EXTRA);
        }
        presenter = new SettingsPresenter(this, userId);
        presenter.loadProfile(new SettingsPresenter.onProfileLoaded() {
            @Override
            public void onSuccess(User user) { // Отображаем профиль
                userProfile = user;
                fullname.setText(userProfile.getFullname());
                username.setText(userProfile.getUsername());
                bio.setText(userProfile.getBio());
                refresh.setRefreshing(true);
                profile.setVisibility(View.VISIBLE);
                updatePhoto();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        toolbar.inflateMenu(R.menu.settings_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_accept:
                        if (!refresh.isRefreshing()) {
                            userProfile.setFullname(fullname.getText().toString());
                            userProfile.setUsername(username.getText().toString());
                            userProfile.setBio(bio.getText().toString());
                            presenter.updateProfile(userProfile, new SettingsPresenter.onProfileUpdated() {
                                @Override
                                public void onSuccess() {
                                    setResult(RESULT_OK);
                                    onBackPressed();
                                }

                                @Override
                                public void onFailure(int message) {
                                    Toast.makeText(SettingsActivity.this, getString(message), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    break;
                }
                return true;
            }
        });

        toolbar.setTitle(R.string.edit_profile);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(UploadActivity.newInstance(SettingsActivity.this, 1), PHOTO_UPLOAD_CODE);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.logout();
                startActivity(new Intent(SettingsActivity.this, AuthActivity.class));
            }
        });

        refresh.setEnabled(false);
        refresh.setRefreshing(true);

    }


    private void init() {
        profile = findViewById(R.id.settings_info);
        fullname = findViewById(R.id.settings_name_input);
        username = findViewById(R.id.settings_username_input);
        bio = findViewById(R.id.settings_bio_input);
        toolbar = findViewById(R.id.toolbar);
        refresh = findViewById(R.id.refresh);
        photo = findViewById(R.id.settings_photo);
        upload = findViewById(R.id.settings_change_photo);
        profile.setVisibility(View.GONE);
        logout = findViewById(R.id.setting_log_out);
    }

    private void updatePhoto() {
        refresh.setRefreshing(false);
        Glide.with(SettingsActivity.this)
            .load(userProfile.getImage())
            .apply(new RequestOptions()
                    .placeholder(R.color.colorGrey)
                    .error(R.drawable.default_avatar)
            )
            .into(photo);
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_UPLOAD_CODE:
                    profile.setVisibility(View.GONE);
                    refresh.setRefreshing(true);
                    String path = UploadActivity.readFileExtra(data);
                    Bundle extras = data.getExtras();
                    Uri image = Uri.parse(path);
                    presenter.uploadPhoto(image, new SettingsPresenter.onPhotoUploaded() {
                        @Override
                        public void onSuccess(String path) {
                            profile.setVisibility(View.VISIBLE);
                            userProfile.setImage(path);
                            updatePhoto();
                        }
                    });
                break;
            }
        }
    }
}
