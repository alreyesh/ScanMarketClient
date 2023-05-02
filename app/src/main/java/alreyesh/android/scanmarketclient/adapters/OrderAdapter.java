package alreyesh.android.scanmarketclient.adapters;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;


import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.models.Order;

import alreyesh.android.scanmarketclient.utils.Util;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private List<Order> orders;
    private SharedPreferences prefs;
    TextView textCodOrder;
    TextView textDateOrder;
    TextView textTotalOrder;
    private  OnItemClickListener itemListener;
    public OrderAdapter(List<Order> orders, OnItemClickListener itemListener) {
        this.itemListener = itemListener;
        this.orders = orders;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_order,parent,false);
        Context  context = parent.getContext();
        prefs = Util.getSP(context);
        ViewHolder view = new ViewHolder(v);

        return  view;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(orders.get(position),itemListener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCodOrder = itemView.findViewById(R.id.textCodOrder);
            textDateOrder = itemView.findViewById(R.id.textDateOrder);
            textTotalOrder = itemView.findViewById(R.id.textTotalOrder);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
// DO Nothing


                }
            });

        }
        public void bind ( final Order order,final  OnItemClickListener itemListener){
        String date=    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getFecha() );
            float f = Float.parseFloat(order.getTotal());
            DecimalFormat df = new DecimalFormat("#.##");
            textCodOrder.setText("cod: "+order.getCodorder());
            textDateOrder.setText("fecha: "+date);
            textTotalOrder.setText("total: S/."+ df.format(f));

            itemView.setOnClickListener(v -> {
                Gson gson = new Gson();
                String json = gson.toJson(order.getProductos());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ordercod",order.getCodorder());
                editor.putString("orderdate",date);
                editor.putString("ordertotal",df.format(f));
                editor.putString("orderdetail",json);
                editor.commit();
                itemListener.onItemClick(order,getBindingAdapterPosition());
            });


        }



    }
    public interface OnItemClickListener {
        void onItemClick(Order order, int position);
    }


}
