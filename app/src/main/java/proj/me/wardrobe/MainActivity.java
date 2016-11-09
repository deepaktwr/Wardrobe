package proj.me.wardrobe;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import proj.me.wardrobe.database.DbHelper;
import proj.me.wardrobe.databinding.ActivityMainBinding;
import proj.me.wardrobe.dialogs.CustomDialogFrag;
import proj.me.wardrobe.frag.ActivityCallback;
import proj.me.wardrobe.frag.FragmentCallback;
import proj.me.wardrobe.frag.bean.PagerBean;
import proj.me.wardrobe.helper.Utils;
import proj.me.wardrobe.dialogs.ImageIntent;
import proj.me.wardrobe.receiver.NotificationReceiver;

public class MainActivity extends AppCompatActivity implements ActivityCallback, View.OnClickListener{

    ImageIntent imageIntent;
    ContainerModel containerModel;

    String TOPS_TAG, BOTTOMS_TAG;

    boolean isShuffling;

    HashMap<Integer, List<Integer>> favoriteItems;
    Animator heartAnim;
    ObjectAnimator shuffleAnim;

    DbHelper dbHelper;

    ImageView heartView;
    ImageView shuffle;

    Random twoRandom, fourRandom;

    //saved instance
    String fromTAG;
    String imagePath;
    int lastTopColor;
    int lastBottomColor;
    int lastTopImageId = -1;
    int lastBottomImageId = -1;
    int lastShuffleTopId = -1;
    int lastShuffleBottomId = -1;
    boolean wasDialogShowing;

    private static final String TOP_LEFT = "top_left";
    private static final String BOTTOM_RIGHT = "bottom_right";
    private static final String FAVORITE = "favorite";
    private static final String SHUFFLE = "shuffle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        twoRandom = new Random();
        fourRandom = new Random();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int density = displayMetrics.densityDpi;
        int scale = 1;
        switch(displayMetrics.densityDpi){
            case DisplayMetrics.DENSITY_MEDIUM:
                scale = 2;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                scale = 3;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                scale = 4;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                scale = 6;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                scale = 8;
                break;
            default:
                if(density < DisplayMetrics.DENSITY_MEDIUM){
                    scale = 2;
                }else if(density >= DisplayMetrics.DENSITY_MEDIUM && density < DisplayMetrics.DENSITY_HIGH){
                    scale = 3;
                }else if(density >= DisplayMetrics.DENSITY_HIGH && density < DisplayMetrics.DENSITY_XHIGH){
                    scale = 4;
                }else if(density >= DisplayMetrics.DENSITY_XHIGH && density < DisplayMetrics.DENSITY_XXHIGH){
                    scale = 6;
                }else if(density >= DisplayMetrics.DENSITY_XXHIGH && density < DisplayMetrics.DENSITY_XXXHIGH){
                    scale = 8;
                }else{
                    scale = 8;
                }
                break;
        }

        Utils.REQUIRED_WIDTH = 100 * scale;
        Utils.REQUIRED_HEIGHT = 120 * scale;

        dbHelper = DbHelper.getDbInstance(this);

        if(favoriteItems == null){
            //get it from db
            favoriteItems = dbHelper.getAllFavorites();
        }


        containerModel = new ContainerModel();

        imageIntent = new ImageIntent();
        lastTopColor = Utils.getVersionColor(this, R.color.colorPrimary);
        lastBottomColor = Utils.getVersionColor(this, R.color.colorAccent);

        if(savedInstanceState != null){
            fromTAG = savedInstanceState.getString("from_tag");
            imagePath = savedInstanceState.getString("image_path");
            lastTopColor = savedInstanceState.getInt("last_top");
            lastBottomColor = savedInstanceState.getInt("last_bottom");
            lastTopImageId = savedInstanceState.getInt("last_top_image");
            lastBottomImageId = savedInstanceState.getInt("last_bottom_image");
            wasDialogShowing = savedInstanceState.getBoolean("was_dialog_showing");
            lastShuffleTopId = savedInstanceState.getInt("last_shuffle_top");
            lastShuffleBottomId = savedInstanceState.getInt("last_shuffle_bottom");
        }

