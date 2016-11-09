package proj.me.wardrobe.exceptions;

import proj.me.wardrobe.helper.Utils;

/**
 * Created by deepak on 8/11/16.
 */
public class BitmapException extends Throwable{
    public BitmapException(String message){
        super(message);
        Utils.logMessage(message);
    }

    public BitmapException(String message, Throwable throwable){
        super(message, throwable);
        Utils.logMessage(message);
    }

    public BitmapException(Throwable throwable){
        super(throwable);
        Utils.logMessage(throwable.getMessage());
    }
}
