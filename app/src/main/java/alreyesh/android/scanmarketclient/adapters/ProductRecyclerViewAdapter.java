package alreyesh.android.scanmarketclient.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.dialog.DetailProductDialog;
import alreyesh.android.scanmarketclient.model.Product;

import alreyesh.android.scanmarketclient.utils.Util;

public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductRecyclerViewAdapter.ViewHolder>{
    private Context context;
    private List<Product> products;
    private  OnItemClickListener itemListener;

    private SharedPreferences prefs;
    private  int layout;
    public ProductRecyclerViewAdapter(List<Product> products, int layout, OnItemClickListener itemListener ){
       this.products = products;
        this.layout = layout;
        this.itemListener =itemListener;


    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        prefs = Util.getSP(context);

        return new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position),itemListener  );
    }

    @Override
    public int getItemCount() {
        return  products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductView;
        TextView textViewPrice;
        TextView textViewDescuento;
        TextView   textViewName;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductView =  itemView.findViewById(R.id.imgProductView);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDescuento = itemView.findViewById(R.id.textViewDescuento);
            textViewName = itemView.findViewById(R.id.textViewName);

        }
        public void bind(final Product products, final  OnItemClickListener itemListener  ){
            float descuento = Float.parseFloat(products.getDescuento());
            float  precio = Float.parseFloat(products.getPrecio());
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
            textViewName.setText(products.getNombre());

            Picasso.get().load(products.getImagen()).fit() .into(imgProductView);

             itemView.setOnClickListener(v -> {
                 Gson gson = new Gson();
                 SharedPreferences.Editor editor = prefs.edit();
                 String json = gson.toJson(products);
                 editor.putString("productos",json);
                 editor.commit();
                 FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                 DetailProductDialog detailProductDialog  = new DetailProductDialog();

                 detailProductDialog.show(manager, "Detalle del Producto");


                 itemListener.onItemClick(products,getBindingAdapterPosition());
             });


        }


    }
    public interface OnItemClickListener {
        void onItemClick(Product products, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Product products, int position);
    }
    public  interface  OnLongClickListener{
        void onLongClick(Product products, int position);
    }

}
