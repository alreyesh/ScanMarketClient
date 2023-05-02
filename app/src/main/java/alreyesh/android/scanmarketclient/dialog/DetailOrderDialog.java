package alreyesh.android.scanmarketclient.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;



import java.util.ArrayList;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.adapters.OrderDetailAdapter;

import alreyesh.android.scanmarketclient.models.DetailProduct;
import alreyesh.android.scanmarketclient.utils.Util;

public class DetailOrderDialog extends DialogFragment {
RecyclerView recyclerview;
    FirebaseFirestore db;
    TextView txtPagar;
    TextView textCodOrder;
    TextView txtDate;
    RecyclerView recyclerHistoryItem;
    Button btnAceptar;
    SharedPreferences prefs;

    RecyclerView.LayoutManager mLayoutManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.list_order_detail,null);
        uis(view);

        builder.setView(view);
        return builder.create();

    }

    private void uis(View view) {
        db = FirebaseFirestore.getInstance();
        txtPagar = view.findViewById(R.id.txtPagar);
        textCodOrder = view.findViewById(R.id.textCodOrder);
        txtDate = view.findViewById(R.id.txtDate);
        recyclerHistoryItem = view.findViewById(R.id.recyclerHistoryItem);
        btnAceptar  = view.findViewById(R.id.txtAceptar);
        prefs = Util.getSP(getContext());
        recyclerHistoryItem.setHasFixedSize(true);
        recyclerHistoryItem.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerHistoryItem.setLayoutManager(mLayoutManager);
        String codOrder = Util.getOrderCod(prefs);
        String fechaOrder = Util.getOrderDate(prefs);
        String totalOrder = Util.getOrderTotal(prefs);
        String json = Util.getOrderDetail(prefs);
        txtPagar.setText("Total pagado: S/. "+totalOrder);
        textCodOrder.setText("cod: "+codOrder);
        txtDate.setText("Fecha: "+fechaOrder);
        btnAceptar.setOnClickListener(v -> dismiss());

        Gson gson= new Gson();
        ArrayList<DetailProduct> products = gson .fromJson(json,new TypeToken< ArrayList<DetailProduct>>(){}.getType());

        OrderDetailAdapter   adapter = new OrderDetailAdapter(products );
        recyclerHistoryItem.setAdapter(adapter);

        adapter.notifyDataSetChanged();


    }
}
