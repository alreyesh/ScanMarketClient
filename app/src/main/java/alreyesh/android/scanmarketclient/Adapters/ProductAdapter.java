package alreyesh.android.scanmarketclient.Adapters;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import alreyesh.android.scanmarketclient.Dialog.DetailProductDialog;
import alreyesh.android.scanmarketclient.Model.Product;
import alreyesh.android.scanmarketclient.R;

public class ProductAdapter extends ArrayAdapter<Product> {
    Context context;
    public ProductAdapter(@NonNull Context context, ArrayList<Product> products) {
        super(context, 0,products);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView =convertView;
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
       listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

         Toast.makeText(getContext(), "Item clicked is : " + products.getId(), Toast.LENGTH_SHORT).show();

            }
        });


        return listitemView;

    }
}
