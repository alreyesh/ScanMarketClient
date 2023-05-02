package alreyesh.android.scanmarketclient.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;


import java.text.DecimalFormat;
import java.util.ArrayList;

import alreyesh.android.scanmarketclient.dialog.DetailProductDialog;
import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;

public class ProductAdapter extends ArrayAdapter<Product>  {
    private Context context;

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
        prefs = Util.getSP(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        if(listitemView == null){
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.list_product,parent,false);
        }
        Product productss = getItem(position);
        ImageView imgProductView = listitemView.findViewById(R.id.imgProductView);

        TextView textViewPrice = listitemView.findViewById(R.id.textViewPrice);
        TextView textViewDescuento = listitemView.findViewById(R.id.textViewDescuento);
        float descuento = Float.parseFloat(productss.getDescuento());
        float  precio = Float.parseFloat(productss.getPrecio());
        DecimalFormat df = new DecimalFormat("#.##");
        if(descuento >0.0) {
            textViewPrice.setText("S/. "+df.format(descuento));

            textViewDescuento.setText("S/. "+df.format(precio));

            textViewDescuento.setTextColor(Color.RED);
            textViewDescuento.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            textViewDescuento.setVisibility(View.VISIBLE);
        }else{
            textViewPrice.setText("S/. "+df.format(precio));
            textViewDescuento.setVisibility(View.GONE);
        }


        Picasso.get().load(productss.getImagen()).fit() .into(imgProductView);



       listitemView.setOnClickListener(v -> {
           String json = gson.toJson(productss);
           editor.putString("productos",json);
           editor.commit();
           FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
           DetailProductDialog detailProductDialog  = new DetailProductDialog();

           detailProductDialog.show(manager, "Detalle del Producto");

        });
        listitemView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, 300));

        return listitemView;

    }
}
