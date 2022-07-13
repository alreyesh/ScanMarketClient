package alreyesh.android.scanmarketclient.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;

import alreyesh.android.scanmarketclient.dialog.DetailProductDialog;
import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.R;

public class ProductAdapter extends ArrayAdapter<Product> implements Serializable {
    private Context context;
    private int layout;
    private ArrayList<Product> products;
    private SharedPreferences prefs;
    public ProductAdapter(@NonNull Context context, ArrayList<Product> products) {
        super(context, 0,products);
        this.context = context;

        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView =convertView;
        prefs =getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if(listitemView == null){
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.list_product,parent,false);
        }
        Product products = getItem(position);
        ImageView imgProductView = listitemView.findViewById(R.id.imgProductView);
        TextView   textViewName = listitemView.findViewById(R.id.textViewName);
        TextView textViewPrice = listitemView.findViewById(R.id.textViewPrice);
        Picasso.get().load(products.getImagen()).fit().into(imgProductView);
        textViewName.setText(products.getNombre());
        textViewPrice.setText("S/. "+products.getPrecio());
       listitemView.setOnClickListener(v -> {
           String json = gson.toJson(products);
           editor.putString("productos",json);
           editor.commit();
           FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
           DetailProductDialog detailProductDialog  = new DetailProductDialog();

           detailProductDialog.show(manager, "Detalle del Producto");

        });


        return listitemView;

    }
}
