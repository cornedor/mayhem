package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarOutputStream;

/**
 * Created by corne on 4/15/14.
 */
public class Data {

    private static HashMap<String, Object> _cache = null;

    private DataListener dataListener;
    private Activity activity;

    public Data(DataListener dataListener, Activity activity) {
        this.dataListener = dataListener;
        this.activity = activity;
    }

    public void getDepartments() {
        getData("departments.json", 30, Config.API_DEPARTMENTS);
    }

    public void getGroups() {
        getData("groups.json", 30, Config.API_GROUPS);
    }

    public void getTimes() {
        Integer group = Session.getGroupId(activity);
        if(group != -1) {
            String department = Session.getDepartment(activity);

            Calendar cal = Calendar.getInstance();
            System.out.println("WEEK: " + Session.getWeek(activity));
            cal.set(Calendar.WEEK_OF_YEAR, Session.getWeek(activity));
            System.out.println(cal.getFirstDayOfWeek());

            StringBuilder filename = new StringBuilder();
            filename.append(department);
            filename.append("_");
            filename.append(group);
            filename.append("_2014-05-12.json");

            StringBuilder url = new StringBuilder();
            url.append(department);
            url.append("/");
            url.append(group);
            url.append("/2014-05-12");

            System.out.println(url + "::::" + filename);


            // getData("52_""_2014-05-12.json", -1, "52/AO3A/2014-05-12");
            getData(filename.toString(), -1, url.toString());
        }
    }

    private static void _setCache(Object json, String type) {
        if(_cache == null) {
            _cache = new HashMap<String, Object>();
        }
        _cache.put(type, json);
    }

    public void getData(final String filename, final int days, final String jsonUrl) {
        Object cached = null;
        if(_cache != null) {
            cached = _cache.get(jsonUrl);
        }

        if(cached == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Object json = null;
                        File file = new File(Environment.getExternalStorageDirectory() + "/.deltionroosterapp/" + filename);
                        if (file.exists() && false) {
                            Date curDate = new Date();
                            curDate.setTime(curDate.getTime() - (days * 1000 * 60 * 60 * 24));
                            if (file.lastModified() < curDate.getTime() || days == -1) {
                                String jsonString = FileUtils.readFileToString(file);
                                if(jsonString.length() != 0) {
                                    if (jsonString.charAt(0) == '[') {
                                        json = new JSONArray(jsonString);
                                    } else {
                                        json = new JSONObject(jsonString);
                                    }

                                    _setCache(json, jsonUrl);
                                    if (days != -1) {
                                        dataListener.onDataLoaded(json);
                                        return;
                                    }
                                }
                            }
                        }

                        // check internet connectivity
                        ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                        if (networkInfo == null || !networkInfo.isConnected()) {
                            if(json == null) {
                                dataListener.noDataAvailable();
                            } else {
                                dataListener.onDataLoaded(json);
                            }
                            return;
                        }
                        JSONParser parser = new JSONParser();
                        String jsonString = parser.getJSONFromUrl(Config.API_URL + jsonUrl);
                        System.out.println(jsonString);
                        FileUtils.write(file, jsonString);
                        if(jsonString.length() == 0) {
                            dataListener.noDataAvailable();
                            return;
                        }
                        if(jsonString.charAt(0) == '[') {
                            json = new JSONArray(jsonString);
                        } else {
                            json = new JSONObject(jsonString);
                        }
                        dataListener.onDataLoaded(json);
                        _setCache(json, jsonUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            dataListener.onDataLoaded(cached);
        }
    }
}
