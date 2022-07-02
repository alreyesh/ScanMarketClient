package alreyesh.android.scanmarketclient.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.Scanner;

import alreyesh.android.scanmarketclient.Model.Product;
import alreyesh.android.scanmarketclient.Models.Cart;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class DetailProductDialog  extends DialogFragment{
    private TextView editCodProduct;
    private TextView editNameProduct;
    private TextView editDescProduct;
    private TextView editPriceProduct;
    private EditText editCountProduct;
    private ImageView imgProductView;
    private Button btnRegistrar,btnCancelar;
    private SharedPreferences prefs;
    private RealmResults<Cart>carts;
    private Purchase purchase;
    private int purchaseId;
    private FirebaseFirestore db;
    private Realm realm;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Context context;
        db = FirebaseFirestore.getInstance();
        realm = Realm.getDefaultInstance();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.detail_product_layout,null);
        editCodProduct=(TextView) view.findViewById(R.id.textViewCod);
        editNameProduct=(TextView) view.findViewById(R.id.textViewName);
        editDescProduct=(TextView)  view.findViewById(R.id.textViewDesc);
        editPriceProduct=(TextView)  view.findViewById(R.id.textViewPrice);
        editCountProduct = (EditText) view.findViewById(R.id.editCantidad) ;
        imgProductView = (ImageView) view.findViewById(R.id.imgProductView);
        btnRegistrar = (Button) view.findViewById(R.id.btnRegistrarEdit);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelarEdit);
        prefs =getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String pro = Util.getProduct(prefs);
        int idp = Util.getPurchaseId(prefs);

        Product  products = gson.fromJson(pro,Product.class);
        //searchData( idpro);
        editCodProduct.setText("sku: "+products.getCodigo());
        editNameProduct.setText(products.getNombre());
        editDescProduct.setText(products.getDescripcion());
        editPriceProduct.setText("S/. "+products.getPrecio());
        editCountProduct.setHint("1");
        editCountProduct.setInputType(InputType.TYPE_CLASS_NUMBER );
        String countDefault="1";
        editCountProduct.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        editCountProduct.setSingleLine(true);
        if(Util.getPurchaseId(prefs) !=null){
            int purchaseId = Util.getPurchaseId(prefs);
            purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
            if(purchase !=null) {
                String nombre = products.getNombre();
                RealmList<Cart> carts = purchase.getCarts();
                Cart verCart = carts.where().equalTo("productName", nombre).findFirst();
                if (verCart != null) {
                    editCountProduct.setHint(verCart.getCountProduct());
                    countDefault = verCart.getCountProduct();
                }
            }

        }


        editCodProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        Picasso.get().load(products.getImagen()).fit().into(imgProductView);
        String finalCountDefault = countDefault;
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int purchaseId = Util.getPurchaseId(prefs);
                purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
                if(purchase !=null) {
                    if (Util.getPurchaseId(prefs) != null) {


                        String lista = Util.getPurchaseName(prefs);
                        String cantidad = editCountProduct.getText().toString().trim();
                        if (cantidad.isEmpty() || cantidad == null || cantidad == "") {
                            cantidad = finalCountDefault;

                            Cart cartin;
                            String nombre = products.getNombre();

                            RealmList<Cart> carts = purchase.getCarts();
                            cartin = carts.where().equalTo("productName", nombre).findFirst();
                            if (cartin == null || cartin.getRealm().isEmpty()) {
                                cartin = new Cart(products.getCodigo(), products.getNombre(), products.getImagen(), products.getPrecio(), cantidad, products.getPrecio());
                                realm.beginTransaction();
                                realm.copyToRealm(cartin);
                                purchase.getCarts().add(cartin);
                                realm.commitTransaction();
                                dismiss();
                                Toast.makeText(getActivity(), "Se añadio a la lista:" + lista, Toast.LENGTH_SHORT).show();
                                } else {

                                realm.beginTransaction();
                                cartin.setCountProduct(cantidad);
                                cartin.setSubPrice(products.getPrecio());
                                realm.copyToRealmOrUpdate(cartin);
                                realm.commitTransaction();


                                       dismiss();
                            }


                        } else {
                            boolean isstringint = isStringInteger(cantidad, 10);

                            if (isstringint == true) {

                                //parseInteger

                                int cant = Integer.parseInt(cantidad);
                                if (cant > 0) {
                                    Cart cartin;

                                    String nombre = products.getNombre();

                                    RealmList<Cart> carts = purchase.getCarts();
                                    float precio = Float.parseFloat(products.getPrecio());
                                    float subprice = precio * cant;
                                    String subpricestring = String.valueOf(subprice);
                                    cartin = carts.where().equalTo("productName", nombre).findFirst();

                                    if (carts.size() > 0) {

                                        if (cartin == null || cartin.getRealm().isEmpty()) {

                                            //parseString
                                            realm.beginTransaction();
                                            cartin = new Cart(products.getCodigo(), products.getNombre(), products.getImagen(), products.getPrecio(), cantidad, subpricestring);
                                            realm.copyToRealm(cartin);
                                            purchase.getCarts().add(cartin);
                                            realm.commitTransaction();

                                            Toast.makeText(getActivity(), "Producto Actualizado: " + lista, Toast.LENGTH_SHORT).show();
                                            dismiss();

                                        } else {
                                            Toast.makeText(getActivity(), "resultado " + cartin.getProductName(), Toast.LENGTH_SHORT).show();
                                            realm.beginTransaction();
                                            cartin.setCountProduct(cantidad);
                                            cartin.setSubPrice(subpricestring);
                                            realm.copyToRealmOrUpdate(cartin);
                                            realm.commitTransaction();

                                              dismiss();

                                        }


                                    } else {
                                        realm.beginTransaction();
                                        cartin = new Cart(products.getCodigo(), products.getNombre(), products.getImagen(), products.getPrecio(), cantidad, subpricestring);
                                        realm.copyToRealm(cartin);
                                        purchase.getCarts().add(cartin);
                                        realm.commitTransaction();

                                          dismiss();


                                    }


                                    dismiss();


                                } else {
                                    Toast.makeText(getActivity(), "Error al ingresar. Ingresar valor mayor o igual a 1", Toast.LENGTH_SHORT).show();

                                }


                            } else {
                                Toast.makeText(getActivity(), "Ingresar  un valor entero a partir del 1", Toast.LENGTH_SHORT).show();

                            }
                        }
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("startnotify",true);
                        editor.commit();


                        //Actualización del menu bar
                        getActivity().invalidateOptionsMenu();

                    } else {
                        Toast.makeText(getActivity(), "Seleccione una lista de Compra", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Seleccionar un listado", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(view);


        return builder.create();
    }


    public static boolean isStringInteger(String stringToCheck, int radix) {
        Scanner sc = new Scanner(stringToCheck.trim());
        if(!sc.hasNextInt(radix)) return false;
        sc.nextInt(radix);
        return !sc.hasNext();
    }

}
