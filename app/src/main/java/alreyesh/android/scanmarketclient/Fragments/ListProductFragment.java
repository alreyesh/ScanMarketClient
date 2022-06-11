package alreyesh.android.scanmarketclient.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import alreyesh.android.scanmarketclient.Adapters.ProductAdapter;
import alreyesh.android.scanmarketclient.Dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.Dialog.DetailProductDialog;
import alreyesh.android.scanmarketclient.Model.Product;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;


public class ListProductFragment extends Fragment {
    ArrayList<Product> productsList;
    private FirebaseFirestore db;
    private GridView gView;
    private ProgressDialog pd;
    private SharedPreferences prefs;
    public ListProductFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pd= new ProgressDialog(getContext());
        prefs =getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);


        Integer colorparse = Util.getPurchaseColor(prefs);
        if(colorparse !=null){
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary,null)));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_list_product, container, false);

        db = FirebaseFirestore.getInstance();
        gView =(GridView) view.findViewById(R.id.am_gv_gridview);
        productsList = new ArrayList<>();


        loadDatainGridView();

        registerForContextMenu(gView);
        return view;
    }

    private void loadDatainGridView() {
        pd.setTitle("Cargando Lista");
        pd.show();
        db.collection("productos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)  {
                if (!queryDocumentSnapshots.isEmpty()){
                    productsList.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        // after getting this list we are passing
                        // that list to our object class.
                        Product products = d.toObject(Product.class);
                        products.setId(d.getReference().getId());
                        // after getting data from Firebase
                        // we are storing that data in our array list
                        productsList.add(products);

                    }

                    ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                    gView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pd.dismiss();

                }else {
                    // if the snapshot is empty we are displaying a toast message.
                    productsList.clear();
                    pd.dismiss();
                    Toast.makeText(getActivity(), "No se encontrar el Producto", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void loadDatainGridViewRep() {

        db.collection("productos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)  {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        // after getting this list we are passing
                        // that list to our object class.
                        Product products = d.toObject(Product.class);
                        products.setId(d.getReference().getId());
                        // after getting data from Firebase
                        // we are storing that data in our array list
                        productsList.add(products);

                    }
                    ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                    gView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }else {
                    // if the snapshot is empty we are displaying a toast message.

                    Toast.makeText(getActivity(), "No se encontrar el Producto", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.findItem(R.id.add_list_purchase).setVisible(false);
        menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(true);

        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
              searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (newText == " "||newText.isEmpty()||newText ==""|| newText == null) {
                    loadDatainGridView();
                }else{
                    searchspeed(newText);
                }

                return false;
            }
        });


       super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
      /*  if(item.getItemId() == R.id.action_settings){
            Toast.makeText(getContext(),"Settings", Toast.LENGTH_SHORT).show();
        }*/

        return super.onOptionsItemSelected(item);
    }

    private void searchData(String s){
        pd.setTitle("Buscando...");
        pd.show();
     // List busca =  Arrays.asList(s.toLowerCase(),s.toUpperCase(),s.toCa);
        db.collection("productos").orderBy("nombre").startAt(s).endAt(s + "\uf8ff").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)  {
                if (!queryDocumentSnapshots.isEmpty()){
                    productsList.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        // after getting this list we are passing
                        // that list to our object class.
                        Product products = d.toObject(Product.class);
                        products.setId(d.getReference().getId());
                        // after getting data from Firebase
                        // we are storing that data in our array list
                        productsList.add(products);

                    }
                    ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                    gView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pd.dismiss();

                }else {
                    // if the snapshot is empty we are displaying a toast message.
                    productsList.clear();
                    loadDatainGridViewRep();
                    pd.dismiss();
                    Toast.makeText(getActivity(), "No se encontrar el Producto", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void searchspeed(String s){
        db.collection("productos").orderBy("nombre").startAt(s).endAt(s + "\uf8ff").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>(){

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)  {
                if (!queryDocumentSnapshots.isEmpty()){
                    productsList.clear();
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {

                        // after getting this list we are passing
                        // that list to our object class.
                        Product products = d.toObject(Product.class);
                        products.setId(d.getReference().getId());
                        // after getting data from Firebase
                        // we are storing that data in our array list
                        productsList.add(products);

                    }
                    ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                    gView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pd.dismiss();

                }else {
                    // if the snapshot is empty we are displaying a toast message.
                    productsList.clear();
                    loadDatainGridViewRep();
                    pd.dismiss();
                    Toast.makeText(getActivity(), "No se encontrar el Producto", Toast.LENGTH_SHORT).show();
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });
    }
}