package alreyesh.android.scanmarketclient.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.IconView;

import java.util.ArrayList;
import java.util.List;

import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.RealmList;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder>{
    private Context context;
    private List<Purchase> purchases;
    private  int layout;
    private OnItemClickListener itemListener;
    private OnButtonClickListener btnListener;
    private ItemTouchHelper itemTouchHelper;
    private OnLongClickListener longListener;
    private SharedPreferences prefs;
    public PurchaseAdapter(List<Purchase> purchases, int layout, OnItemClickListener itemListener, OnButtonClickListener btnListener,OnLongClickListener longListener) {
        this.purchases = purchases;
        this.layout = layout;
        this.itemListener = itemListener;
        this.btnListener = btnListener;
        this.longListener = longListener;
    }



    @NonNull
    @Override
    public  PurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        prefs = Util.getSP(context);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseAdapter.ViewHolder holder, int position) {
        holder.bind(purchases.get(position),itemListener,btnListener,longListener);
    }

    @Override
    public int getItemCount() {
        return purchases.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView limit;
        public TextView actual;
        public TextView count;
        public LinearLayout linearLayout;
        public CardView cardView;
        public IconView iconView;
        public Button btnSelect;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewName);
            limit = (TextView) itemView.findViewById(R.id.textViewLimit);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.listViewPurchase);
            cardView = (CardView) itemView.findViewById(R.id.ListPurchaseCardView);
            iconView = (IconView) itemView.findViewById(R.id.imgIcon);
            actual = (TextView) itemView.findViewById(R.id.textViewActual);
            count = (TextView) itemView.findViewById(R.id.textViewCount);
            //btnSelect = (Button) itemView.findViewById(R.id.btnSelector);
        }
        public void bind(final Purchase purchase, final OnItemClickListener itemListener, final OnButtonClickListener btnListener,final OnLongClickListener longListener)
        {
            name.setText( purchase.getName());
            limit.setText("S/. "+purchase.getLimit()+"");
            List<Cart> list = new ArrayList<Cart>();
            RealmList<Cart> carts=purchase.getCarts() ;
            list.addAll(carts);
            float totalCart=0.0f;
            for(int i =0; i<list.size();i++){

                String subtotal =  list.get(i).getSubPrice();
                float parsesubtotal = Float.parseFloat(subtotal);
                totalCart+= parsesubtotal;
            }



            actual.setText("S/. "+totalCart);
             iconView.setIcon(purchase.getIcon());
             if(iconView.isSelected()){
                 iconView.setColorFilter(Color.GREEN);
             }
            int numberOfCart = purchase.getCarts().size();
             String textForCart = (numberOfCart ==1)? numberOfCart +" Producto": numberOfCart +" Productos";
                count.setText(textForCart);


            cardView.setCardBackgroundColor(purchase.getColor());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("idp",String.valueOf( purchase.getId()));
                    editor.putString("np",purchase.getName());
                    editor.putString("cp",String.valueOf(purchase.getColor()));
                    editor.commit();
                    itemListener.onItemClick(purchase,getBindingAdapterPosition());

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longListener.onLongClick(purchase,getBindingAdapterPosition());
                    return false;
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
    public  interface  OnLongClickListener{
        void onLongClick(Purchase purchase, int position);
    }


}
