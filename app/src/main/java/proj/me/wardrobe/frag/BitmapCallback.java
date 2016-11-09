package proj.me.wardrobe.frag;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by deepak on 8/11/16.
 */
public interface BitmapCallback {
    void bitmapGenerated(Bitmap bitmap, String path);
    Context getViewContext();
}
