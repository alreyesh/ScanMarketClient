package alreyesh.android.scanmarketclient.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import alreyesh.android.scanmarketclient.R;

public class DetailProductDialog  extends DialogFragment {
    private TextView editCodProduct;
    private TextView editNameProduct;
    private TextView editDescProduct;
    private TextView editPriceProduct;
    private ImageView img_product;
    private Button btnRegistrar,btnCancelar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.detail_product_layout,null);
        editCodProduct=(TextView) view.findViewById(R.id.textViewCod);
        editNameProduct=(TextView) view.findViewById(R.id.textViewName);
        editDescProduct=(TextView)  view.findViewById(R.id.textViewDesc);
        editPriceProduct=(TextView)  view.findViewById(R.id.textViewPrice);
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrar);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelar);


        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);
        return builder.create();
    }



}
