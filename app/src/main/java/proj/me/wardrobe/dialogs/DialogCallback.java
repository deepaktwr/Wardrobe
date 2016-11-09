package proj.me.wardrobe.dialogs;

import android.content.Context;

/**
 * Created by deepak on 8/11/16.
 */
public interface DialogCallback {
    void selectedItem(String value);
    Context getDialogContext();
}
