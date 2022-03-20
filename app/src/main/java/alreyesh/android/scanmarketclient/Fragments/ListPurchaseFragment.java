package alreyesh.android.scanmarketclient.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import alreyesh.android.scanmarketclient.Adapters.PurchaseAdapter;
import alreyesh.android.scanmarketclient.Dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ListPurchaseFragment extends Fragment implements RealmChangeListener<RealmResults<Purchase>> {
    private Realm realm;
    private RealmResults<Purchase> purchases;
    private RecyclerView recycler;
    private PurchaseAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth mAuth;


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
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menulistpurchase,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();
        Toast.makeText(getActivity(),userEmail,Toast.LENGTH_SHORT).show();
        purchases = realm.where(Purchase.class).equalTo("emailID",userEmail).findAll();
        if(purchases !=null){
            Toast.makeText(getActivity(),"hay purchases",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(),"NO hay purchases",Toast.LENGTH_SHORT).show();
        }
        purchases.addChangeListener(this);

        View view = inflater.inflate(R.layout.fragment_list_purchase, container, false);
       recycler=(RecyclerView) view.findViewById(R.id.recyclerView);
       recycler.setHasFixedSize(true);
       mLayoutManager = new LinearLayoutManager(getActivity());
       recycler.setLayoutManager(mLayoutManager);
        adapter = new PurchaseAdapter(purchases, R.layout.recycler_view_list_purchase_item,
                new PurchaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Purchase purchase, int position) {

                    }
                }, new PurchaseAdapter.OnButtonClickListener() {
            @Override
            public void onButtonClick(Purchase purchase, int position) {

            }
        });
        recycler.setAdapter(adapter);
        purchases.addChangeListener(this);

        return view;
    }

    @Override
    public void onChange(RealmResults<Purchase> purchases) {
        adapter.notifyDataSetChanged();
    }

}