        ActivityMainBinding activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        activityMainBinding.setClickHandler(this);
        activityMainBinding.setContainer(containerModel);

        heartView = activityMainBinding.heart;
        shuffle = activityMainBinding.shuffle;

        containerModel.setTopBackgroundColor(lastTopColor);
        containerModel.setBottomBackgroundColor(lastBottomColor);

        if(wasDialogShowing) ((CustomDialogFrag)getSupportFragmentManager().findFragmentByTag(fromTAG+"myImageDialog")).setImageIntent(imageIntent);


        //check permission before this
        Intent intent = new Intent(Utils.NOTIFICATION_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Utils.REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE);
        if(pendingIntent == null) {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH");
            int hour = Integer.parseInt(simpleDateFormat.format(date));
            Calendar calendar = Calendar.getInstance();
            if(hour < 6){
                //this day
                calendar.setTime(date);
                calendar.set(Calendar.HOUR_OF_DAY, 6);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
            }else{
                //next day
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 6);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
            }

            pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Utils.REQUEST_CODE, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
            //alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),  /*24 * 60 * */60 * 1000, pendingIntent);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        TOPS_TAG = getString(R.string.top_left_frag);
        BOTTOMS_TAG = getString(R.string.bottom_right_frag);

        String tag = fragment.getTag();

        if(TOPS_TAG.equals(tag))
            //get tops list
            ((FragmentCallback) fragment).initializePager(true);
        else if(BOTTOMS_TAG.equals(tag))
            //get bottoms list
            ((FragmentCallback) fragment).initializePager(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.logMessage("result code"+resultCode+" imge"+imagePath+" from"+fromTAG);
        if(resultCode == Activity.RESULT_OK && !TextUtils.isEmpty(fromTAG)){
            imagePath = imageIntent.getImageResultPath(data, imagePath, this);
            ((FragmentCallback)getSupportFragmentManager().findFragmentByTag(fromTAG)).processImage(imagePath);
        }
        //after done
        fromTAG = null;
        imagePath = null;
    }

    @Override
    public void picIntentInitiated(String filePath) {
        imagePath = filePath;
        wasDialogShowing = false;
    }

    @Override
    public void setMarkerColor(int color, boolean isTop, int imageId, boolean isInitial) {
        if(isShuffling) return;
        //end anim
        if(heartAnim != null && heartAnim.isRunning()) heartAnim.end();
        boolean wasFavorite = isItemFavorite(lastTopImageId, lastBottomImageId);
        if(isTop){
            containerModel.setTopBackgroundColor(color);
            lastTopColor = color;
            lastTopImageId = imageId;
        } else{
            containerModel.setBottomBackgroundColor(color);
            lastBottomColor = color;
            lastBottomImageId = imageId;
        }

        boolean isFav = isItemFavorite(lastTopImageId, lastBottomImageId);
        if(isInitial){
            if(isFav) heartView.setImageResource(R.drawable.fill_heart);
            else heartView.setImageResource(R.drawable.empty_heart);
        }
        //if lastTopImageId and lastBottomImageId in collection then make favorite
        //else make favorite empty
        else{
            if(isItemFavorite(lastTopImageId, lastBottomImageId)){
                //start fill anim
                if(!wasFavorite) performAnimation(true);
            }else{
                //start empty anim
                if(wasFavorite) performAnimation(false);
            }
        }
    }

