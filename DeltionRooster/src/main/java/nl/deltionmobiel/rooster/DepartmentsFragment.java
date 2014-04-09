package nl.deltionmobiel.rooster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class DepartmentsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.sectorpager, container, false);

        ViewPager pager = (ViewPager) view.findViewById(R.id.sectorpager);
        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), getChildFragmentManager());
        pager.setAdapter(groupAdapter);

        return view;
    }
}
