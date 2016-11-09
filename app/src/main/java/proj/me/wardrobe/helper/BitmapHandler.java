package proj.me.wardrobe.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import proj.me.wardrobe.exceptions.BitmapException;

/**
 * Created by deepak on 8/11/16.
 */
public class BitmapHandler {

    public static Bitmap decodeBitmapFromPath(int reqWidth, int reqHeight, String imagePath, Context context) throws BitmapException{
        if(reqWidth == 0 || reqHeight == 0 || TextUtils.isEmpty(imagePath) || context == null)
            throw new IllegalArgumentException("Failed to decode bitmap from path, invalid arguments");
        /*boolean hasExtension = !TextUtils.isEmpty(MimeTypeMap.getFileExtensionFromUrl(imagePath));
        if(hasExtension && imagePath.contains(".")) imagePath=imagePath.substring(0,imagePath.lastIndexOf('.'));*/
        Uri fileUri = Uri.parse(imagePath);

        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(fileUri);
            BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            options.inSampleSize = calculateInSmapleSize(options, reqWidth, reqHeight);

            options.inJustDecodeBounds = false;
            inputStream = context.getContentResolver().openInputStream(fileUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            inputStream.close();
            return bitmap;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if(inputStream != null) {
                try {
                    inputStream.close();
                    throw new BitmapException("Could not found file on path : "+imagePath, e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw new BitmapException("Could not close stream", e1);
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            if(inputStream != null) {
                try {
                    inputStream.close();
                    throw new BitmapException("could not decode file : "+imagePath, e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw new BitmapException("could not close stream", e1);

                }
            }
            return null;
        }
    }

    private static int calculateInSmapleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){

        final int width = options.outWidth;
        final int height = options.outHeight;

        int inSampleSize = 1;

        while((height / inSampleSize > reqHeight) && (width / inSampleSize) > reqWidth){
            inSampleSize *= 2;
        }

        /*if(height > reqHeight || width > reqWidth){
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }*/
        return inSampleSize;
    }
}
