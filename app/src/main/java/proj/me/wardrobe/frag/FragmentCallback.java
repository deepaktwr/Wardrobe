package proj.me.wardrobe.frag;

/**
 * Created by deepak on 8/11/16.
 */
public interface FragmentCallback extends BitmapCallback{
    void initializePager(boolean isTop);
    void processImage(String imagePath);

    int notifyPager(int imageId);
}
