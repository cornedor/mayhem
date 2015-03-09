package nl.deltionmobiel.rooster;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        ScheduleFragment.OnFragmentInteractionListener,
        DepartmentFragment.OnFragmentInteractionListener,
        DataListener {

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

        Intent intent = getIntent();
        int openFragment = intent.getIntExtra(Config.OPEN_FRAGMENT, -1);
        if (openFragment != -1) {
            fragmentSwitcher(openFragment);
        }

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
        fragmentSwitcher(position);
    }
    
    private switchToScheduleFragment() {
        String selectedGroup = Session.getGroup(this);
        // no group selected, show de default selector
        if (selectedGroup.equals("") || selectedGroup.equals(getString(R.string.no_group))) {
            Session.selectDefault(true);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, new DepartmentsFragment())
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ScheduleFragment.newInstance())
                    .commit();
        }
    }

    /**
     * Change the fragment in the MainView, position 0 will show the ScheduleFragment and if there
     * is no default group initialized the DepartmentsFragment will be shown. Position 1 will show
     * the YearFragment, position 2 the DepartmentsFragment. Position 3 will do the same as position
     * 0 but with the current date and position 4 will open the SettingsActivity
     *
     * @param position
     */
    public void fragmentSwitcher(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        SharedPreferences settings = getSharedPreferences(Config.ROOSTER_PREFS, 0);

        Session.setCurrentFragment(position);

        switch (position) {
            case 0:
                switchToScheduleFragment();
                break;
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new YearFragment())
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, new DepartmentsFragment())
                        .commit();
                break;
            case 3:
                Session.setWeek(null);
                Session.setYear(null);
                switchToScheduleFragment();
                break;
            case 4:
                Session.setCurrentFragment(0); // @TODO: Dirty fix for back button
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;

        }
    }


    public void onSectionAttached(int position) {
        switch (position) {
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

    /**
     * This function will show an AlertDialog with a message to tell the user that old data is
     * being displayed.
     */
    public void showOffline() {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.offline_message_title))
                .setMessage(getString(R.string.offline_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(R.drawable.ic_action_network_wifi_fail)
                .show();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    /**
     * A function to check if there is a working connection to the internet
     *
     * @return if there is a internet connection
     */
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
        if (mNavigationDrawerFragment != null && !mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main_activity_actions, menu);
            restoreActionBar();
            if (isOnline()) {
                MenuItem item = menu.findItem(R.id.action_offline);
                assert item != null;
                item.setVisible(false);
            }
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_offline:
                showOffline();
                return true;
            case R.id.action_refresh:
                if (isOnline()) {
                    item.setVisible(false);
                    this.invalidateOptionsMenu();
                    new Data(this, this).update();
                } else {
                    item.setVisible(true);
                    this.invalidateOptionsMenu();
                    showOffline();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDataLoaded(Object json) {
        fragmentSwitcher(Session.getCurrentFragment());
    }

    @Override
    public void noDataAvailable() {

    }
}
