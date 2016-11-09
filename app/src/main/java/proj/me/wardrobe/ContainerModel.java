package proj.me.wardrobe;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import proj.me.wardrobe.BR;

/**
 * Created by deepak on 8/11/16.
 */
public class ContainerModel extends BaseObservable {
    private int topBackgroundColor;
    private int bottomBackgroundColor;
    private boolean isFavorite;

    @Bindable public int getTopBackgroundColor() {
        return topBackgroundColor;
    }

    public void setTopBackgroundColor(int topBackgroundColor) {
        this.topBackgroundColor = topBackgroundColor;
        notifyPropertyChanged(BR.topBackgroundColor);
    }

    @Bindable public int getBottomBackgroundColor() {
        return bottomBackgroundColor;
    }

    public void setBottomBackgroundColor(int bottomBackgroundColor) {
        this.bottomBackgroundColor = bottomBackgroundColor;
        notifyPropertyChanged(BR.bottomBackgroundColor);
    }

    @Bindable public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
        notifyPropertyChanged(BR.favorite);
    }
}