    @Override
    public void setMarkerColorShuffle(int topColor, int bottomColor, int topId, int bottomId) {
        if(!isShuffling) return;
        if(heartAnim != null && heartAnim.isRunning()) heartAnim.end();
        boolean wasFavorite = isItemFavorite(lastTopImageId, lastBottomImageId);

        containerModel.setTopBackgroundColor(topColor);
        lastTopColor = topColor;
        lastTopImageId = topId;

        containerModel.setBottomBackgroundColor(bottomColor);
        lastBottomColor = bottomColor;
        lastBottomImageId = bottomId;

        if(isItemFavorite(lastTopImageId, lastBottomImageId)){
            //start fill anim
            if(!wasFavorite) performAnimation(true);
        }else{
            //start empty anim
            if(wasFavorite) performAnimation(false);
        }
    }

    int counter = 0;
    @Override
    public void pagerAnimDone() {
        if(!isShuffling) return;
        if(counter < 1){
            counter++;
            return;
        }
        counter = 0;
        Utils.logMessage("end shuffle anim");
        stopShuffle();
    }

    @Override
    public void onClick(View view) {
        if(isShuffling){
            stopShuffle();
            return;
        }
        switch((String)view.getTag()){
            case TOP_LEFT:
                fromTAG = TOPS_TAG;
                wasDialogShowing = true;
                CustomDialogFrag customDialogFragTop = new CustomDialogFrag();
                customDialogFragTop.setImageIntent(imageIntent);
                customDialogFragTop.show(getSupportFragmentManager(), fromTAG+"myImageDialog");
                break;
            case BOTTOM_RIGHT:
                fromTAG = BOTTOMS_TAG;
                wasDialogShowing = true;
                CustomDialogFrag customDialogFragBottom = new CustomDialogFrag();
                customDialogFragBottom.setImageIntent(imageIntent);
                customDialogFragBottom.show(getSupportFragmentManager(), fromTAG+"myImageDialog");
                break;
            case FAVORITE:
                boolean isFav = isItemFavorite(lastTopImageId, lastBottomImageId);
                if(lastTopImageId == -1 || lastBottomImageId == -1 || isFav){
                    if(isFav) heartView.setImageResource(R.drawable.fill_heart);
                    break;
                }
                //if not already favorite, make entry
                //add to collection
                //make favorite
                makeItemFavorite(lastTopImageId, lastBottomImageId);
                //start fill anim
                performAnimation(true);
                //insert to db
                boolean isInserted = dbHelper.insertFavorite(lastTopImageId, lastBottomImageId);
                if(!isInserted){
                    Utils.showToast(this, "Could not make item favorite");
                    removeFavoriteEntry(lastTopImageId, lastBottomImageId);
                    performAnimation(false);
                }
                break;
            case SHUFFLE:
                if(lastTopImageId == -1 || lastBottomImageId == -1) break;
                //check if already not shuffling
                isShuffling = true;
                startShuffle();
                break;
        }
    }

    void startShuffle(){
        //if size less than 3 then select a random number(get a random number between a set of values) return that
        //else if remove last shuffle selected item
        //if no favorites go to 1
        //else get all favorite and if only one favorite return that
        //else select mostly used color by mixing two closest colors(mix two colors which difference is lowest)
        //get mix color and find closest color to that mix color return that


        //for top
        lastShuffleTopId = getShuffleId(lastShuffleTopId, true);
        //for bottom
        lastShuffleBottomId = getShuffleId(lastShuffleBottomId, false);

        Utils.logMessage("top : "+lastShuffleTopId+" bottom : "+lastShuffleBottomId);


        Utils.logMessage("start shuffle anim");
        //start shuffle anim
        shuffleAnim = ObjectAnimator.ofFloat(shuffle, "rotation", 0f, 360f);
        shuffleAnim.setDuration(350);
        shuffleAnim.start();

        //notify view pager
        int topColor = ((FragmentCallback)getSupportFragmentManager().findFragmentByTag(TOPS_TAG)).notifyPager(lastShuffleTopId);
        int bottomColor = ((FragmentCallback)getSupportFragmentManager().findFragmentByTag(BOTTOMS_TAG)).notifyPager(lastShuffleBottomId);

        setMarkerColorShuffle(topColor, bottomColor, lastShuffleTopId, lastShuffleBottomId);
    }

