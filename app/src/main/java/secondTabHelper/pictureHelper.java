package secondTabHelper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import firstTabHelper.base64Converter;

public class pictureHelper {
    public pictureHelper() {
    }

    public static void addPicture(String picture, Integer count, Context context) {
        base64Converter base = new base64Converter();
        Bitmap bm = base.getBitmapFromString(picture);
        FileOutputStream fos = null;
        String imgName = "backup" + Integer.toString(count) + ".png";
        try {
            File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File file = new File(directory, imgName);
            fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), "tab2" + imgName, "backup");
            Log.d("savesuccess", Integer.toString(count));
        } catch (Exception e) {
            Log.d("savefail", e.getMessage());
        }
    }

    public static void deletePicture(Context context) {
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
        }
        cursor.close();
        try {
            for (int i = 0; i < files.size(); i++) {
                File file = new File(files.get(i));
                file.delete();
            }
        } catch(Exception e){}
    }
}
