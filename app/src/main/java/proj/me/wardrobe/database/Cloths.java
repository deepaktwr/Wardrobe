package proj.me.wardrobe.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import proj.me.wardrobe.frag.bean.PagerBean;

/**
 * Created by deepak on 9/11/16.
 */
public class Cloths {
    static final String TABLE = "Cloths";

    static final String CLOTH_ID = "cloth_id";
    private static final String CLOTH_PATH = "path";
    static final String CLOTH_COLOR = "color";
    static final String IS_TOP = "is_top";

    static final String CREATE_TABLE = "CREATE TABLE " + TABLE +
            DbHelper.OPEN_BRAC +
            CLOTH_ID + " integer primary key, "+CLOTH_PATH+" varchar not null,"+CLOTH_COLOR+" integer, " +IS_TOP+" boolean default 0"+
            DbHelper.CLOSE_BRAC;

    public int insertCloth(SQLiteDatabase sqLiteDatabase, PagerBean pagerBean, boolean isTop){
        String query = "insert into "+TABLE
                +DbHelper.OPEN_BRAC+
                CLOTH_PATH+DbHelper.COMMA+CLOTH_COLOR+DbHelper.COMMA+IS_TOP
                +DbHelper.CLOSE_BRAC+
                " values (?,?,?)";

        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{pagerBean.getImagePath(),""+pagerBean.getPaletteColor(),""+isTop});
        cursor.moveToFirst();
        cursor.close();
        return getLastEntry(sqLiteDatabase);
    }

    int getLastEntry(SQLiteDatabase sqLiteDatabase){
        String query = "select "+CLOTH_ID+" from "+TABLE+" order by "+CLOTH_ID+" desc limit 1";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{});
        int id = 0;
        if(cursor.moveToFirst()) id = cursor.getInt(cursor.getColumnIndex(CLOTH_ID));
        cursor.close();
        return id;
    }


    List<PagerBean> getCloths(SQLiteDatabase sqLiteDatabase, boolean isTop){
        String query = "select * from "+TABLE+" where "+IS_TOP+" = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{""+isTop});
        List<PagerBean> pagerBeanList = new ArrayList<>();
        if(!cursor.moveToFirst()){
            cursor.close();
            return pagerBeanList;
        }
        do{
            PagerBean pagerBean = new PagerBean();
            pagerBean.setImageId(cursor.getInt(cursor.getColumnIndex(CLOTH_ID)));
            pagerBean.setPaletteColor(cursor.getInt(cursor.getColumnIndex(CLOTH_COLOR)));
            pagerBean.setImagePath(cursor.getString(cursor.getColumnIndex(CLOTH_PATH)));
            pagerBeanList.add(pagerBean);
        }while(cursor.moveToNext());

        cursor.close();
        return pagerBeanList;
    }

    public int[] getAllImageIds(SQLiteDatabase sqLiteDatabase, int lastId, boolean isTop){
        String countQ = "select count"+DbHelper.OPEN_BRAC+CLOTH_ID+DbHelper.CLOSE_BRAC+" as total from "+TABLE+" where "+IS_TOP+" = ?";
        Cursor cursor = sqLiteDatabase.rawQuery(countQ, new String[]{""+isTop});

        int total = 0;
        if(cursor.moveToFirst()) total = cursor.getInt(cursor.getColumnIndex("total"));
        cursor.close();


        Cursor imageCursor = null;
        if(total < 3){
            String imageQ = "select "+CLOTH_ID+" from "+TABLE+" where "+IS_TOP+" = ?";
            imageCursor = sqLiteDatabase.rawQuery(imageQ, new String[]{""+isTop});
        } else{
            String imageQ = "select "+CLOTH_ID+" from "+TABLE+" where "+IS_TOP+" = ? and "+CLOTH_ID+" <> ?";
            imageCursor = sqLiteDatabase.rawQuery(imageQ, new String[]{""+isTop, ""+lastId});
        }

        int[] imageIds = new int[imageCursor.getCount()];

        if(!imageCursor.moveToFirst()){
            imageCursor.close();
            return imageIds;
        }

        int i = 0;
        do{
            imageIds[i] = imageCursor.getInt(imageCursor.getColumnIndex(CLOTH_ID));
            i++;
        }while (imageCursor.moveToNext());

        imageCursor.close();
        return imageIds;
    }

    public int getImageIdFromColor(SQLiteDatabase sqLiteDatabase, int[] imageIds, int color, boolean isTop){
        String ids = Arrays.toString(imageIds);
        String query = "select "+CLOTH_ID+" from "+TABLE+" where "+CLOTH_COLOR+" = ? and "+IS_TOP+" = ? and "+CLOTH_ID+" in "+DbHelper.OPEN_BRAC+
                ids.substring(1, ids.length()-1)
                +DbHelper.CLOSE_BRAC;

        Cursor cursor = sqLiteDatabase.rawQuery(query, new String[]{""+color,""+isTop});
        int clothId = -1;
        if(!cursor.moveToFirst()){
            cursor.close();
            return clothId;
        }
        clothId = cursor.getInt(cursor.getColumnIndex(CLOTH_ID));
        cursor.close();

        return clothId;
    }

    public int[] getAllColors(SQLiteDatabase sqLiteDatabases, int[] imageIds, boolean isTop){
        String ids = Arrays.toString(imageIds);
        String query = "select "+CLOTH_COLOR+" from "+TABLE+" where "+IS_TOP+" = ? and "+CLOTH_ID+" in "+
                DbHelper.OPEN_BRAC+ids.substring(1, ids.length()-1)+DbHelper.CLOSE_BRAC;

        Cursor cursor = sqLiteDatabases.rawQuery(query, new String[]{""+isTop});

        if(!cursor.moveToFirst()){
            cursor.close();
            return new int[0];
        }

        int[] allColors = new int[cursor.getCount()];

        int i = 0;
        do{
            allColors[i] = cursor.getInt(cursor.getColumnIndex(CLOTH_COLOR));
            i++;
        }while (cursor.moveToNext());

        cursor.close();
        return allColors;
    }

    public int[] getAllColorsExceptFavColors(SQLiteDatabase sqLiteDatabases, int[] imageIds, boolean isTop, int[] favColors){
        String ids = Arrays.toString(imageIds);
        String favIds = Arrays.toString(favColors);
        String query = "select "+CLOTH_COLOR+" from "+TABLE+" where "+IS_TOP+" = ? and "+CLOTH_ID+" in "+
                DbHelper.OPEN_BRAC+ids.substring(1, ids.length()-1)+DbHelper.CLOSE_BRAC+
                " and "+CLOTH_COLOR+" not in "+
                DbHelper.OPEN_BRAC+favIds.substring(1, favIds.length()-1)+DbHelper.CLOSE_BRAC;


        Cursor cursor = sqLiteDatabases.rawQuery(query, new String[]{""+isTop});

        if(!cursor.moveToFirst()){
            cursor.close();
            return new int[0];
        }

        int[] allColors = new int[cursor.getCount()];

        int i = 0;
        do{
            allColors[i] = cursor.getInt(cursor.getColumnIndex(CLOTH_COLOR));
            i++;
        }while (cursor.moveToNext());

        cursor.close();
        return allColors;
    }
}
