package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by corne on 4/23/14.
 */
public class Session {
    private static String _group = null;

    public static void setGroup(String group) {
        _group = group;
    }

    public static String getGroup(Activity activity) {
        if(_group == null) {
            SharedPreferences prefs = activity.getSharedPreferences(Config.ROOSTER_PREFS, 0);
            setGroup(prefs.getString(Config.SELECTED_GROUP, activity.getString(R.string.no_group)));

        }
        return _group;
    }
}
