package proj.me.wardrobe.frag;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by deepak on 8/11/16.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    ArrayList<ImageFragment> imageFragmentList;
    public PagerAdapter(FragmentManager fm, ArrayList<ImageFragment> imageFragmentList) {
        super(fm);
        this.imageFragmentList = imageFragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return imageFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return imageFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
