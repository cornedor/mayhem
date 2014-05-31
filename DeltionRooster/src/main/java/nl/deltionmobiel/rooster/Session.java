package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by corne on 4/23/14.
 */
public class Session {
    private static String _group = null;
    private static String _department = null;
    private static Integer _groupId = null;
    private static Integer _week = null;
    private static Integer _currentFragment = null;
    private static Integer _year = null;
    private static Boolean _selectDefault = false;

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

    public static void setDepartment(String department) {
        _department = department;
    }

    public static String getDepartment(Activity activity) {
        if(_department == null) {
            SharedPreferences prefs = activity.getSharedPreferences(Config.ROOSTER_PREFS, 0);
            setDepartment(prefs.getString(Config.SELECTED_DEPARTMENT, activity.getString(R.string.no_group)));
        }
        return _department;
    }

    public static void setGroupId(Integer id) {
        _groupId = id;
    }

    public static Integer getGroupId(Activity activity) {
        if(_groupId == null) {
            SharedPreferences prefs = activity.getSharedPreferences(Config.ROOSTER_PREFS, 0);
            setGroupId(prefs.getInt(Config.SELECTED_GROUP_ID, -1));
        }
        return _groupId;
    }

    public static void setWeek(Integer week) {
        _week = week;
    }

    public static Integer getWeek() {
        if(_week == null) {
            _week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        }
        return _week;
    }

    public static void setYear(Integer year) {
        _year = year;
    }

    public static Integer getYear() {
        if(_year == null) {
            _year = Calendar.getInstance().get(Calendar.YEAR);
        }
        return _year;
    }

    public static void selectDefault(boolean b) {
        _selectDefault = b;
    }

    public static Boolean selectDefault() {
        return _selectDefault;
    }

    public static void setCurrentFragment(Integer position) {
        _currentFragment = position;
    }

    public static Integer getCurrentFragment() {
        return _currentFragment;
    }
}
