package alreyesh.android.scanmarketclient.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.R;
import io.realm.Realm;

public class CartAdapter  extends RecyclerView.Adapter<CartAdapter.ViewHolder>{
    private Context context;
    private List<Cart> carts;
    private  int layout;
    private Realm realm;
    private CartAdapter.OnItemClickListener itemListener;
    private CartAdapter.OnButtonClickListener btnListener;
    private SharedPreferences prefs;
    public CartAdapter(List<Cart> carts, int layout, CartAdapter.OnItemClickListener itemListener, CartAdapter.OnButtonClickListener btnListener) {
        this.carts = carts;
        this.layout = layout;
        this.itemListener = itemListener;
        this.btnListener = btnListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        ViewHolder vh = new ViewHolder(v);
        realm = Realm.getDefaultInstance();

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(carts.get(position),itemListener,btnListener);
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView  textViewName,textViewSubPrice,textViewCount;
        public ImageView imgProductView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            imgProductView = (ImageView) itemView.findViewById(R.id.imgProductView);
            textViewSubPrice=(TextView)itemView.findViewById(R.id.textViewSubPrice);
            textViewCount =(TextView) itemView.findViewById(R.id.textViewCountP);

        }
        public void bind(final Cart cart, final CartAdapter.OnItemClickListener itemListener, final CartAdapter.OnButtonClickListener btnListener ){
            textViewName.setText(cart.getProductName());
            textViewSubPrice.setText("S/. "+cart.getSubPrice());
            textViewCount.setText(cart.getCountProduct() +" und");
            Picasso.get().load(cart.getImagenProduct()).fit().into(imgProductView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    itemListener.onItemClick(cart,getBindingAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return false;

                }
            });

        }

    }
    public interface OnItemClickListener {
        void onItemClick(Cart cart, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Cart cart, int position);
    }





}
