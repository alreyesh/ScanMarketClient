package alreyesh.android.scanmarketclient.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import alreyesh.android.scanmarketclient.adapters.ProductAdapter;
import alreyesh.android.scanmarketclient.adapters.RelatedProductAdapter;

import alreyesh.android.scanmarketclient.camarascan.DetectorActivity;
import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;
import io.realm.RealmList;


public class RecommentFragment extends Fragment {
    private SharedPreferences prefs;
    private Realm realm;
    private Purchase purchase;
    private static final String PRODUCTNAME= "productName";
    private static final String MESSAGE = "Se Añadio el Producto en la cesta";

    public RecommentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs =Util.getSP(getActivity() );
        realm = Realm.getDefaultInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_recomment, container, false);
        String producto = Util.getProductFromCamera(prefs);
        String categoria = Util.getCategoryFromCamera(prefs);
        String predict = Util.getPredictFromCamera(prefs);
        TextView txtProductName = (TextView)view.findViewById(R.id.txtProductName);
        TextView txtProductoPrecio = (TextView)view.findViewById(R.id.txtProductoPrecio);
        EditText editProductoCantidad = (EditText)view.findViewById(R.id.editProductoCantidad);
        ImageView imageViewProducto = (ImageView)view.findViewById(R.id.imageViewProducto);
        Button btnAgregar = (Button)view.findViewById(R.id.btnAgregar);
        Button btnCapturar = (Button)view.findViewById(R.id.btnCapturar);
        RecyclerView recyclerViewProducto =(RecyclerView)view.findViewById(R.id.recyclerViewProducto);
        ArrayList<Product>  productsList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         recyclerViewProducto.setHasFixedSize(true);
        recyclerViewProducto.setItemAnimator(new DefaultItemAnimator());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewProducto.setLayoutManager(mLayoutManager);
        String countDefault = "1";
        if(Util.getPurchaseId(prefs) !=null){
            int purchaseId = Util.getPurchaseId(prefs);
            purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
            if(purchase !=null){
                String nombre = producto;
                RealmList<Cart> carts = purchase.getCarts();
                Cart verCart = carts.where().equalTo(PRODUCTNAME, nombre).findFirst();
                if (verCart != null){
                    editProductoCantidad.setHint(verCart.getCountProduct());
                    countDefault  = verCart.getCountProduct();

                }




            }


        }
        String finalCountDefault = countDefault;
        btnAgregar.setOnClickListener(v -> {
            int purchaseId = Util.getPurchaseId(prefs);
            purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
            if(purchase !=null){
                if (Util.getPurchaseId(prefs) != null){

                    String cantidad = editProductoCantidad.getText().toString().trim();
                    if (cantidad.isEmpty()   || cantidad.equals("")){
                        cantidad = finalCountDefault;

                        Cart cartin;
                        String nombre =  producto;
                        RealmList<Cart> carts = purchase.getCarts();
                        cartin = carts.where().equalTo(PRODUCTNAME, nombre).findFirst();
                        String codigo = Util.getCodigoFromCamera(prefs);
                        String imagen = Util.getImagenFromCamera(prefs);
                        String precio = Util.getPrecioFromCamera(prefs);
                        if (cartin == null || cartin.getRealm().isEmpty()){
                            cartin = new Cart(codigo, producto,imagen, precio, cantidad, precio);
                            realm.beginTransaction();
                            realm.copyToRealm(cartin);
                            purchase.getCarts().add(cartin);
                            realm.commitTransaction();
                            Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                            getActivity().invalidateOptionsMenu();
                        }else{
                            realm.beginTransaction();
                            cartin.setCountProduct(cantidad);
                            cartin.setSubPrice(precio);
                            realm.copyToRealmOrUpdate(cartin);
                            realm.commitTransaction();

                            Toast.makeText(getActivity(), "Se Actualizo el Producto en la Cesta", Toast.LENGTH_SHORT).show();
                            getActivity().invalidateOptionsMenu();
                        }

                    }else{
                        boolean isstringint = isStringInteger(cantidad, 10);
                        if (isstringint == true){
                            int cant = Integer.parseInt(cantidad);
                            if (cant > 0){
                                Cart cartin;

                                String nombre = Util.getProductFromCamera(prefs);
                                String codigo = Util.getCodigoFromCamera(prefs);
                                String imagen = Util.getImagenFromCamera(prefs);
                                String precios = Util.getPrecioFromCamera(prefs);

                                RealmList<Cart> carts = purchase.getCarts();
                                float precio = Float.parseFloat(precios);
                                float subprice = precio * cant;
                                String subpricestring = String.valueOf(subprice);
                                cartin = carts.where().equalTo(PRODUCTNAME,nombre).findFirst();
                                if (!carts.isEmpty()){
                                    if (cartin == null || cartin.getRealm().isEmpty()){
                                        realm.beginTransaction();
                                        cartin = new Cart(codigo,nombre,imagen,precios, cantidad, subpricestring);
                                        realm.copyToRealm(cartin);
                                        purchase.getCarts().add(cartin);
                                        realm.commitTransaction();
                                        editProductoCantidad.setHint(cantidad);
                                        editProductoCantidad.setText("");

                                        Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                                        getActivity().invalidateOptionsMenu();
                                    }else{
                                        realm.beginTransaction();
                                        cartin.setCountProduct(cantidad);
                                        cartin.setSubPrice(subpricestring);
                                        realm.copyToRealmOrUpdate(cartin);
                                        realm.commitTransaction();
                                        Toast.makeText(getActivity(), "Se Actualizo el Producto en la Cesta", Toast.LENGTH_SHORT).show();
                                        editProductoCantidad.setHint(cantidad);
                                        editProductoCantidad.setText("");
                                        getActivity().invalidateOptionsMenu();
                                    }
                                }else{
                                    realm.beginTransaction();
                                    cartin = new Cart(codigo,nombre,imagen,precios, cantidad, subpricestring);
                                    realm.copyToRealm(cartin);
                                    purchase.getCarts().add(cartin);
                                    realm.commitTransaction();
                                    editProductoCantidad.setHint(cantidad);
                                    editProductoCantidad.setText("");
                                    Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
                                    getActivity().invalidateOptionsMenu();
                                }


                            }else{
                                Toast.makeText(getActivity(), "  Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                            }




                        }else{
                            Toast.makeText(getActivity(), "Error al ingresar. Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                        }



                    }



                }
            }




        });
    btnCapturar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), DetectorActivity.class);
            startActivity(intent);
        }
    });


   db.collection("productos").whereEqualTo("predict",predict).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty()){

                        try {

                           productsList.clear();
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d:list){
                                Product products = d.toObject(Product.class);
                                products.setId(d.getReference().getId());
                                products.setNombre(products.getNombre().toLowerCase());
                                productsList.add(products);
                            }
                            txtProductName.setText(productsList.get(0).getNombre());
                            txtProductoPrecio.setText("S/. "+ productsList.get(0).getPrecio()+" c/u.");
                            Picasso.get().load(productsList.get(0).getImagen()).fit().into(imageViewProducto);

                            ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                        }
                        catch(NullPointerException e) {
                            System.out.println("NullPointerException thrown!");
                        }






                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
