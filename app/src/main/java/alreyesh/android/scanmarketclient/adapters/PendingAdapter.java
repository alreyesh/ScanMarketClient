package alreyesh.android.scanmarketclient.adapters;

import android.app.Dialog;
import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.text.DecimalFormat;
import java.util.List;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.models.Pending;



public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter.ViewHolder> {
    private Context context;
    private List<Pending> pending;
    private int layout;
    private OnItemClickListener itemListener;

    public PendingAdapter(List<Pending> pending, int layout, OnItemClickListener itemListener) {
        this.pending = pending;
        this.layout = layout;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();


        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(pending.get(position),itemListener);
    }

    @Override
    public int getItemCount() {
        return pending.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         TextView codigo;
         TextView total;
        ImageView bitmap;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            codigo = (TextView) itemView.findViewById(R.id.textViewCod);
            total = (TextView) itemView.findViewById(R.id.textViewTotal);
            bitmap = (ImageView) itemView.findViewById(R.id.imgQR);

        }

        public void bind(final Pending pending,final OnItemClickListener itemListener){
            codigo.setText("cod: "+pending.getCodorder());
            float f = Float.parseFloat(pending.getTotal());
            DecimalFormat df = new DecimalFormat("#.##");
            total.setText("S/. "+ df.format(f));
            Bitmap bit =  BitmapFactory.decodeByteArray(pending.getBitmap(), 0, pending.getBitmap().length);
            bitmap.setImageBitmap(bit);
            bitmap.setOnClickListener(v -> showImage(bit));
            itemView.setOnClickListener(v -> itemListener.onItemClick(pending,getBindingAdapterPosition()));
        }
        public void showImage(Bitmap bit) {
            Dialog builder = new Dialog(context);
            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setOnDismissListener(dialogInterface -> {
                //nothing;
            });

            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(bit);
            builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                    600,
                   600));
            builder.show();
        }

    }
    public interface OnItemClickListener {
        void onItemClick(Pending pending, int position);
    }
}
