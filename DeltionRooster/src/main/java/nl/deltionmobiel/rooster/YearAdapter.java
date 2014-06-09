package nl.deltionmobiel.rooster;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by corne on 20-5-14.
 */
public class YearAdapter extends FragmentPagerAdapter {
    Context context;

    public YearAdapter(Context context, FragmentManager fm) {
        super(fm);

        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        WeekFragment frag = null;
        frag = WeekFragment.newInstance(position);

        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
