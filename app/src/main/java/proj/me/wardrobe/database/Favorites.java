package proj.me.wardrobe.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import proj.me.wardrobe.frag.bean.PagerBean;
import proj.me.wardrobe.helper.Utils;

/**
 * Created by deepak on 9/11/16.
 */
public class Favorites {
    static final String TABLE = "Favorites";

    private static final String TOP_ID = "top_id";
    private static final String BOTTOM_ID = "bottom_id";

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE +
            DbHelper.OPEN_BRAC +
            TOP_ID + " integer not null, "+BOTTOM_ID+" integer not null"+
            DbHelper.CLOSE_BRAC;

    public void insertFavorite(SQLiteDatabase sqLiteDatabase, int top, int bottom){
        String query = "insert into "+TABLE+
                DbHelper.OPEN_BRAC+
                TOP_ID+DbHelper.COMMA+BOTTOM_ID+
                DbHelper.CLOSE_BRAC+" values(?,?)";

        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{""+top,""+bottom});
        cursor.moveToFirst();
        cursor.close();
    }

    public HashMap<Integer, List<Integer>> getAllFavorites(SQLiteDatabase sqLiteDatabase){
        String query = "select * from "+TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{});
        HashMap<Integer, List<Integer>> favorites = new HashMap<>();
        if(!cursor.moveToFirst()){
            cursor.close();
            return favorites;
        }

        do{
            int top = cursor.getInt(cursor.getColumnIndex(TOP_ID));
            int bottom = cursor.getInt(cursor.getColumnIndex(BOTTOM_ID));

            if(favorites.containsKey(top)) favorites.get(top).add(bottom);
            else{
                List<Integer> bottoms = new ArrayList<>();
                bottoms.add(bottom);
                favorites.put(top, bottoms);
            }
        }while(cursor.moveToNext());

        return favorites;
    }

    /*public List<PagerBean> getAllFavorites(SQLiteDatabase sqLiteDatabases, int[] imageIds, boolean isTop){
        String query = "select "+ Cloths.CLOTH_ID+", "+Cloths.CLOTH_COLOR+" from "+
                DbHelper.OPEN_BRAC
                +"select "+(isTop?TOP_ID:BOTTOM_ID)+" as "+Cloths.CLOTH_ID+" from "+TABLE
                +" where " + Cloths.CLOTH_ID +" in "+DbHelper.OPEN_BRAC+
                ""+ Arrays.toString(imageIds)
                +DbHelper.CLOSE_BRAC
                +DbHelper.CLOSE_BRAC+
                DbHelper.OPEN_BRAC
                + " join select "+Cloths.CLOTH_ID+", "+Cloths.CLOTH_COLOR+" from "+Cloths.TABLE + " where "+Cloths.IS_TOP+" = "+isTop
                +DbHelper.CLOSE_BRAC
                +" using"+DbHelper.OPEN_BRAC+
                Cloths.CLOTH_ID
                +DbHelper.CLOSE_BRAC;

        Cursor cursor = sqLiteDatabases.rawQuery(query, new String[]{});
        if(!cursor.moveToFirst()){
            cursor.close();
            return new ArrayList<>();
        }

        List<PagerBean> pagerBeanList = new ArrayList<>();
        do{
            PagerBean pagerBean = new PagerBean();
            pagerBean.setImageId(cursor.getInt(cursor.getColumnIndex(Cloths.CLOTH_ID)));
            pagerBean.setPaletteColor(cursor.getInt(cursor.getColumnIndex(Cloths.CLOTH_COLOR)));
            pagerBeanList.add(pagerBean);
        }while(cursor.moveToNext());

        cursor.close();
        return pagerBeanList;
    }*/

    public int[] getAllFavoriteColors(SQLiteDatabase sqLiteDatabases, int[] imageIds, boolean isTop){
        String ids = Arrays.toString(imageIds);
        String query = "select "+ Cloths.CLOTH_COLOR+" from "+
                DbHelper.OPEN_BRAC

                +"select "+(isTop?TOP_ID:BOTTOM_ID)+" as "+Cloths.CLOTH_ID+" from "+TABLE

                +DbHelper.CLOSE_BRAC+
                " join "
                + Cloths.TABLE

                +" using"
                +DbHelper.OPEN_BRAC+
                Cloths.CLOTH_ID
                +DbHelper.CLOSE_BRAC

                +" where " + Cloths.CLOTH_ID +" in "
                +DbHelper.OPEN_BRAC+
                ""+ ids.substring(1, ids.length() - 1)
                +DbHelper.CLOSE_BRAC +" and "+Cloths.IS_TOP+" = ?";
        Utils.logMessage(query);

        Cursor cursor = sqLiteDatabases.rawQuery(query, new String[]{""+isTop});
        if(!cursor.moveToFirst()){
            cursor.close();
            return new int[0];
        }

        int[] favoriteColors = new int[cursor.getCount()];
        int i = 0;
        do{
            favoriteColors[i] = cursor.getInt(cursor.getColumnIndex(Cloths.CLOTH_COLOR));
            i++;
        }while(cursor.moveToNext());

        cursor.close();
        return favoriteColors;
    }
}
