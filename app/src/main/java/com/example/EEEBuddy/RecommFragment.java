package com.example.EEEBuddy;


import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommFragment extends Fragment {


    public RecommFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.seniorbuddy_fragment_recomm, container, false);
        setHasOptionsMenu(true);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //do the button action here
        if (item.getItemId() == R.id.seniorbuddy_filterIcon){

            Toast.makeText(getActivity(), "CLICKED",Toast.LENGTH_LONG).show();
            item.setIcon(R.drawable.ic_filter_colour2);
        }

        return super.onOptionsItemSelected(item);
    }

}