//.collection("productos").orderBy("predict").startAt(producto).endAt(producto+"\uf8ff")
       // Toast.makeText(getActivity(), "Categoria:"+ categoria, Toast.LENGTH_SHORT).show();

        db.collection("productos").whereEqualTo("categoria",categoria).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
       @Override
       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
           if (!queryDocumentSnapshots.isEmpty()){
               List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
               productsList.clear();
               int pos =0;
               for (DocumentSnapshot d : list) {
                    if(pos !=0) {
                        // after getting this list we are passing
                        // that list to our object class.
                        Product products = d.toObject(Product.class);
                        products.setId(d.getReference().getId());
                        products.setNombre(products.getNombre().toLowerCase());
                        // after getting data from Firebase
                        // we are storing that data in our array list

                        productsList.add(products);
                    }
                    pos++;
               }
               RelatedProductAdapter adapter = new RelatedProductAdapter(productsList, R.layout.recycler_view_list_related_product  );

               recyclerViewProducto.setAdapter(adapter);

               adapter.notifyDataSetChanged();

           }
       }
   }).addOnFailureListener(new OnFailureListener() {
       @Override
       public void onFailure(@NonNull Exception e) {

       }
   });





        return view;
    }
    public static boolean isStringInteger(String stringToCheck, int radix) {
        try( Scanner sc = new Scanner(stringToCheck.trim())){
            if(!sc.hasNextInt(radix)) return false;
            sc.nextInt(radix);
            return !sc.hasNext();
        }


    }
}