package sai.developement.travelogue.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sai.developement.travelogue.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNewTripFragment extends Fragment {


    public AddNewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_trip, container, false);
    }

}
