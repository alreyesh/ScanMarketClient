package alreyesh.android.scanmarketclient.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.models.DetailProduct;


public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<DetailProduct> products;
    ImageView imgProductView;
    TextView textViewName;
    TextView textViewCantidad;
    TextView textViewPrice;


    public OrderDetailAdapter(List<DetailProduct> products ) {
        this.products = products;

    }

    @NonNull
    @Override
    public OrderDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order_detail_item,parent,false);


        return  new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        holder.bind(products.get(position) );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProductView = itemView.findViewById(R.id.imgProductView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCantidad= itemView.findViewById(R.id.textViewCantidad);
            textViewPrice= itemView.findViewById(R.id.textViewPrice);

        }
        public void bind (final DetailProduct detailProduct ){
            Picasso.get().load(detailProduct.getLink()).fit().into(imgProductView);
            textViewName.setText(detailProduct.getName());
            textViewCantidad.setText(detailProduct.getCantidad());
            textViewPrice.setText(detailProduct.getSubtotal());
        }


    }
    public interface OnItemClickListener {
        void onItemClick(DetailProduct detailProduct, int position);
    }


}
