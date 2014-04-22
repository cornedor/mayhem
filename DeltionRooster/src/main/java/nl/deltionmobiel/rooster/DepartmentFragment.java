package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;


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

//        adapter = new PresentationAdapter(this,
//                android.R.layout.simple_list_item_1, list);

        /* String[] names = {
                "ICT Lyceum",
                "Techniek & Transport",
                "Welzijn & Sport",
                "Media & Design"
        }; */
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
    public void onDataLoaded(final JSONObject json) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ArrayList<String> list = new ArrayList<String>();
                    JSONObject groupJson = json.getJSONObject(jsonName);

                    for (int i = 0; i < groupJson.length(); i++) {
                        list.add(groupJson.getString(groupJson.names().getString(i)));
                    }

                    Collections.sort(list);

                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            SharedPreferences pref = getActivity().getSharedPreferences(Config.ROOSTER_PREFS, 0);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(Config.SELECTED_GROUP, adapter.getItem(i));
                            editor.commit();
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

}