package nl.deltionmobiel.rooster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;


public class DepartmentsFragment extends Fragment implements DataListener {
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {
        view = inflater.inflate(R.layout.sectorpager, container, false);

        new Data(this, getActivity()).getDepartments();

        return view;
    }

    /**
     * Load a list with departments and feed that to the GroupAdapter which will load the
     * DepartmentFragment's into the DepartmentsFragment
     *
     * @param out JSON with list of departments and classes
     */
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
