package alreyesh.android.scanmarketclient.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.adapters.OrderDetailAdapter;
import alreyesh.android.scanmarketclient.models.Cart;
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
    private OrderDetailAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.list_order_detail,null);
        UI(view);

        builder.setView(view);
        return builder.create();

    }

    private void UI(View view) {
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
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Gson gson= new Gson();
        ArrayList<DetailProduct> products = gson .fromJson(json,new TypeToken< ArrayList<DetailProduct>>(){}.getType());

        adapter = new OrderDetailAdapter(products, new OrderDetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DetailProduct detailProduct, int position) {

            }
        });
        recyclerHistoryItem.setAdapter(adapter);

        adapter.notifyDataSetChanged();


    }
}
