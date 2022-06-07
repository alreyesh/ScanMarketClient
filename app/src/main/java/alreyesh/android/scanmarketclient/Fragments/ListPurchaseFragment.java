package alreyesh.android.scanmarketclient.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rowland.cartcounter.view.CartCounterActionView;

import java.util.List;

import alreyesh.android.scanmarketclient.Adapters.PurchaseAdapter;
import alreyesh.android.scanmarketclient.Dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ListPurchaseFragment extends Fragment implements RealmChangeListener<RealmResults<Purchase>> {
    private Realm realm;
    private RealmResults<Purchase> purchases;
    private RecyclerView recycler;
    private PurchaseAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private   CartCounterActionView actionviewCart;
    private  MenuItem menuId;
    private int cantCart;
    private int purchaseId;
    private Purchase purchase;
    public ListPurchaseFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     setHasOptionsMenu(true);


    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (Util.getPurchaseId(prefs) != null){
            purchaseId = Util.getPurchaseId(prefs);
        purchase = realm.where(Purchase.class).equalTo("id", purchaseId).findFirst();
                    if(purchase !=null){
                    cantCart = purchase.getCarts().size();
                    actionviewCart = (CartCounterActionView)menuId.getActionView();
                    Toast.makeText(getActivity(),"Cart: "+cantCart, Toast.LENGTH_SHORT).show();
                    actionviewCart.setItemData(menu,menuId);
                    if(cantCart>0)
                        actionviewCart.setCount(cantCart );
                    else     actionviewCart.setCount(0);
                    }
            }



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menulistpurchase,menu);
        if (Util.getPurchaseId(prefs) != null){
            purchaseId = Util.getPurchaseId(prefs);
            purchase = realm.where(Purchase.class).equalTo("id", purchaseId).findFirst();
            if(purchase !=null){
                cantCart = purchase.getCarts().size();
                menuId=  menu.findItem(R.id.action_addcart);
                actionviewCart = (CartCounterActionView)menuId.getActionView();
                Toast.makeText(getActivity(),"Cart: "+cantCart, Toast.LENGTH_SHORT).show();
                actionviewCart.setItemData(menu,menuId);
                if(cantCart>0)
                    actionviewCart.setCount(cantCart );
                else     actionviewCart.setCount(0);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_all:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        realm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        Toast.makeText(getActivity(),userEmail,Toast.LENGTH_SHORT).show();
        purchases = realm.where(Purchase.class).equalTo("emailID",userEmail).findAll().sort("id", Sort.DESCENDING);
        prefs =getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
       purchases.addChangeListener(this);

        View view = inflater.inflate(R.layout.fragment_list_purchase, container, false);
       recycler=(RecyclerView) view.findViewById(R.id.recyclerView);
       recycler.setHasFixedSize(true);
       recycler.setItemAnimator(new DefaultItemAnimator());
       mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);
        adapter = new PurchaseAdapter(purchases, R.layout.recycler_view_list_purchase_item,
                new PurchaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Purchase purchase, int position) {
                            CartFragment cartFragment = new CartFragment();
                        /*    FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_list_purchase,purchaseHistoryFragment);
                            fragmentTransaction.commit();*/
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("idp",purchase.getId());
                        editor.putString("np",purchase.getName());
                        editor.putInt("cp",purchase.getColor());
                            editor.commit();
                        cantCart  =purchase.getCarts().size();
                      getActivity().invalidateOptionsMenu();
                       getActivity().getSupportFragmentManager()
                                .beginTransaction().replace(R.id.content_frame,cartFragment)
                                .commit();


                    }
                }, new PurchaseAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(Purchase purchase, int position) {

            }
        }


        );





        recycler.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                deletePurchases(viewHolder.getLayoutPosition());
            }
        }).attachToRecyclerView(recycler);

     purchases.addChangeListener(this);

        return view;
    }

    @Override
    public void onChange(RealmResults<Purchase> purchases) {
        adapter.notifyDataSetChanged();
    }
    private void deletePurchases(int position) {
        realm.beginTransaction();
        purchases.get(position).deleteFromRealm();
        realm.commitTransaction();
    }


}