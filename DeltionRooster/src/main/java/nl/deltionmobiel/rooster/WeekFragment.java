package nl.deltionmobiel.rooster;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by corne on 20-5-14.
 */
public class WeekFragment extends Fragment {

    private int position;

    public static WeekFragment newInstance(int position) {
        WeekFragment fragment = new WeekFragment();
        fragment.setPosition(position);
        return fragment;
    }

    public WeekFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_department, container, false);

        TextView name = (TextView) view.findViewById(R.id.sectorName);
        name.setText("" + (2014 + this.position));

        ListView listView = (ListView) view.findViewById(R.id.listView);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        for(int i = 0; i < 53; i++) {
            adapter.add("Week " + i);
        }

        listView.setAdapter(adapter);

        listView.setVisibility(View.VISIBLE);

        return view;

    }

    public void setPosition(int position) {
        this.position = position;
    }
}
