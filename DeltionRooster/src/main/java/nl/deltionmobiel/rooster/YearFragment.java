package nl.deltionmobiel.rooster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONObject;


public class YearFragment extends Fragment implements DataListener {
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {
        view = inflater.inflate(R.layout.yearpager, container, false);

        ViewPager pager = (ViewPager) view.findViewById(R.id.yearpager);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(getActivity(), 0);
        yearAdapter.add(2014);
        yearAdapter.add(2015);
        yearAdapter.add(2016);


        return view;
    }

    @Override
    public void onDataLoaded(Object out) {
        final JSONObject json = (JSONObject) out;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ViewPager pager = (ViewPager) view.findViewById(R.id.sectorpager);
                GroupAdapter groupAdapter = new GroupAdapter(getActivity(), getChildFragmentManager());
                groupAdapter.setJson(json);
                pager.setAdapter(groupAdapter);
            }
        });

    }

    @Override
    public void noDataAvailable() {

    }
}
