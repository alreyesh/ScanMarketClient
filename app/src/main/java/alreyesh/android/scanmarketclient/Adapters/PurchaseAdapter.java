package alreyesh.android.scanmarketclient.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder>{
    private Context context;
    private List<Purchase> purchases;
    private  int layout;
    private OnItemClickListener itemListener;
    private OnButtonClickListener btnListener;

    public PurchaseAdapter(List<Purchase> purchases, int layout, OnItemClickListener itemListener, OnButtonClickListener btnListener) {
        this.purchases = purchases;
        this.layout = layout;
        this.itemListener = itemListener;
        this.btnListener = btnListener;
    }

    @NonNull
    @Override
    public PurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.ViewHolder holder, int position) {
        holder.bind(purchases.get(position),itemListener,btnListener);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView limit;
        public Button btnSelect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewName);
            limit = (TextView) itemView.findViewById(R.id.textViewLimit);
            btnSelect = (Button) itemView.findViewById(R.id.btnSelector);
        }
        public void bind(final Purchase purchase, final OnItemClickListener itemListener, final OnButtonClickListener btnListener )
        {
            name.setText( purchase.getName());
            limit.setText(purchase.getLimit()+"");
            btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnListener.onButtonClick(purchase,getBindingAdapterPosition());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemListener.onItemClick(purchase,getBindingAdapterPosition());
                }
            });
        }







    }

    public interface OnItemClickListener {
        void onItemClick(Purchase purchase, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Purchase purchase, int position);
    }


}
