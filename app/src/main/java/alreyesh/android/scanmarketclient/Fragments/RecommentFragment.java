package alreyesh.android.scanmarketclient.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;


public class RecommentFragment extends Fragment {
    private SharedPreferences prefs;


    public RecommentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs =getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            String producto = Util.getProductFromCamera(prefs);
            Toast.makeText(getActivity(),producto, Toast.LENGTH_SHORT).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recomment, container, false);
    }
}