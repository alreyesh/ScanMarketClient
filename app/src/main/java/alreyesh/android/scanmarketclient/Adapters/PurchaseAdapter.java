package alreyesh.android.scanmarketclient.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.IconView;
import com.squareup.picasso.Picasso;

import java.util.List;

import alreyesh.android.scanmarketclient.Activities.LoginActivity;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import io.realm.RealmResults;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder>{
    private Context context;
    private List<Purchase> purchases;
    private  int layout;
    private OnItemClickListener itemListener;
    private OnButtonClickListener btnListener;
    private ItemTouchHelper itemTouchHelper;
    private SharedPreferences prefs;
    public PurchaseAdapter(List<Purchase> purchases, int layout, OnItemClickListener itemListener, OnButtonClickListener btnListener) {
        this.purchases = purchases;
        this.layout = layout;
        this.itemListener = itemListener;
        this.btnListener = btnListener;

    }



    @NonNull
    @Override
    public  PurchaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
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
        public void bind(final Purchase purchase, final OnItemClickListener itemListener, final OnButtonClickListener btnListener )
        {
            name.setText( purchase.getName());
            limit.setText("S/. "+purchase.getLimit()+"");
             iconView.setIcon(purchase.getIcon());
             if(iconView.isSelected()){
                 iconView.setColorFilter(Color.GREEN);
             }
            int numberOfCart = purchase.getCarts().size();
             String textForCart = (numberOfCart ==1)? numberOfCart +" Producto": numberOfCart +" Productos";
                count.setText(textForCart);

           // Picasso.get().load(purchase.getIcon()).fit().into(iconView);
            //linearLayout.setBackgroundColor( purchase.getColor());
           // cardView.setCardBackgroundColor(purchase.getColor());
            cardView.setCardBackgroundColor(purchase.getColor());
            //cardView.setBackgroundResource(ContextCompat.getColor(context,purchase.getColor()));
         /*   btnSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnListener.onButtonClick(purchase,getBindingAdapterPosition());
                }
            });
*/
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
        }







    }

    public interface OnItemClickListener {
        void onItemClick(Purchase purchase, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Purchase purchase, int position);
    }


}
