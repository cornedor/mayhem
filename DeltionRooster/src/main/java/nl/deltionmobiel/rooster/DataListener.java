package nl.deltionmobiel.rooster;

import org.json.JSONObject;

/**
 * Created by corne on 4/15/14.
 */
public interface DataListener {
    void onDataLoaded(Object json);
    void noDataAvailable();
}
