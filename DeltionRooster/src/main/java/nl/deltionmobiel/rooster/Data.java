package nl.deltionmobiel.rooster;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by corne on 4/15/14.
 */
public class Data {

    private static HashMap<String, Object> _cache = null;

    private DataListener dataListener;
    private Activity activity;
    private Context context;

    public Data(DataListener dataListener, Activity activity) {
        this.dataListener = dataListener;
        this.activity = activity;
    }

    public Data(Context context) {
        this.context = context;
    }

    public void update() {
        getData("departments.json", -1, Config.API_DEPARTMENTS, true);
        getData("groups.json", -1, Config.API_GROUPS, true);
        getTimes();
    }

    public void getDepartments() {
        getData("departments.json", 30, Config.API_DEPARTMENTS, true);
    }

    public void getGroups() {
        getData("groups.json", 30, Config.API_GROUPS, true);
    }

    public void getTimes() {
        Context c = context == null ? activity.getApplicationContext() : context;
        Integer group = Session.getGroupId(c);
        for (int i = -1; i <= 1; i++)
            if (group != -1) {
                String department = Session.getDepartment(c);

                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.setFirstDayOfWeek(Calendar.MONDAY);
                cal.set(Calendar.WEEK_OF_YEAR, Session.getWeek() + i);
                cal.set(Calendar.YEAR, Session.getYear());
                int year = cal.get(Calendar.YEAR);
                String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
                String month = String.format("%02d", cal.get(Calendar.MONTH) + 1);

                StringBuilder filename = new StringBuilder();
                filename.append(department);
                filename.append('_');
                filename.append(group);
                filename.append("_");
                filename.append(year);
                filename.append('-');
                filename.append(month);
                filename.append('-');
                filename.append(day);
                filename.append(".json");

                StringBuilder url = new StringBuilder();
                url.append(department);
                url.append('/');
                url.append(group);
                url.append('/');
                url.append(year);
                url.append('-');
                url.append(month);
                url.append('-');
                url.append(day);

                // getData("52_""_2014-05-12.json", -1, "52/AO3A/2014-05-12");
                getData(filename.toString(), -1, url.toString(), i == 0);
            }
    }

    private static void _setCache(Object json, String type) {
        if (_cache == null) {
            _cache = new HashMap<String, Object>();
        }
        _cache.put(type, json);
    }

    private void getData(final String filename, final int days, final String jsonUrl, final boolean reportBack) {
        Object cached = null;
        if (_cache != null) {
            cached = _cache.get(jsonUrl);
        }

        if (cached == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean bReportBack = reportBack;
                    if(dataListener == null) bReportBack = false;
                    try {
                        Object json = null;
                        String jsonStringFromFile = "";
                        File file = new File(Environment.getExternalStorageDirectory() + "/.deltionroosterapp/" + filename);
                        if (file.exists()) {
                            Date curDate = new Date();
                            // current time - days available
                            curDate.setTime(curDate.getTime() - (days * 1000 * 60 * 60 * 24));
                            if (file.lastModified() < curDate.getTime() || days == -1) {
                                jsonStringFromFile = FileUtils.readFileToString(file);
                                if (jsonStringFromFile.length() != 0) {
                                    try {
                                        if (jsonStringFromFile.charAt(0) == '[')
                                            json = new JSONArray(jsonStringFromFile);
                                        else if (jsonStringFromFile.charAt(0) == '{') {
                                            JSONObject j = new JSONObject(jsonStringFromFile);
                                            if (j.has("error")) {
                                                if (bReportBack) dataListener.noDataAvailable();
                                                return;
                                            }
                                            json = j;
                                        } else if(bReportBack && days != -1) {
                                            Log.w("DeltionRoosterApp", "no valid data!");
                                            dataListener.noDataAvailable();
                                            return;
                                        }
                                        _setCache(json, jsonUrl);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if (days != -1) {
                                        if (bReportBack) dataListener.onDataLoaded(json);
                                        return;
                                    }
                                }
                            }
                        }

                        // check internet connectivity
                        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                        if (networkInfo == null || !networkInfo.isConnected()) {
                            if (bReportBack) {
                                if (json == null) dataListener.noDataAvailable();
                                else dataListener.onDataLoaded(json);
                            }
                            return;
                        }
                        String jsonString = JSONParser.getJSONFromUrl(Config.API_URL + jsonUrl);
                        if (!jsonStringFromFile.equals("") && !jsonStringFromFile.equals(jsonString)) {
                            activity.runOnUiThread(new Runnable() {
                                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                @Override
                                public void run() {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(),
                                            0, intent, 0);

                                    Notification.Builder n = new Notification.Builder(activity)
                                            .setContentTitle(activity.getString(R.string.notification_title))
                                            .setContentText(activity.getString(R.string.notification))
                                            .setSmallIcon(android.R.drawable.stat_sys_warning)
                                            .setAutoCancel(true)
                                            .setContentIntent(pendingIntent);

                                    NotificationManager notificationManager =
                                            (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

                                    notificationManager.notify(0, Build.VERSION.SDK_INT < 16 ? n.getNotification() : n.build());
                                }
                            });
                        }
                        FileUtils.write(file, jsonString);
                        if (jsonString == null || jsonString.length() == 0) {
                            if (bReportBack) dataListener.noDataAvailable();
                            return;
                        }
                        if (jsonString.charAt(0) == '[') json = new JSONArray(jsonString);
                        else {
                            JSONObject j = new JSONObject(jsonString);
                            if (j.has("error")) {
                                if (bReportBack) dataListener.noDataAvailable();
                                return;
                            }
                            json = j;
                        }
                        if (bReportBack) dataListener.onDataLoaded(json);
                        _setCache(json, jsonUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (bReportBack) dataListener.noDataAvailable();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (bReportBack) dataListener.noDataAvailable();
                    }
                }
            }).start();
        } else {
            if (reportBack && dataListener != null) dataListener.onDataLoaded(cached);
        }
    }
}
