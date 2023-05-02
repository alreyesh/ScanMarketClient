package alreyesh.android.scanmarketclient.adapters;

import android.content.Context;
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

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Scanner;

import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;
import io.realm.RealmList;

public class RelatedProductAdapter  extends RecyclerView.Adapter<RelatedProductAdapter.ViewHolder>{
    private Context context;
    private List<Product> products;
    private int layout;


    private Realm realm;
    private Purchase purchase;
    private SharedPreferences prefs;
    private  String cantidad;
    private static final String MESSAGE = "Se Ingreso Correctamente a la Cesta";  // Compliant
    private static final String productName = "productName";
    public RelatedProductAdapter(List<Product> products, int layout  ) {
        this.products = products;
        this.layout = layout;

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
        holder.bind(products.get(position) );
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
         TextView name;
         TextView price;
         EditText count;
         ImageView img;
         Button btnAgregar;
         String defaultcount;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.textViewName);
            price = (TextView) itemView.findViewById(R.id.textPrice);
            count = (EditText) itemView.findViewById(R.id.editCantidad);
            img =(ImageView) itemView.findViewById(R.id.imgProductView);
            btnAgregar=(Button) itemView.findViewById(R.id.btnAgregar);

        }
        public void bind(final Product product  ) {
            realm = Realm.getDefaultInstance();
            prefs = Util.getSP(context);
            if(Util.getPurchaseId(prefs) !=null){
                int purchaseId = Util.getPurchaseId(prefs);
                purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
                if(purchase !=null) {
                    String nombre = product.getNombre();
                    RealmList<Cart> carts = purchase.getCarts();
                    Cart verCart = carts.where().equalTo(productName, nombre).findFirst();
                    if (verCart != null) {
                        count.setHint(verCart.getCountProduct());
                        defaultcount =verCart.getCountProduct();
                    }

                    name.setText(product.getNombre());
                    price.setText(product.getPrecio());
                    Picasso.get().load(product.getImagen()).fit().into(img);


                    btnAgregar.setOnClickListener(v -> {
                         cantidad = count.getText().toString().trim();
                        if (cantidad.isEmpty() || cantidad.equals("") ){
                            cantempty(product, defaultcount);
                            count.setHint(cantidad);
                            count.setText("");
                        }
                        else{
                            boolean isstringint = isStringInteger(cantidad, 10);
                            if (isstringint == true){
                                int cant = Integer.parseInt(cantidad);
                                if (cant > 0){
                                    Cart cartin;

                                    String nombre1 = product.getNombre();

                                    RealmList<Cart> carts1 = purchase.getCarts();
                                    float precio = Float.parseFloat(product.getPrecio());
                                    float subprice = precio * cant;
                                    String subpricestring = String.valueOf(subprice);
                                    cartin = carts1.where().equalTo(productName, nombre1).findFirst();
                                    if (!carts1.isEmpty()){
                                      cartsexist(product,cartin,subpricestring);

                                    }else{
                                        cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, subpricestring);

                                        cartsnoexist( cartin);


                                    }


                                }else{
                                    Toast.makeText(context, "  Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                                }




                            }else{
                                Toast.makeText(context, "Error al ingresar. Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                            }

                        }

                    });






                }

            } else{
                Toast.makeText(context, "Seleccione una lista de Compra", Toast.LENGTH_SHORT).show();

            }








        }

        private void cantempty(Product product, String defaultcount){
            cantidad = defaultcount;
            Cart cartin;
            String nombre = product.getNombre();
            RealmList<Cart> carts = purchase.getCarts();
            cartin = carts.where().equalTo(productName, nombre).findFirst();
            if (cartin == null || cartin.getRealm().isEmpty()){
                cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, product.getPrecio());
                realm.beginTransaction();
                realm.copyToRealm(cartin);
                purchase.getCarts().add(cartin);
                realm.commitTransaction();

                Toast.makeText(context, MESSAGE, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, MESSAGE, Toast.LENGTH_SHORT).show();
            ((MainActivity)context).invalidateOptionsMenu();



        }
        private void cartsexist(Product product, Cart cartin, String subpricestring ){
            if (cartin == null || cartin.getRealm().isEmpty()){
                realm.beginTransaction();
                cartin = new Cart(product.getCodigo(), product.getNombre(), product.getImagen(), product.getPrecio(), cantidad, subpricestring);
                realm.copyToRealm(cartin);
                purchase.getCarts().add(cartin);
                realm.commitTransaction();
                Toast.makeText(context, MESSAGE, Toast.LENGTH_SHORT).show();
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


        }
        private void   cartsnoexist(  Cart cartin ){
            realm.beginTransaction();
            realm.copyToRealm(cartin);
            purchase.getCarts().add(cartin);
            realm.commitTransaction();
            Toast.makeText(context, MESSAGE, Toast.LENGTH_SHORT).show();
            ((MainActivity)context).invalidateOptionsMenu();
        }

    }
    public interface OnItemClickListener {
        void onItemClick(Product product, int position);
    }

    public interface OnButtonClickListener {
        void onButtonClick(Product product, int position);
    }
    public static boolean isStringInteger(String stringToCheck, int radix) {
        try(  Scanner sc = new Scanner(stringToCheck.trim())){
            if(!sc.hasNextInt(radix))
                return false;
            sc.nextInt(radix);
            return !sc.hasNext();
        }


    }

}
