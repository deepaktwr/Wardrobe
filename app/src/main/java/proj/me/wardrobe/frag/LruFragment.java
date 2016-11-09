package proj.me.wardrobe.frag;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;

/**
 * Created by deepak on 8/11/16.
 */
public abstract class LruFragment extends Fragment {

    LruCache<String, Bitmap> imageCache;

    final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() / 1024);

    final int CACHE_SIZE = MAX_MEMORY / (16 * 2);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        imageCache = new LruCache<String, Bitmap>(CACHE_SIZE){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void storeImageBitmap(String key, Bitmap bitmap){
        if(getImageBitmap(key) == null) imageCache.put(key, bitmap);
    }

    public Bitmap getImageBitmap(String key){
        return imageCache.get(key);
    }

    abstract void makeTableEntry(Bitmap bitmap, String path);
}
