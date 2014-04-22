package nl.deltionmobiel.rooster;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                   ScheduleFragment.OnFragmentInteractionListener,
                   DepartmentFragment.OnFragmentInteractionListener {

    /**
     * Used to get preferences like current week or group
     */
    public static final String RoosterPrefs = "RoosterPrefs";
    private static final String SELECTED_GROUP = "selectedGroup";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, ScheduleFragment.newInstance())
//                .commit();
        FragmentManager fragmentManager = getSupportFragmentManager();
        SharedPreferences settings = getSharedPreferences(RoosterPrefs, 0);

        switch(position) {
            case 0:
                String selectedGroup = settings.getString(SELECTED_GROUP,"");
                if(selectedGroup.equals("")) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, new DepartmentsFragment())
                            .commit();
                    break;
                }else {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, ScheduleFragment.newInstance())
                            .commit();
                    break;
                }
            case 2:
                fragmentManager.beginTransaction()
                    .replace(R.id.container, new DepartmentsFragment())
                    .commit();
                break;
            case 4:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;

        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 0:
                mTitle = getString(R.string.rooster);
                break;
            case 1:
                mTitle = getString(R.string.select_week);
                break;
            case 2:
                mTitle = getString(R.string.select_class);
                break;
            case 3:
                mTitle = getString(R.string.today);
                break;
            case 4:
                mTitle = getString(R.string.action_settings);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_activity_actions, menu);
            restoreActionBar();
            if(isOnline())
                menu.removeItem(R.id.action_offline);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
