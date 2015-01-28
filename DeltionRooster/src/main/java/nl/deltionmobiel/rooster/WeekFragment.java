package nl.deltionmobiel.rooster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by corne on 20-5-14.
 */
public class WeekFragment extends Fragment {

    private int position;
    private Context context;

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

        this.context = getActivity();
        View view = inflater.inflate(R.layout.fragment_year, container, false);

        TextView name = (TextView) view.findViewById(R.id.yearName);

        Calendar cal = Calendar.getInstance();
        Integer year = cal.get(Calendar.YEAR);

        final Integer curYear = year + this.position;
        name.setText("" + curYear);

        final ListView listView = (ListView) view.findViewById(R.id.listView);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);

        ArrayList<WeekObject> arrayOfWeeks = new ArrayList<WeekObject>();


        int weekOfYear = new GregorianCalendar().get(Calendar.WEEK_OF_YEAR);
        SharedPreferences settings = getActivity().getSharedPreferences(Config.ROOSTER_PREFS, 0);
        String chosenWeek = ""+settings.getInt(Config.SELECTED_WEEK, weekOfYear);

        final WeekAdapter adapter = new WeekAdapter(getActivity(), arrayOfWeeks, chosenWeek);

//        final ArrayAdapter<TextView> adapter = new ArrayAdapter<TextView>(getActivity(), android.R.layout.simple_list_item_1);


        String weekSelected = Config.SELECTED_WEEK;
        Log.w("","Selected week: "+weekSelected);

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

        for (int i = 0; i < lastWeekCal.get(Calendar.WEEK_OF_YEAR); i++) {
            WeekObject newWeek = new WeekObject("Week", ""+(i+1));
            adapter.add(newWeek);
        }

        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WeekObject weekObj = adapter.getItem(i);
                String weekStr = weekObj.number;
                Integer week = Integer.decode(weekStr);

                Session.setWeek(week);
                Session.setYear(curYear);
                Session.setCurrentFragment(0);

                SharedPreferences pref = getActivity().getSharedPreferences(Config.ROOSTER_PREFS, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt(Config.SELECTED_WEEK, week);
                editor.commit();

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
