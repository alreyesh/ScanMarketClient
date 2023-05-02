package alreyesh.android.scanmarketclient.dialog;

import android.app.AlertDialog;
import android.app.Dialog;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;

public class NotificationDialog extends DialogFragment {
    TextView txtTitleNoti;
    TextView textDescription;
    Button btnOk;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        SharedPreferences  prefs = Util.getSP(getContext());
        String titulo = Util.getTitle(prefs);

        String descrip = Util.getDescrip(prefs);

        View v = inflater.inflate(R.layout.notification_dialog_layout,null);
        txtTitleNoti = v.findViewById(R.id.txtTitleNoti);
        textDescription = v.findViewById(R.id.textDescription);
        btnOk = v.findViewById(R.id.btnOk);
        txtTitleNoti.setText(titulo);
        textDescription.setText(descrip);
        textDescription.setMovementMethod(new ScrollingMovementMethod());



        btnOk.setOnClickListener(v1 -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("turnnoti",true);
            editor.commit();

            dismiss();
        });


        builder.setView(v);

        return builder.create();
    }
}
