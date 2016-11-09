package proj.me.wardrobe.helper;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import proj.me.wardrobe.exceptions.BitmapException;
import proj.me.wardrobe.frag.BitmapCallback;

/**
 * Created by deepak on 8/11/16.
 */
public class BitmapHandlerTask extends AsyncTask<String, Void, Object[]> {

    BitmapCallback bitmapCallback;
    public BitmapHandlerTask(BitmapCallback bitmapCallback){
        this.bitmapCallback = bitmapCallback;
    }
    @Override
    protected Object[] doInBackground(String... imagePath) {
        Object[] objects = new Object[2];
        Bitmap bitmap = null;
        try {
            bitmap = BitmapHandler.decodeBitmapFromPath(Utils.REQUIRED_WIDTH, Utils.REQUIRED_HEIGHT,
                    imagePath[0], bitmapCallback.getViewContext());
        } catch (BitmapException e) {
            e.printStackTrace();
        }
        objects[0] = bitmap;
        objects[1] = imagePath[0];

        return objects;
    }

    @Override
    protected void onPostExecute(Object[] object) {
        super.onPostExecute(object);
        bitmapCallback.bitmapGenerated(object[0] == null ? null : (Bitmap)object[0], object[1] == null ? null : (String)object[1]);
    }
}
