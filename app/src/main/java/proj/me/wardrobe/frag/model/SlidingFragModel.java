package proj.me.wardrobe.frag.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import proj.me.wardrobe.BR;

/**
 * Created by deepak on 7/11/16.
 */
public class SlidingFragModel extends BaseObservable{
    private boolean isEmpty;
    private boolean isTop;

    @Bindable
    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
        notifyPropertyChanged(BR.empty);
    }

    @Bindable  public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
        notifyPropertyChanged(BR.top);
    }
}
