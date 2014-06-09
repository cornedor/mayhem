package nl.deltionmobiel.rooster;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Html;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by corne on 4/8/14.
 */
public class GroupAdapter extends FragmentPagerAdapter {

    private Context context;
    private JSONObject json;

    public GroupAdapter(Context context, FragmentManager fm) {
        super(fm);
        // this.json = json;
        this.context = context;
    }

    /**
     * Set the json with departments and classes
     *
     * @param json the json with departments and classes
     */
    public void setJson(JSONObject json) {
        this.json = json;
    }

    /**
     * Get a fragment for a department filled with classes
     *
     * @param position the department position
     * @return the DepartmentFragment
     */
    @Override
    public Fragment getItem(int position) {
        // DepartmentFragment frag = new DepartmentFragment(position);
        DepartmentFragment frag = null;
        try {
            String jsonName = json.names().getString(position);
            String name = json.getString(jsonName);
            name = Html.fromHtml(name).toString();
            frag = DepartmentFragment.newInstance(position, name, jsonName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return frag;
    }

    @Override
    public int getCount() {
        return json.length();
    }

}
