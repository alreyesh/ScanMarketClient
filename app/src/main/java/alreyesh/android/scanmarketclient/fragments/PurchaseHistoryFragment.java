package alreyesh.android.scanmarketclient.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import alreyesh.android.scanmarketclient.R;


public class PurchaseHistoryFragment extends Fragment {


    public PurchaseHistoryFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_purchase_history, container, false);







        return v;
    }

}