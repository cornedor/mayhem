package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ScheduleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ScheduleFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class ScheduleFragment extends Fragment implements DataListener {

    private OnFragmentInteractionListener mListener;

    private ProgressDialog pDialog;

    private View v;
    private LinearLayout view;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScheduleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();

        // new Data()

        return fragment;
    }
    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Rooster ophalen ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(pDialog.isShowing()) pDialog.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_schedule, container, false);

        view = (LinearLayout) v.findViewById(R.id.schedule_container);

        new Data(this, getActivity()).getTimes();

        return v;
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

    @Override
    public void onDataLoaded(Object out) {
        final JSONArray json = (JSONArray) out;

        final String[] days = {
                getText(R.string.monday).toString(),
                getText(R.string.tuesday).toString(),
                getText(R.string.wednesday).toString(),
                getText(R.string.thursday).toString(),
                getText(R.string.friday).toString()
        };
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    for(int i = 0; i < json.length(); i++) {
                        Card card = new Card(v.getContext(), null);
                        LinearLayout cardLinearLayout = (LinearLayout) card.findViewById(R.id.container);
                        JSONArray times = json.getJSONObject(i).getJSONArray("items");

                        for(int j = 0; j < times.length(); j++) {
                            JSONObject curTime = times.getJSONObject(j);
                            Time time = new Time(v.getContext(), null);
                            time.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            cardLinearLayout.addView(time);

                            TextView hours = (TextView) time.findViewById(R.id.time);
                            TextView lesson = (TextView) time.findViewById(R.id.lesson);
                            TextView teacher = (TextView) time.findViewById(R.id.teacher);
                            TextView room = (TextView) time.findViewById(R.id.room);

                            String from = curTime.getString("from");
                            String to = curTime.getString("to");
                            String lessonStr = curTime.getString("lesson");
                            String teacherStr = curTime.getString("teacher");
                            String roomStr = curTime.getString("classroom");

                            hours.setText(from + " - " + to);
                            lesson.setText(lessonStr);
                            teacher.setText(teacherStr);
                            room.setText(roomStr);
                        }

                        pDialog.dismiss();

                        card.setAlpha(0);
                        card.setTranslationY(card.getTranslationY() + 200);
                        card.setRotationX(20);


                        card.animate()
                                .alpha(1)
                                .translationYBy(-200)
                                .rotationX(0)
                                .setDuration(300)
                                .setStartDelay(i * 150);

                        view.addView(card);
                        TextView day = (TextView) card.findViewById(R.id.day_label);
                        day.setText(days[i]);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    @Override
    public void noDataAvailable() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pDialog.dismiss();

                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.offline_fail_title))
                        .setMessage(getString(R.string.offline_fail_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .show();
            }
        });

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
