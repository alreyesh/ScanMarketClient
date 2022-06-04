package alreyesh.android.scanmarketclient.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import alreyesh.android.scanmarketclient.Model.Product;
import alreyesh.android.scanmarketclient.Models.Cart;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
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
        Picasso.get().load(products.getImagen()).fit().into(imgProductView);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Util.getPurchaseId(prefs) !=null){
                    int purchaseId = Util.getPurchaseId(prefs);
                    String lista = Util.getPurchaseName(prefs);
                    String cantidad = editCountProduct.getText().toString().trim();
                    if(cantidad.isEmpty() || cantidad == null|| cantidad =="" ){
                        cantidad ="1";
                    }
                    //parseInteger
                    int cant = Integer.parseInt(cantidad);
                    float precio = Float.parseFloat(products.getPrecio());
                    float subprice = precio * cant;
                    //parseString
                    String subpricestring = String.valueOf(subprice);
                    Toast.makeText(getActivity(),cantidad,Toast.LENGTH_SHORT).show();
                 purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
                    Toast.makeText(getActivity(),purchase.getName(),Toast.LENGTH_SHORT).show();
                     realm.beginTransaction();
                    Cart cartin =  new Cart(products.getCodigo(),products.getNombre(),products.getImagen(),products.getPrecio(),cantidad,subpricestring);
                    realm.copyToRealm(cartin);
                    purchase.getCarts().add(cartin);
                    realm.commitTransaction();
                    Toast.makeText(getActivity(),"Se añadio a la lista:"+lista,Toast.LENGTH_SHORT).show();
                    dismiss();
                }else{
                    Toast.makeText(getActivity(),"Seleccione una lista de Compra",Toast.LENGTH_SHORT).show();
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
    private void searchData(String s){
        CollectionReference productos = db.collection("productos");




    }



}
