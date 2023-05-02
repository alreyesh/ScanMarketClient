package alreyesh.android.scanmarketclient.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cazaea.sweetalert.SweetAlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;



import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.adapters.PendingAdapter;

import alreyesh.android.scanmarketclient.models.Pending;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class PurchasePendingFragment extends Fragment implements RealmChangeListener<RealmResults<Pending>> {
    private Realm realm;
    private RealmResults<Pending> pendings;
    FirebaseAuth mAuth;
    RecyclerView recycler;
    RecyclerView.LayoutManager mLayoutManager;
    private PendingAdapter adapter;
    private FirebaseFirestore db;
    public PurchasePendingFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_purchase_pending, container, false);
        realm = Realm.getDefaultInstance();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();

        recycler=(RecyclerView) v.findViewById(R.id.recyclerpending);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(mLayoutManager);
        pendings =  realm.where(Pending.class) .equalTo("user",userEmail).findAll().sort("id", Sort.DESCENDING);
        adapter = new PendingAdapter(pendings, R.layout.recycler_view_list_pending, (pending, position) -> {
            // Do nothing
        });
        recycler.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Eliminar")
                        .setContentText("¿Desea eliminar la canasta pendiente?")
                        .setConfirmText("Si")
                        .setConfirmClickListener(sDialog -> {
                            deletePending(viewHolder.getLayoutPosition());
                            sDialog.dismissWithAnimation();

                        }).setCancelText("No")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();





            }
        }).attachToRecyclerView(recycler);



        pendings.addChangeListener(this);
        getActivity().invalidateOptionsMenu();
         identificar(pendings);
        return v;
    }

    private void deletePending(int position) {
        realm.beginTransaction();
        pendings.get(position).deleteFromRealm();
        realm.commitTransaction();
        adapter.notifyDataSetChanged();
    }

    private void identificar(RealmResults<Pending> p ) {

        for(int i = 0; i < p.size(); i++) {
            int position = i;
          String cod=pendings.get(i).getCodorder();
            Toast.makeText(getContext(),"codigo: "+ cod,Toast.LENGTH_SHORT).show();
          db.collection("order").whereEqualTo("codorder", cod).limit(1).get()
                  .addOnCompleteListener(task -> {
                    Boolean isEmpty = task.getResult().isEmpty();
                      Toast.makeText(getContext(),"estado: "+ isEmpty,Toast.LENGTH_SHORT).show();
                      if(Boolean.FALSE.equals(isEmpty)){
                          realm.beginTransaction();
                          pendings.get(position).deleteFromRealm();
                          realm.commitTransaction();


                      }



                  });
            pendings.addChangeListener(this);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onChange(RealmResults<Pending> pendings) {
        adapter.notifyDataSetChanged();
    }
}