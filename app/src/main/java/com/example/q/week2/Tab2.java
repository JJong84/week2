package com.example.q.week2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import firstTabHelper.base64Converter;
import secondTabHelper.DeletePicture;
import secondTabHelper.GetPicture;
import secondTabHelper.PostPicture;
import secondTabHelper.pictureHelper;

public class Tab2 extends FragmentActivity {

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton more, upload, download;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab2);
        final PageTwo firstFragment = new PageTwo();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.tab2_frag, firstFragment).commit();

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        more = (FloatingActionButton) findViewById(R.id.more2);
        upload = (FloatingActionButton) findViewById(R.id.upload2);
        download = (FloatingActionButton) findViewById(R.id.download2);

        more.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                anim();
            }
        });
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                builder = new AlertDialog.Builder(Tab2.this);
                builder.setTitle("Gallery List Backup")        // 제목 설정
                        .setMessage("Are you sure to upload gallery list?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList<String> pathes = getPath(Tab2.this);
                                base64Converter base = new base64Converter();
                                Log.d("connecttest", "1");
                                DeletePicture delete = new DeletePicture();
                                delete.deletePicture();
                                for(int i=0; i<pathes.size(); i++) {
                                    Bitmap myBitmap = BitmapFactory.decodeFile(pathes.get(i));
                                    String code = base.getStringFromBitmap(myBitmap);
                                    PostPicture post = new PostPicture();
                                    post.postPicture(code);
                                }

                                Toast.makeText(getApplicationContext(), "Successfully uploaded.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });
        download.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                builder = new AlertDialog.Builder(Tab2.this);
                builder.setTitle("Gallery List Recover")        // 제목 설정
                        .setMessage("Are you sure to download gallery?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ArrayList<String> downPic = new ArrayList<String>();
                                GetPicture get = new GetPicture();
                                Log.d("REST GET999", "");
                                try {
                                    Log.d("REST GET999", "");
                                    ArrayList<String> pathes = getPath(Tab2.this);
                                    /*
                                    for(int i=0; i<pathes.size(); i++){
                                        File file = new File(pathes.get(i));
                                        file.delete();

                                        Uri uri = null;
                                        //TODO: 경로 완전히 삭제

                                        if (type == PublicVariable.MEDIA_TYPE_IMAGE) {
                                            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                                        } else {
                                            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                                        }
                                        String selection = MediaStore.Images.Media.DATA + " = ?";
                                        String[] selectionArgs = {pathes.get(i)};
                                        int count = resolver.delete(uri, selection, selectionArgs);

                                    }
                                    */
                                    downPic = get.getPicture();

                                    for(int i=0; i<downPic.size(); i++){
                                        pictureHelper.addPicture(downPic.get(i), i, Tab2.this);
                                    }

                                    Log.d("REST14", downPic.get(0));
                                    Toast.makeText(getApplicationContext(), "Successfully downloaded.", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Log.d("REST567", e.getMessage());
                                    Toast.makeText(getApplicationContext(), "Download failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                builder.show();
            }
        });
    }

    public void anim() {
        if (isFabOpen) {
            more.setImageResource(R.drawable.more);
            upload.startAnimation(fab_close);
            download.startAnimation(fab_close);
            upload.setClickable(false);
            download.setClickable(false);
            isFabOpen = false;
        } else {
            more.setImageResource(R.drawable.close);
            upload.startAnimation(fab_open);
            download.startAnimation(fab_open);
            upload.setClickable(true);
            download.setClickable(true);
            isFabOpen = true;
        }
    }

    public ArrayList<String> getPath(Context context) {
        ArrayList<String> files = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
        base64Converter base = new base64Converter();

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, MediaStore.MediaColumns.DATE_ADDED + " desc");
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        int columnDisplayname = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);

        int lastIndex;
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(columnIndex);
            String nameOfFile = cursor.getString(columnDisplayname);
            lastIndex = absolutePathOfImage.lastIndexOf(nameOfFile);
            lastIndex = lastIndex >= 0 ? lastIndex : nameOfFile.length() - 1;

            if (!TextUtils.isEmpty(absolutePathOfImage)) {
                files.add(absolutePathOfImage);
            }
        }cursor.close();
        return files;
    }
}
