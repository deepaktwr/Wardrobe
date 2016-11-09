package proj.me.wardrobe.dialogs.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

/**
 * Created by deepak on 8/11/16.
 */
public class DialogModel extends BaseObservable{
    public ObservableField<String> text = new ObservableField<>("TAKE PHOTO");
    public ObservableInt backgroundColor = new ObservableInt(0);
}
