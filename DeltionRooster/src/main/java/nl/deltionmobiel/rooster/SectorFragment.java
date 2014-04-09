package nl.deltionmobiel.rooster;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
* A simple {@link android.support.v4.app.Fragment} subclass.
* Activities that contain this fragment must implement the
* {@link SectorFragment.OnFragmentInteractionListener} interface
* to handle interaction events.
* Use the {@link SectorFragment#newInstance} factory method to
* create an instance of this fragment.
*
*/
public class SectorFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private int position;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SectorFragment.
     */
    public static SectorFragment newInstance(int position) {
        SectorFragment fragment = new SectorFragment();
        fragment.setPosition(position);
        return fragment;
    }
    public SectorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sector, container, false);

        TextView name = (TextView) view.findViewById(R.id.sectorName);
        String[] names = {
                "ICT Lyceum",
                "Techniek & Transport",
                "Welzijn & Sport",
                "Media & Design"
        };
        name.setText(names[position]);

        return view;
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

    public void setPosition(int position) {
        this.position = position;
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