package alreyesh.android.scanmarketclient.fragments;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import alreyesh.android.scanmarketclient.adapters.CartAdapter;
import alreyesh.android.scanmarketclient.dialog.QrDialog;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmResults;


public class CartFragment extends Fragment  implements RealmChangeListener<RealmResults<Cart>> {

    private SharedPreferences prefs;
    private RealmList<Cart> carts;
     RecyclerView recycler;
    private CartAdapter adapter;
     TextView txtListadoCompra;
      RecyclerView.LayoutManager mLayoutManager;
      RecyclerView mRecycler;
    private Realm realm;
    private Purchase purchase;
     int purchaseId;
    FirebaseAuth mAuth;
    Button  btnPagar;
    MenuItem menuId;

      float totalCart;
    private static final String STARTNOTIFY = "startnotify";
    public CartFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_delete_all){
            deleteCarts();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.add_list_purchase).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_delete_all).setVisible(true);


        super.onCreateOptionsMenu(menu, inflater);

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
        prefs =Util.getSP(getContext());
        mAuth = FirebaseAuth.getInstance();
        String titulo= Util.getPurchaseName(prefs);
        Integer colorparse = Util.getPurchaseColor(prefs);
        btnPagar = v.findViewById(R.id.btnPagar);
        btnPagar.setVisibility(View.GONE);
        mRecycler= v.findViewById(R.id.recyclerViewCart);

        realm = Realm.getDefaultInstance();
        if(Util.getPurchaseId(prefs) != null){
            purchaseId =  Util.getPurchaseId(prefs);
            FirebaseUser user = mAuth.getCurrentUser();
            String userEmail = user.getEmail();
            purchase = realm.where(Purchase.class).equalTo("id",purchaseId).equalTo("emailID",userEmail).findFirst();
           // purchase.getCarts().size() >0
            //purchase != null
            if(!purchase.getCarts().isEmpty()){
                carts = purchase.getCarts();
                Integer color = Util.getPurchaseColor(prefs);
                btnPagar.setVisibility(View.VISIBLE);
                btnPagar.setBackgroundColor(color);
                btnPagar.setOnClickListener(v1 -> {




                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    QrDialog qrdialog = new QrDialog();
                    qrdialog.show(manager,"QR");
                });


                List<Cart> list = new ArrayList<>();
                list.addAll(carts);
                for(int i =0; i<list.size();i++){

                    String subtotal =  list.get(i).getSubPrice();
                    float parsesubtotal = Float.parseFloat(subtotal);
                    totalCart+= parsesubtotal;
                }


                txtListadoCompra= (TextView)v.findViewById(R.id.txtListadoCompra);
                txtListadoCompra.setText(titulo);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
                recycler=(RecyclerView) v.findViewById(R.id.recyclerViewCart);
                recycler.setHasFixedSize(true);
                recycler.setItemAnimator(new DefaultItemAnimator());
                mLayoutManager = new LinearLayoutManager(getActivity());
                recycler.setLayoutManager(mLayoutManager);

                adapter = new CartAdapter(carts, R.layout.recycler_view_list_cart_item, (cart, position) -> showAlertForEditing(cart)  );
                recycler.setAdapter(adapter);

                getActivity().invalidateOptionsMenu();

                adapter.notifyDataSetChanged();

            }

        }


        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        Toast.makeText(getActivity(), "Cambio:"+carts.size(), Toast.LENGTH_SHORT).show();
    }



    private void deleteCart(int position) {
        realm.beginTransaction();
        carts.get(position).deleteFromRealm();
        realm.commitTransaction();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(STARTNOTIFY,true);
        editor.putBoolean("qrstate",false);
        editor.commit();
        getActivity().invalidateOptionsMenu();

        if(carts.isEmpty())
            btnPagar.setVisibility(View.GONE);


        adapter.notifyDataSetChanged();
    }
    private void deleteCarts(){
        realm.beginTransaction();
          purchase.getCarts().deleteAllFromRealm();
        realm.commitTransaction();
        getActivity().invalidateOptionsMenu();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(STARTNOTIFY,true);
        editor.putBoolean("qrstate",false);
        editor.commit();
        btnPagar.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }
    private void editCart(String subprice,String count,Cart  cart){
        realm.beginTransaction();
        cart.setSubPrice(subprice);
        cart.setCountProduct(count);
        realm.copyToRealmOrUpdate(cart);
        realm.commitTransaction();
        adapter.notifyDataSetChanged();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(STARTNOTIFY,true);
        editor.commit();
        getActivity().invalidateOptionsMenu();
    }
    private void showAlertForEditing(Cart cart){


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.edit_list_cart_dialog_layout,null);

        builder.setView(viewInflated);
        final ImageView imgView = (ImageView) viewInflated.findViewById(R.id.imgProductView);
        final EditText cantidad = (EditText) viewInflated.findViewById(R.id.editCantidadCart);
        final Button btnRegister =(Button)viewInflated.findViewById(R.id.btnRegistrarEditCart) ;
        final Button btnCancelar =(Button)viewInflated.findViewById(R.id.btnCancelarEdit) ;
        final TextView txtCodidgod = (TextView) viewInflated.findViewById(R.id.textViewCod);
        final TextView txtNombre = (TextView) viewInflated.findViewById(R.id.textViewName);
        final TextView txtPrecioprod = (TextView) viewInflated.findViewById(R.id.textViewPrice);
        cantidad.setHint(cart.getCountProduct());
        txtNombre.setText(cart.getProductName());
        txtCodidgod.setText("sku: "+cart.getProductID());
        txtPrecioprod.setText("S/. "+cart.getProductPrice()+" c/u");
        cantidad.setInputType(InputType.TYPE_CLASS_NUMBER );
        cantidad.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        cantidad.setSingleLine(true);
        Picasso.get().load(cart.getImagenProduct()).fit().into(imgView);
        AlertDialog dialog = builder.create();
        btnRegister.setOnClickListener(v -> {
            String countText = cantidad.getText().toString().trim();
               if(countText !=null) {

              boolean isstringint = isStringInteger(countText, 10);
              if(isstringint){
                  int counInt = Integer.parseInt(countText);
                    if(counInt >0){
                        float proprice = Float.parseFloat(cart.getProductPrice());
                        float propricefloat = counInt * proprice;
                        String result = String.valueOf(propricefloat);
                        editCart(result,countText,cart);

                        Toast.makeText(getActivity(), "Se Actualizo", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }else{
                        Toast.makeText(getActivity(), "Ingresar valor mayor a 0", Toast.LENGTH_SHORT).show();

                    }

              }else{
                  Toast.makeText(getActivity(), "Ingresar un numero entero mayor o igual 1", Toast.LENGTH_SHORT).show();

              }

            }

        });
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    public static boolean isStringInteger(String stringToCheck, int radix) {
        try(  Scanner sc = new Scanner(stringToCheck.trim())){
            if(!sc.hasNextInt(radix)) return false;
            sc.nextInt(radix);
            return !sc.hasNext();
        }


    }

}