    int getShuffleId(int lastId, boolean isTop){
        int[] allImageIds = dbHelper.getAllImageIds(lastId, isTop);
        if(allImageIds.length < 3) return Utils.getRandom(allImageIds, twoRandom);

        int[] favoriteColors = dbHelper.getAllFavoriteColors(allImageIds, isTop);

        if(favoriteColors.length == 0) return Utils.getRandom(allImageIds, twoRandom);
        if(favoriteColors.length == 1) return dbHelper.getImageIdFromColor(allImageIds, favoriteColors[0], isTop);
        int[] twoFavs = Utils.getLeastTwoNumbers(favoriteColors);
        int mix = Utils.getMixedArgbColor(twoFavs);
        int num = fourRandom.nextInt(4);
        int[] allColors = null;
        switch(num){
            case 0:
                allColors = dbHelper.getAllColors(allImageIds, isTop);
                break;
            case 1:
                allColors = dbHelper.getAllColorsExceptFavColors(allImageIds, isTop, new int[]{twoFavs[0]});
                break;
            case 2:
                allColors = dbHelper.getAllColorsExceptFavColors(allImageIds, isTop, new int[]{twoFavs[1]});
                break;
            case 3:
                allColors = dbHelper.getAllColorsExceptFavColors(allImageIds, isTop, twoFavs);
                break;
        }
        return dbHelper.getImageIdFromColor(allImageIds, Utils.getClosestNumber(mix, allColors) , isTop);
    }


    void stopShuffle(){
        //end shuffle anim
        if(shuffleAnim != null && shuffleAnim.isRunning()) shuffleAnim.end();
        isShuffling = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        //end anim
        if(heartAnim != null && heartAnim.isRunning()) heartAnim.end();
        if(isShuffling) stopShuffle();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("from_tag", fromTAG);
        outState.putString("image_path", imagePath);
        outState.putInt("last_top", lastTopColor);
        outState.putInt("last_bottom", lastBottomColor);
        outState.putInt("last_top_image", lastTopImageId);
        outState.putInt("last_bottom_image", lastBottomImageId);
        outState.putBoolean("was_dialog_showing", wasDialogShowing);
        outState.putInt("last_shuffle_top", lastShuffleTopId);
        outState.putInt("last_shuffle_bottom", lastShuffleBottomId);
    }


    boolean isItemFavorite(int top, int bottom){
        if(!favoriteItems.containsKey(top)) return false;
        return favoriteItems.get(top).contains(bottom);
    }

    void makeItemFavorite(int top, int bottom){
        if(isItemFavorite(top, bottom)) return;
        //make db entry
        if(favoriteItems.containsKey(top)) favoriteItems.get(top).add(bottom);
        else{
            ArrayList<Integer> bottoms = new ArrayList<>();
            bottoms.add(bottom);
            favoriteItems.put(top, bottoms);
        }
    }

    void removeFavoriteEntry(int top, int bottom){
        if(!isItemFavorite(top, bottom) || !favoriteItems.containsKey(top)) return;
        favoriteItems.get(top).remove(bottom);
    }

    void performAnimation(final boolean isFill){
        if(heartAnim != null && heartAnim.isRunning()) heartAnim.end();
        heartAnim = AnimatorInflater.loadAnimator(this, isFill ? R.animator.fill_heart : R.animator.empty_heart);
        heartAnim.setTarget(heartView);
        heartAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                heartView.setImageResource(isFill ? R.drawable.fill_heart : R.drawable.empty_heart);
            }
            @Override
            public void onAnimationEnd(Animator animator) {}
            @Override
            public void onAnimationCancel(Animator animator) {}
            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        heartAnim.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageIntent = null;
        containerModel = null;
        TOPS_TAG = null;
        BOTTOMS_TAG = null;
        favoriteItems = null;
        heartAnim = null;
        heartView = null;
    }
}
