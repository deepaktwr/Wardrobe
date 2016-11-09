package proj.me.wardrobe.dialogs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import proj.me.wardrobe.frag.ActivityCallback;


/**
 * Created by Deepak.Tiwari on 30-10-2015.
 */
public class ImageIntent {
    static final int TAKE_PHOTO = 1;
    static final int FROM_GALLERY = 2;

    void takePhoto(ActivityCallback activityCallback) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            Uri fileUri = createImageFile((Context) activityCallback);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            activityCallback.picIntentInitiated(fileUri.toString());
            ((Activity)activityCallback).startActivityForResult(takePictureIntent, TAKE_PHOTO);
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
    }

    void fetchFromGallery(ActivityCallback activityCallback) {
        activityCallback.picIntentInitiated(null);
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity)activityCallback).startActivityForResult(intent, FROM_GALLERY);
    }

    Uri createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());

        ContentValues values = new ContentValues();
        String fileName = "IMG_" + timeStamp + ".jpg";
        values.put(MediaStore.Images.Media.TITLE, fileName);
        return context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    public String getImageResultPath(Intent data, String filePath, Context context) {
        if((data == null || data.getData() == null) && filePath == null) return null;
        //Constants.logMessage("comment on image -> "+comment);
        String imagePath = "";
            if (data != null && data.getData() != null) {
                Uri img = data.getData();
                String[] filePathClm = { MediaStore.Images.Media.DATA };
                Cursor c = context.getContentResolver().query(img, filePathClm,
                        null, null, null);
                if(c != null) {
                    c.moveToFirst();
                    c.getColumnIndex(filePathClm[0]);
                    c.close();
                }
                imagePath = img.toString();
            }else
                imagePath = filePath;
        return imagePath;
    }
}