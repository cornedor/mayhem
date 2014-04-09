package nl.deltionmobiel.rooster;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by corne on 4/8/14.
 */
public class GroupAdapter extends FragmentPagerAdapter {

    Context context;

    public GroupAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // SectorFragment frag = new SectorFragment(position);
        SectorFragment frag = SectorFragment.newInstance(position);
        return (Fragment) frag;
    }

    @Override
    public int getCount() {
        return 4;
    }

}
