package alreyesh.android.scanmarketclient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Scanner;

import alreyesh.android.scanmarketclient.Activities.MainActivity;
import alreyesh.android.scanmarketclient.Camara.CamaraActivity;
import alreyesh.android.scanmarketclient.Model.Product;
import alreyesh.android.scanmarketclient.Models.Cart;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
import io.realm.RealmList;

public class RelatedProductAdapter  extends RecyclerView.Adapter<RelatedProductAdapter.ViewHolder>{
    private Context context;
    private List<Product> products;
    private int layout;
    private OnItemClickListener itemListener;
    private OnButtonClickListener btnListener;
    private Realm realm;
    private Purchase purchase;
    private SharedPreferences prefs;
    public RelatedProductAdapter(List<Product> products, int layout, OnItemClickListener itemListener, OnButtonClickListener btnListener) {
        this.products = products;
        this.layout = layout;
        this.itemListener = itemListener;
        this.btnListener = btnListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(products.get(position),itemListener,btnListener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView price;
        public EditText count;
        public ImageView img;
        public Button btnAgregar;
        public String defaultcount;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewName);
            price = (TextView) itemView.findViewById(R.id.textPrice);
            count = (EditText) itemView.findViewById(R.id.editCantidad);
            img =(ImageView) itemView.findViewById(R.id.imgProductView);
            btnAgregar=(Button) itemView.findViewById(R.id.btnAgregar);

        }
        public void bind(final Product product, final RelatedProductAdapter.OnItemClickListener itemListener, final RelatedProductAdapter.OnButtonClickListener btnListener ) {
            realm = Realm.getDefaultInstance();
            prefs = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            if(Util.getPurchaseId(prefs) !=null){
                int purchaseId = Util.getPurchaseId(prefs);
                purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
                if(purchase !=null) {
                    String nombre = product.getNombre();
                    RealmList<Cart> carts = purchase.getCarts();
                    Cart verCart = carts.where().equalTo("productName", nombre).findFirst();
                    if (verCart != null) {
                        count.setHint(verCart.getCountProduct());
                        defaultcount =verCart.getCountProduct();
                    }

                    name.setText(product.getNombre());
                    price.setText(product.getPrecio());
                    Picasso.get().load(product.getImagen()).fit().into(img);
                    String lista = Util.getPurchaseName(prefs);

                    btnAgregar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String cantidad = count.getText().toString().trim();
                            if (cantidad.isEmpty() || cantidad == null || cantidad == ""){
                                cantidad = defaultcount;
                                Cart cartin;
                                String nombre = product.getNombre();
                                RealmList<Cart> carts = purchase.getCarts();
                                cartin = carts.where().equalTo("productName", nombre).findFirst();
                                if (cartin == null || cartin.getRealm().isEmpty()){
                                    cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, product.getPrecio());
                                    realm.beginTransaction();
                                    realm.copyToRealm(cartin);
                                    purchase.getCarts().add(cartin);
                                    realm.commitTransaction();

                                    Toast.makeText(context, "Se Ingreso Correctamente a la Cesta", Toast.LENGTH_SHORT).show();
                                    ((MainActivity)context).invalidateOptionsMenu();
                                }else{
                                    realm.beginTransaction();
                                    cartin.setCountProduct(cantidad);
                                    cartin.setSubPrice(product.getPrecio());
                                    realm.copyToRealmOrUpdate(cartin);
                                    realm.commitTransaction();


                                    Toast.makeText(context ,"Se Actualizo el Producto en la Cesta", Toast.LENGTH_SHORT).show();
                                    ((MainActivity)context).invalidateOptionsMenu();

                                }
                                Toast.makeText(context, "Se Ingreso Correctamente a la Cesta", Toast.LENGTH_SHORT).show();
                                ((MainActivity)context).invalidateOptionsMenu();
                                count.setHint(cantidad);
                                count.setText("");


                            }else{
                                boolean isstringint = isStringInteger(cantidad, 10);
                                if (isstringint == true){
                                    int cant = Integer.parseInt(cantidad);
                                    if (cant > 0){
                                        Cart cartin;

                                        String nombre = product.getNombre();

                                        RealmList<Cart> carts = purchase.getCarts();
                                        float precio = Float.parseFloat(product.getPrecio());
                                        float subprice = precio * cant;
                                        String subpricestring = String.valueOf(subprice);
                                        cartin = carts.where().equalTo("productName", nombre).findFirst();
                                        if (carts.size() > 0){
                                            if (cartin == null || cartin.getRealm().isEmpty()){
                                                realm.beginTransaction();
                                                cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, subpricestring);
                                                realm.copyToRealm(cartin);
                                                purchase.getCarts().add(cartin);
                                                realm.commitTransaction();
                                                Toast.makeText(context, "Se Ingreso Correctamente a la Cesta", Toast.LENGTH_SHORT).show();
                                                ((MainActivity)context).invalidateOptionsMenu();
                                            }else{
                                                realm.beginTransaction();
                                                cartin.setCountProduct(cantidad);
                                                cartin.setSubPrice(subpricestring);
                                                realm.copyToRealmOrUpdate(cartin);
                                                realm.commitTransaction();
                                                Toast.makeText(context ,"Se Actualizo el Producto en la Cesta", Toast.LENGTH_SHORT).show();
                                                ((MainActivity)context).invalidateOptionsMenu();
                                            }



                                        }else{
                                            realm.beginTransaction();
                                            cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, subpricestring);
                                            realm.copyToRealm(cartin);
                                            purchase.getCarts().add(cartin);
                                            realm.commitTransaction();
                                            Toast.makeText(context, "Se Ingreso Correctamente a la Cesta", Toast.LENGTH_SHORT).show();
                                            ((MainActivity)context).invalidateOptionsMenu();

                                        }


                                    }else{
                                        Toast.makeText(context, "  Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                                    }




                                }else{
                                    Toast.makeText(context, "Error al ingresar. Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                                }

                            }
                       /* Intent intent = new Intent(context, CamaraActivity.class);
                        context.startActivity(intent);
                        */
                        }
                    });






                }

            } else{
                Toast.makeText(context, "Seleccione una lista de Compra", Toast.LENGTH_SHORT).show();

            }








        }



    }
    public interface OnItemClickListener {
        void onItemClick(Product product, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Product product, int position);
    }
    public static boolean isStringInteger(String stringToCheck, int radix) {
        Scanner sc = new Scanner(stringToCheck.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }

}
