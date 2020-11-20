package com.example.instagram.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.instagram.R;
import com.example.instagram.presenters.UploadPresenter;
import com.yashoid.instacropper.InstaCropperView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class UploadActivity extends Activity implements UploadPresenter.connectionError {


    private UploadPresenter presenter;

    private InstaCropperView preview;
    private RecyclerView photoView;

    private ArrayList<String> images = new ArrayList<>();

    private Toolbar toolbar;
    private static final int DETAILS_REQUEST = 0;

    private static final String INTENT_EXTRA = "IS_PROFILE_EXTRA";
    private static final String FILE_EXTRA = "FILE_EXTRA";
    private int type; // 0 - пост,  1 - фото профиля, 2 - история

    public static Intent newInstance(Context context, int type) {
        Intent intent = new Intent(context, UploadActivity.class);
        intent.putExtra(INTENT_EXTRA, type);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new);

        Bundle args = getIntent().getExtras();
        if (args != null) {
            type = args.getInt(INTENT_EXTRA);
        }

        presenter = new UploadPresenter(this);
        photoView = findViewById(R.id.recycler_view);
        preview = findViewById(R.id.image_cropper);
        photoView.setLayoutManager(new GridLayoutManager(this, 4));
        photoView.setAdapter(new PhotoAdapter());
        photoView.setHasFixedSize(true);
        if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            loadImage loadImage = new loadImage();
            loadImage.execute();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_cancel);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        toolbar.setTitle(R.string.gallery_title);
        toolbar.inflateMenu(R.menu.gallery_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                

                if (item.getItemId() == R.id.action_next) {
                    preview.crop(
                            View.MeasureSpec.makeMeasureSpec(1024, View.MeasureSpec.AT_MOST),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            new InstaCropperView.BitmapCallback() {
                                @Override
                                public void onBitmapReady(Bitmap bitmap) {
                                    saveImage(bitmap);
                                }
                            });
                }
                return true;
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImage loadImage = new loadImage();
                    loadImage.execute();
                } else {
                    finish();
                }
            break;
        }
    }

    @Override
    public void showConnectionError() {
        Toast.makeText(this, R.string.error_connection, Toast.LENGTH_SHORT).show();
    }


    private void saveImage(Bitmap bitmap) {
        String name = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String file = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, name, null);
        Intent intent = new Intent();
        switch(type) {
            case 0: // пост
                startActivityForResult(PostActivityDetails.newInstance(this, file), DETAILS_REQUEST);
            break;

            case 1: // фото профиля
                intent.putExtra(FILE_EXTRA, file);
                setResult(RESULT_OK, intent);
                finish();
            break;
            case 2: // история
                Intent i = new Intent();
                i.putExtra(FILE_EXTRA, file);
                setResult(RESULT_OK, i);
                finish();
                Log.i("[UploadActivity.java]", "лолкекчебурек" + file);
            break;
        }

    }

    private class loadImage extends AsyncTask<Void, Void, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            Cursor cursor = getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
            int path_column = cursor.getColumnIndexOrThrow(projection[0]);
            String path;
            ArrayList<String> images = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                if (cursor.moveToNext()) {
                    path = cursor.getString(path_column);
                    images.add(path);
                } else {
                    break;
                }
            }

            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            images = strings;
            photoView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DETAILS_REQUEST && resultCode == RESULT_OK) {
            onBackPressed();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        ImageView image;

        public PhotoHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.photo_item, parent, false));
            image = itemView.findViewById(R.id.post_photo);
        }

        public void Bind(final String path) {
            int s = photoView.getWidth() / 4;
            Glide.with(UploadActivity.this)
                    .load(path)
                    .apply(new RequestOptions()
                            .override(s, s)
                            .error(R.color.colorBlack))
                    .into(image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preview.setImageUri(Uri.fromFile(new File(path)));
                    if (type == 2) {
                        preview.setRatios(0.5625F, 0.5625F, 0.5625F);
                    }
                    else {
                        preview.setRatios(0.8F, 0.8F, 0.8F); // Соотношение 4:5
                    }
                }
            });
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PhotoHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            holder.Bind(images.get(position));
        }

        @Override
        public int getItemCount() {
            return images.size();
        }
    }

    public static String readFileExtra(Intent intent) {
        return intent.getExtras().getString(FILE_EXTRA);
    }

}
