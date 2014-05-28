package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
* A simple {@link android.support.v4.app.Fragment} subclass.
* Activities that contain this fragment must implement the
* {@link DepartmentFragment.OnFragmentInteractionListener} interface
* to handle interaction events.
* Use the {@link DepartmentFragment#newInstance} factory method to
* create an instance of this fragment.
*
*/
public class DepartmentFragment extends Fragment implements DataListener {

    private OnFragmentInteractionListener mListener;
    private int position;
    private String name;
    private ListView listView;
    private ProgressBar progressBar;
    private String jsonName;
    private HashMap<String, Integer> dList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DepartmentFragment.
     */
    public static DepartmentFragment newInstance(int position, String name, String jsonName) {
        DepartmentFragment fragment = new DepartmentFragment();
        fragment.setName(name);
        fragment.setPosition(position);
        fragment.setJSONName(jsonName);
        return fragment;
    }

    public DepartmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_department, container, false);

        TextView name = (TextView) view.findViewById(R.id.sectorName);
        listView = (ListView) view.findViewById(R.id.listView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        name.setText(this.name);

        new Data(this, this.getActivity()).getGroups();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setName(String name) { this.name = name; }

    @Override
    public void onDataLoaded(Object out) {
        final JSONObject json = (JSONObject) out;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<String> list = new ArrayList<String>();
                    dList = new HashMap<String, Integer>();
                    JSONObject groupJson = json.getJSONObject(jsonName);
                    for (int i = 0; i < groupJson.length(); i++) {
                        String name = groupJson.names().getString(i);
                        dList.put(groupJson.getString(name), Integer.parseInt(name));

                        list.add(groupJson.getString(name));
                    }

                    Collections.sort(list);

                    final GroupArrayAdapter adapter = new GroupArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if(Session.selectDefault()) {
                                SharedPreferences pref = getActivity().getSharedPreferences(Config.ROOSTER_PREFS, 0);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString(Config.SELECTED_GROUP, adapter.getItem(i));
                                editor.putString(Config.SELECTED_DEPARTMENT, jsonName);
                                editor.putInt(Config.SELECTED_GROUP_ID, dList.get(adapter.getItem(i)));
                                editor.commit();
                            }

                            Session.setGroupId(dList.get(adapter.getItem(i)));
                            Session.setGroup(adapter.getItem(i));
                            Session.setDepartment(jsonName);

                            for(int j = 0; j < listView.getCount(); j++) {
                                View v = listView.getChildAt(j);
                                if(v == null) continue;
                                TextView tv = (TextView) v.findViewById(android.R.id.text1);
                                tv.setTypeface(null, Typeface.NORMAL);
                            }

                            TextView textView = (TextView) view.findViewById(android.R.id.text1);
                            textView.setTypeface(null, Typeface.BOLD);


                            Session.setCurrentFragment(0);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.container, ScheduleFragment.newInstance())
                                    .commit();
                        }
                    });

                } catch (JSONException e) {
                    final ArrayList<String> list = new ArrayList<String>();
                    list.add(getString(R.string.no_groups));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void noDataAvailable() {

    }

    public void setJSONName(String jsonName) {
        this.jsonName = jsonName;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private class GroupArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Context context;

        public GroupArrayAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            this.context = context;
            for(int i = 0; i < objects.size(); i++) {
                map.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int pos) {
            String item = getItem(position);
            return map.get(item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            String currentGroup = Session.getGroup(getActivity());

            if(map.containsKey(currentGroup) && map.get(currentGroup) == position)
                textView.setTypeface(null, Typeface.BOLD);

            // if(convertView != null && map.get(Session.getGroup(getActivity())) == position)
            //     ((TextView) convertView.findViewById(android.R.id.text1)).setTypeface(null, Typeface.BOLD);
            return super.getView(position, textView, parent);
        }

        @Override
        public String getItem(int position) {

            // return Html.fromHtml("<b>BOLD</b>").toString();
            return super.getItem(position);
        }
    }

}