package nl.deltionmobiel.rooster;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

        View view = inflater.inflate(R.layout.fragment_year, container, false);

        TextView name = (TextView) view.findViewById(R.id.yearName);

        Calendar cal = Calendar.getInstance();
        Integer year = cal.get(Calendar.YEAR);

        name.setText("" + (year + this.position));

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);

        DateFormat dfm = new SimpleDateFormat("dd/MM/yyyy");
        Date lastDay = null;
        try {
            lastDay = dfm.parse("31/12/" + year);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar lastWeekCal = Calendar.getInstance();
        lastWeekCal.setMinimalDaysInFirstWeek(7);
        lastWeekCal.setTime(lastDay);

        for(int i = 0; i < lastWeekCal.get(Calendar.WEEK_OF_YEAR); i++) {
            adapter.add("Week " + (i + 1));
        }

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String weekStr = adapter.getItem(i).replace("Week ", "");
                Integer week = Integer.decode(weekStr);

                Session.setWeek(week);

                SharedPreferences pref = getActivity().getSharedPreferences(Config.ROOSTER_PREFS, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(Config.SELECTED_WEEK, week);
                editor.commit();

                for(int j = 0; j < listView.getCount(); j++) {
                    View v = listView.getChildAt(j);
                    if(v == null) continue;
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                    tv.setTypeface(null, Typeface.NORMAL);
                }

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTypeface(null, Typeface.BOLD);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, ScheduleFragment.newInstance())
                        .commit();
            }
        });

        return view;

    }

    public void setPosition(int position) {
        this.position = position;
    }
}
