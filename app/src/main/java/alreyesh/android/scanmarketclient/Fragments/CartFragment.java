package alreyesh.android.scanmarketclient.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import alreyesh.android.scanmarketclient.Adapters.CartAdapter;
import alreyesh.android.scanmarketclient.Adapters.PurchaseAdapter;
import alreyesh.android.scanmarketclient.Models.Cart;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CartFragment extends Fragment  implements RealmChangeListener<RealmResults<Cart>> {

    private SharedPreferences prefs;
    private RealmList<Cart> carts;
    private RecyclerView recycler;
    private CartAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm realm;
    private Purchase purchase;
    private int purchaseId;
    public CartFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
View v =  inflater.inflate(R.layout.fragment_cart, container, false);
        prefs =getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        String titulo= Util.getPurchaseName(prefs);
        Integer colorparse = Util.getPurchaseColor(prefs);

        Toast.makeText(getActivity(), titulo, Toast.LENGTH_SHORT).show();

        realm = Realm.getDefaultInstance();
        if(Util.getPurchaseId(prefs) != null)
            purchaseId =  Util.getPurchaseId(prefs);
        purchase = realm.where(Purchase.class).equalTo("id",purchaseId).findFirst();
        carts = purchase.getCarts();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(titulo);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
        recycler=(RecyclerView) v.findViewById(R.id.recyclerViewCart);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);

        adapter = new CartAdapter(carts, R.layout.recycler_view_list_cart_item, new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Cart cart, int position) {
                showAlertForEditing(cart);
            }
        }, new CartAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(Cart cart, int position) {

            }
        });
        recycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deleteCart(viewHolder.getLayoutPosition());
            }
        }).attachToRecyclerView(recycler);



        return v;
    }

    @Override
    public void onChange(RealmResults<Cart> carts) {
        adapter.notifyDataSetChanged();
    }


    private void deleteCart(int position) {
        realm.beginTransaction();
        carts.get(position).deleteFromRealm();
        realm.commitTransaction();
    }
    private void deleteCarts(){
        realm.beginTransaction();
        // elimina todas las notas de esa board
        purchase.getCarts().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private void editCart(String subprice,String count,Cart  cart){
        realm.beginTransaction();
        cart.setSubPrice(subprice);
        cart.setCountProduct(count);
        realm.copyToRealmOrUpdate(cart);
        realm.commitTransaction();
        adapter.notifyDataSetChanged();

    }


    private void showAlertForEditing(Cart cart){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.detail_product_layout,null);
        builder.setView(viewInflated);
        final ImageView imgView = (ImageView) viewInflated.findViewById(R.id.imgProductView);
        final EditText cantidad = (EditText) viewInflated.findViewById(R.id.editCantidad);
        final Button btnRegister =(Button)viewInflated.findViewById(R.id.btnRegistrarEdit) ;
        final Button btnCancelar =(Button)viewInflated.findViewById(R.id.btnCancelarEdit) ;
        cantidad.setHint(cart.getCountProduct());
        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER );
        cantidad.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        cantidad.setSingleLine(true);
        Picasso.get().load(cart.getImagenProduct()).fit().into(imgView);
        AlertDialog dialog = builder.create();
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countText = cantidad.getText().toString().trim();
                Toast.makeText(getActivity(), countText, Toast.LENGTH_SHORT).show();
              if(countText !=null|| !countText.isEmpty()|| countText != "") {

                                    int counInt = Integer.valueOf(countText);

                                  // Toast.makeText(getActivity(), counInt+"", Toast.LENGTH_SHORT).show();

                                  float proprice = Float.parseFloat(cart.getProductPrice());
                                  Toast.makeText(getActivity(), proprice+"", Toast.LENGTH_SHORT).show();
                                    float propricefloat = counInt * proprice;
                                    String result = String.valueOf(propricefloat);
                                  //  editCart(result,countText,cart);
                                    //Toast.makeText(getActivity(), "Se Actualizo", Toast.LENGTH_SHORT).show();

                }
                dialog.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }
}