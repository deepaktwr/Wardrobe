package proj.me.wardrobe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;

import proj.me.wardrobe.frag.bean.PagerBean;

/**
 * Created by deepak on 9/11/16.
 */
public class DbHelper extends SQLiteOpenHelper {
    static final String OPEN_BRAC = "(";
    static final String CLOSE_BRAC = ")";
    static final String COMMA = ",";

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "Wardrobe.db";

    private static DbHelper dbHelper;
    public static DbHelper getDbInstance(Context context){
        if(dbHelper ==  null) dbHelper= new DbHelper(context.getApplicationContext());
        return dbHelper;
    }

    Cloths cloths;
    Favorites favorites;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        cloths = new Cloths();
        favorites = new Favorites();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Cloths.CREATE_TABLE);
        sqLiteDatabase.execSQL(Favorites.CREATE_TABLE);
    }

    public int insertCloth(PagerBean pagerBean, boolean isTop){
        return cloths.insertCloth(getWritableDatabase(), pagerBean, isTop);
    }

    public List<PagerBean> getCloths(boolean isTop){
        return cloths.getCloths(getReadableDatabase(), isTop);
    }

    public boolean insertFavorite(int top, int bottom){
        if(top == -1 || bottom == -1) return false;
        favorites.insertFavorite(getWritableDatabase(), top, bottom);
        return true;
    }

    public HashMap<Integer, List<Integer>> getAllFavorites(){
        return favorites.getAllFavorites(getReadableDatabase());
    }

    public int[] getAllImageIds(int lastId, boolean isTop){
        return cloths.getAllImageIds(getReadableDatabase(), lastId, isTop);
    }

    /*public List<PagerBean> getAllFavorites(int[] imageIds, boolean isTop){
        return favorites.getAllFavorites(getReadableDatabase(), imageIds, isTop);
    }*/

    public int[] getAllFavoriteColors(int[] imageIds, boolean isTop){
        return favorites.getAllFavoriteColors(getReadableDatabase(), imageIds, isTop);
    }

    public int[] getAllColors(int[] imageIds, boolean isTop){
        return cloths.getAllColors(getReadableDatabase(), imageIds, isTop);
    }
    public int[] getAllColorsExceptFavColors(int[] imageIds, boolean isTop, int[] favColors){
        return cloths.getAllColorsExceptFavColors(getReadableDatabase(), imageIds, isTop, favColors);
    }

    public int getImageIdFromColor(int[] imageIds, int color, boolean isTop){
        return cloths.getImageIdFromColor(getReadableDatabase(), imageIds, color, isTop);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
