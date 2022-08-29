package alreyesh.android.scanmarketclient.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.notifications.NotificacionPush;
import alreyesh.android.scanmarketclient.services.Notifyservice;
import alreyesh.android.scanmarketclient.utils.Util;


public class SettingsFragment extends Fragment {

Switch turnswitch ;
    SharedPreferences prefs;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);

        turnswitch = v.findViewById(R.id.turnswitch);
        prefs = Util.getSP(getActivity());
        Boolean turn = Util.getTurnNotify(prefs);
        Toast.makeText(getActivity(),"turn: "+turn, Toast.LENGTH_SHORT).show();
        if(Boolean.TRUE.equals(turn)){
            turnswitch.setChecked(true);
        }else{
            turnswitch.setChecked(false);
        }

        Intent intent = new Intent(getActivity(), Notifyservice.class);

        turnswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Boolean turn;
                if(isChecked){
                    turn = true;
                    getActivity().startService(intent);
                    Toast.makeText(getActivity(),"On", Toast.LENGTH_SHORT).show();

                }else{
                    turn = false;

                    getActivity().  stopService(intent);

                    Toast.makeText(getActivity(),"Off", Toast.LENGTH_SHORT).show();

                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("turnm",turn);
                editor.commit();
            }
        });

        return v;
    }
}