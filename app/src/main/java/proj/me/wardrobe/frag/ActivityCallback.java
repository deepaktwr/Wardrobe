package proj.me.wardrobe.frag;

/**
 * Created by deepak on 8/11/16.
 */
public interface ActivityCallback {
    void picIntentInitiated(String filePath);
    void setMarkerColor(int color, boolean isTop, int imageId, boolean isInitial);
    void setMarkerColorShuffle(int topColor, int bottomColor, int topId, int bottomId);
    void pagerAnimDone();
}
