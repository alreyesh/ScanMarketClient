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
import java.util.Scanner;

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

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.add_list_purchase).setVisible(true);
        menu.findItem(R.id.action_delete_all).setVisible(true);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete_all:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                getActivity().invalidateOptionsMenu();
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

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("idp", purchase.getId());
                        editor.putString("np", purchase.getName());
                        editor.putInt("cp", purchase.getColor());
                        editor.putFloat("limitp", purchase.getLimit());
                        editor.commit();
                        cantCart = purchase.getCarts().size();
                        getActivity().invalidateOptionsMenu();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().replace(R.id.content_frame, cartFragment)
                                .commit();


                    }
                }, new PurchaseAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(Purchase purchase, int position) {

            }
        }, new PurchaseAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(Purchase purchase, int position) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("cep",true);
                editor.putInt("sp",purchase.getId());
                editor.commit();

                AddListPurchaseDialog addListPurchaseDialog = new AddListPurchaseDialog();
                addListPurchaseDialog.show(getActivity().getSupportFragmentManager(),"addListPurchase");

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
        getActivity().invalidateOptionsMenu();
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
        getActivity().invalidateOptionsMenu();
        adapter.notifyDataSetChanged();
    }

}