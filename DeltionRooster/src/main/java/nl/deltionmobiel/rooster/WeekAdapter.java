package nl.deltionmobiel.rooster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class WeekAdapter extends ArrayAdapter<WeekObject> {

    public String chosenWeek;

    public WeekAdapter(Context context, ArrayList<WeekObject> weeks, String chosenWeek) {
        super(context, 0, weeks);
        this.chosenWeek = chosenWeek;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WeekObject week = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_week, parent, false);
        }
        // Lookup view for data population
        TextView weekString = (TextView) convertView.findViewById(R.id.weekString);
        TextView weekNumber = (TextView) convertView.findViewById(R.id.weekNumber);
        // Populate the data into the template view using the data object

        weekString.setText(week.week);
        weekNumber.setText(week.number);

        String weekNow = ""+CurrentWeekNumber();
        String weekSelected = Config.SELECTED_WEEK;

        Log.w("","Selected config week: "+weekSelected);

        if(weekNow.equals(week.number)){
            SpannableString numberContent = new SpannableString(weekNumber.getText().toString());
            numberContent.setSpan(new UnderlineSpan(), 0, numberContent.length(), 0);
            weekNumber.setText(numberContent);

            SpannableString stringContent = new SpannableString(weekString.getText().toString());
            stringContent.setSpan(new UnderlineSpan(), 0, stringContent.length(), 0);
            weekString.setText(stringContent);
        }

        if(chosenWeek.equals(week.number)){
            weekNumber.setTypeface(null, Typeface.BOLD_ITALIC);
            weekString.setTypeface(null, Typeface.BOLD_ITALIC);
        }
        // Return the completed view to render on screen
        return convertView;
    }

    public int CurrentWeekNumber(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.WEEK_OF_YEAR);
        return week;
    }
}