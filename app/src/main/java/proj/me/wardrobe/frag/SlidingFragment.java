package proj.me.wardrobe.frag;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import proj.me.wardrobe.R;
import proj.me.wardrobe.database.DbHelper;
import proj.me.wardrobe.databinding.SliderFragBinding;
import proj.me.wardrobe.frag.bean.PagerBean;
import proj.me.wardrobe.frag.model.SlidingFragModel;
import proj.me.wardrobe.helper.BitmapHandlerTask;
import proj.me.wardrobe.helper.Utils;

/**
 * Created by deepak on 7/11/16.
 */
public class SlidingFragment extends LruFragment implements FragmentCallback{
    SlidingFragModel slidingFragModel;
    ActivityCallback activityCallback;
    ViewPager viewPager;
    Context context;
    BitmapHandlerTask bitmapHandlerTask;
    boolean isRequestCancelled;
    ArrayList<ImageFragment> imageFragmentArrayList;

    private static final int defaultColor = Color.parseColor("#ffffffff");

    boolean isTop;
    List<PagerBean> pagerBeanList;

    //save instance
    int lastSelectedItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        try{
            activityCallback = (ActivityCallback) context;
        }catch (ClassCastException e){
            Utils.logMessage("activity must implement callback");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*if(savedInstanceState != null){
            pagerBeanList = savedInstanceState.getParcelableArrayList("pager_list");
            isTop = savedInstanceState.getBoolean("is_top");
        }else if(pagerBeanList == null){
            //we may get data from db here
            pagerBeanList = new ArrayList<>();
        }*/

        if(pagerBeanList == null){
            //get data from db
            pagerBeanList = DbHelper.getDbInstance(context).getCloths(isTop);
        }

        if(savedInstanceState != null) lastSelectedItem = savedInstanceState.getInt("last_position");

        SliderFragBinding sliderFragBinding = DataBindingUtil.bind(inflater.inflate(R.layout.slider_frag, container, false));
        slidingFragModel = new SlidingFragModel();
        sliderFragBinding.setSlider(slidingFragModel);
        return sliderFragBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        slidingFragModel.setTop(isTop);
        slidingFragModel.setEmpty(pagerBeanList == null || pagerBeanList.isEmpty());

        imageFragmentArrayList = new ArrayList<>();

        for(PagerBean pagerBean : pagerBeanList){
            ImageFragment imageFragment = ImageFragment.newInstance(isTop, pagerBean.getImagePath());
            imageFragmentArrayList.add(imageFragment);
        }

        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager(), imageFragmentArrayList);
        viewPager = (ViewPager) view.findViewById(R.id.cloth_pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                lastSelectedItem = position;
                activityCallback.setMarkerColor(pagerBeanList.get(position).getPaletteColor(), isTop, pagerBeanList.get(position).getImageId(), false);
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE) activityCallback.pagerAnimDone();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(lastSelectedItem == viewPager.getCurrentItem()) {
                    if (pagerBeanList.size() > 0)
                        activityCallback.setMarkerColor(pagerBeanList.get(lastSelectedItem).getPaletteColor(),
                                isTop, pagerBeanList.get(lastSelectedItem).getImageId(), true);
                }else viewPager.setCurrentItem(lastSelectedItem);
            }
        }, 20);
    }

    @Override
    public void initializePager(boolean isTop) {
        this.isTop = isTop;
    }

    @Override
    void makeTableEntry(final Bitmap bitmap, final String path) {
        Utils.logMessage("again : "+path);
        if(viewPager == null || !isAdded()) return;
        Utils.logMessage("in : "+path);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                if(viewPager == null || !isAdded()) return;
                Utils.logMessage("palette : "+path);
                storeImageBitmap(path, bitmap);

                Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                int vibrantPopulation = vibrantSwatch == null ? 0 : vibrantSwatch.getPopulation();
                int mutedPopulation = mutedSwatch == null ? 0 : mutedSwatch.getPopulation();


                //make entry-----------
                PagerBean pagerBean = new PagerBean();
                pagerBean.setImagePath(path);
                pagerBean.setPaletteColor(vibrantPopulation >= mutedPopulation ? palette.getVibrantColor(defaultColor) :
                        palette.getMutedColor(defaultColor));
                //-----------
                pagerBean.setImageId(DbHelper.getDbInstance(context).insertCloth(pagerBean, isTop));


                pagerBeanList.add(pagerBean);
                slidingFragModel.setEmpty(pagerBeanList == null || pagerBeanList.isEmpty());

                ImageFragment imageFragment = ImageFragment.newInstance(isTop, path);
                imageFragmentArrayList.add(imageFragment);

                if(imageFragmentArrayList.size() == 1) activityCallback.setMarkerColor(pagerBean.getPaletteColor(), isTop, pagerBean.getImageId(), false);

                viewPager.getAdapter().notifyDataSetChanged();
                viewPager.setCurrentItem(imageFragmentArrayList.size() - 1);
            }
        });
    }

    @Override
    public void bitmapGenerated(Bitmap bitmap, String path) {
        Utils.logMessage("got : "+path);
        makeTableEntry(bitmap, path);
    }

    @Override
    public Context getViewContext() {
        return context;
    }

    @Override
    public void onStop() {
        super.onStop();
        isRequestCancelled = true;
        if(bitmapHandlerTask != null && !bitmapHandlerTask.isCancelled()) bitmapHandlerTask.cancel(isRequestCancelled);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("last_position", lastSelectedItem);
    }

    @Override
    public void processImage(String imagePath) {
        Utils.logMessage("loading : "+imagePath);
        bitmapHandlerTask = new BitmapHandlerTask(this);
        bitmapHandlerTask.execute(imagePath);
    }

    @Override
    public int notifyPager(int imageId) {
        int color = -1;
        for(int i = -1;++i<pagerBeanList.size();){
            if(imageId == pagerBeanList.get(i).getImageId()){
                color = pagerBeanList.get(i).getPaletteColor();
                viewPager.setCurrentItem(i);
                break;
            }
        }
        return color;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.unbindDrawables(viewPager, true, true);
        slidingFragModel = null;
        activityCallback = null;
        viewPager = null;
        context = null;
        bitmapHandlerTask = null;
        imageFragmentArrayList = null;
    }
}
