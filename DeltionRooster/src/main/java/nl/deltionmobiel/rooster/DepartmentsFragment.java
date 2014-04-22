package nl.deltionmobiel.rooster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;


public class DepartmentsFragment extends Fragment implements DataListener {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {
        view = inflater.inflate(R.layout.sectorpager, container, false);

        new Data(this, getActivity()).getDepartments();

        return view;
    }

    @Override
    public void onDataLoaded(final JSONObject json) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewPager pager = (ViewPager) view.findViewById(R.id.sectorpager);
                GroupAdapter groupAdapter = new GroupAdapter(getActivity(), getChildFragmentManager());
                System.out.println(json.names().toString());
                groupAdapter.setJson(json);
                pager.setAdapter(groupAdapter);
            }
        });

    }

    @Override
    public void noDataAvailable() {

    }
}