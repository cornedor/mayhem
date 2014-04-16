package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by corne on 4/15/14.
 */
public class Data {

    private static JSONObject _departmentsCache = null;
    private static JSONObject _groupsCache = null;

    private DataListener dataListener;
    private Activity activity;

    public Data(DataListener dataListener, Activity activity) {
        this.dataListener = dataListener;
        this.activity = activity;
    }

    public void getDepartments() {
        if(_departmentsCache == null) {
            getData("departments.json", 30, Config.API_DEPARTMENTS);
        } else {
            dataListener.onDataLoaded(_departmentsCache);
        }
    }

    public void getGroups() {
        if(_groupsCache == null) {
            getData("groups.json", 30, Config.API_GROUPS);
        } else {
            dataListener.onDataLoaded(_groupsCache);
        }
    }

    private static void set_departmentsCache(JSONObject json) {
        _departmentsCache = json;
    }

    private static void set_groupsCache(JSONObject json) {
        _groupsCache = json;
    }

    private void setCache(JSONObject json, String type) {
        if(type.equals(Config.API_DEPARTMENTS)) {
            set_departmentsCache(json);
        } else if(type.equals(Config.API_DEPARTMENTS)) {
            set_groupsCache(json);
        }
    }

    public void getData(final String filename, final int days, final String jsonUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    File file = new File(Environment.getExternalStorageDirectory() + "/.deltionroosterapp/" + filename);
                    if(file.exists()) {
                        Date curDate = new Date();
                        curDate.setTime( curDate.getTime() - (days*1000*60*60*24));
                        if(file.lastModified() > curDate.getTime()) {
                            String jsonString = FileUtils.readFileToString(file);
                            JSONObject json = new JSONObject(jsonString);
                            dataListener.onDataLoaded(json);
                            setCache(json, jsonUrl);
                            return;
                        }
                    }

                    // check internet connectivity
                    ConnectivityManager connManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                    if(networkInfo == null || !networkInfo.isConnected()) {
                        dataListener.noDataAvailable();
                        return;
                    }
                    JSONParser parser = new JSONParser();
                    String jsonString = parser.getJSONFromUrl(Config.API_URL + jsonUrl);
                    FileUtils.write(file, jsonString);
                    JSONObject json = new JSONObject(jsonString);
                    dataListener.onDataLoaded(json);
                    setCache(json, jsonUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
