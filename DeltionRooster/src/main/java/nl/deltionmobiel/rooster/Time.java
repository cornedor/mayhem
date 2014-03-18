package nl.deltionmobiel.rooster;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by corne on 3/11/14.
 */
public class Time extends RelativeLayout implements View.OnTouchListener, View.OnClickListener, View.OnFocusChangeListener {

    private TextView teacher, room, teacher2, room2;

    public Time(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.time, this);

        this.setOnTouchListener(this);
        this.setOnClickListener(this);
        this.setOnFocusChangeListener(this);

        this.setFocusable(true);

        teacher = (TextView) findViewById(R.id.teacher);
        room = (TextView) findViewById(R.id.room);

        teacher2 = (TextView) findViewById(R.id.teacher2);
        room2 = (TextView) findViewById(R.id.room2);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            setBackgroundColor(getResources().getColor(R.color.item_hover));
        } else {
            setBackgroundColor(getResources().getColor(android.R.color.white));
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if(teacher.getVisibility() == GONE) {
            teacher.setVisibility(VISIBLE);
            room.setVisibility(VISIBLE);
            teacher2.setVisibility(VISIBLE);
            room2.setVisibility(VISIBLE);
        } else {
            teacher.setVisibility(GONE);
            room.setVisibility(GONE);
            teacher2.setVisibility(GONE);
            room2.setVisibility(GONE);
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        Log.wtf("WTF!!!", b + "");
        if(!b) {
            teacher.setVisibility(GONE);
            room.setVisibility(GONE);
        } else {
            teacher.setVisibility(VISIBLE);
            room.setVisibility(VISIBLE);
        }
    }
}
