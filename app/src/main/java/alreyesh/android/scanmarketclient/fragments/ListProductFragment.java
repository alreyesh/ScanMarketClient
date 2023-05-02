package alreyesh.android.scanmarketclient.fragments;


import android.app.ProgressDialog;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;


import com.cazaea.sweetalert.SweetAlertDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

import alreyesh.android.scanmarketclient.adapters.ProductAdapter;
import alreyesh.android.scanmarketclient.adapters.ProductRecyclerViewAdapter;

import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.R;

import alreyesh.android.scanmarketclient.utils.Util;


public class ListProductFragment extends Fragment {
    ArrayList<Product> productsList;
    ArrayList<Product> productsTestList;
    ArrayList<Product> productsSearchTest;
    private FirebaseFirestore db;
    private GridView gView;
    RecyclerView recycler;
    RecyclerView.LayoutManager mLayoutManager;
    private ProductRecyclerViewAdapter adapter;


    private static final String PRODUCTOS = "productos";
    private static final String MESSAGE = "No se encontro el Producto";
    private static final String ERROR = "Error al cargar los datos";
    ProgressDialog pd = null;

     SharedPreferences prefs;
    public ListProductFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pd= new ProgressDialog(getContext());
        prefs = Util.getSP(getActivity());
      ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        Integer colorparse = Util.getPurchaseColor(prefs);
        if(colorparse !=null){
          ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
        }else{
          ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary,null)));
        }
        db = FirebaseFirestore.getInstance();

        productsList = new ArrayList<>();
        productsTestList = new ArrayList<>();






    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_list_product, container, false);



        recycler=(RecyclerView) view.findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new GridLayoutManager(getContext(), 3);
        recycler.setLayoutManager(mLayoutManager);
        loadDatainGridView();


        Integer purchase = Util.getPurchaseId(prefs);

        if( purchase.equals(-1)){
           new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Registrar")
                    .setContentText("Registre y/o seleccione un listado de compras antes de añadir los productos")
                    .setConfirmText("Ir")
                    .setConfirmClickListener(sDialog -> {
                        sDialog.dismissWithAnimation();
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("cep",false);
                        editor.putBoolean("newp",true);
                        editor.commit();

                        getActivity().getSupportFragmentManager()
                                .beginTransaction().replace(R.id.content_frame,new ListPurchaseFragment())
                                .commit();
                    })
                    .show();








        }



        getActivity().invalidateOptionsMenu();
        return view;
    }

    private void loadDatainGridView() {

        pd.setTitle("Cargando Lista");
        pd.show();
        db.collection(PRODUCTOS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
              if(productsList !=null)
                  productsList.clear();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot d : list) {

                    // after getting this list we are passing
                    // that list to our object class.
                    Product products = d.toObject(Product.class);
                    products.setId(d.getReference().getId());
                    products.setNombre(products.getNombre().toUpperCase());

                    // after getting data from Firebase
                    // we are storing that data in our array list
                    productsList.add(products);

                }


                adapter = new ProductRecyclerViewAdapter(productsList, R.layout.list_product, new ProductRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Product products, int position) {

                    }
                });

                recycler.setAdapter(adapter);


          pd.dismiss();

            }else {
                // if the snapshot is empty we are displaying a toast message.
                productsList.clear();
           pd.dismiss();
                Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
            }


        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
        });



    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
          menu.findItem(R.id.add_list_purchase).setVisible(false);
         menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.action_search).setVisible(true);

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

                if (newText.equals(" ") ||newText.isEmpty() ) {

                      loadDatainGridViewinData();
                }else{
                     searchspeed(newText);
                }

                return false;
            }
        });


       super.onCreateOptionsMenu(menu, inflater);
    }

    private void loadDatainGridViewinData() {

        adapter = new ProductRecyclerViewAdapter(productsList, R.layout.list_product, new ProductRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product products, int position) {

            }
        });

        recycler.setAdapter(adapter);


    }


    private void searchData(String s){
        pd.setTitle("Buscando...");
        pd.show();

     ArrayList<Product> milista = new ArrayList<>();
     for(Product obj: productsList){
         if(obj.getNombre().toLowerCase().contains(s.toLowerCase())){
             milista.add(obj);
         }
     }

        adapter = new ProductRecyclerViewAdapter(milista, R.layout.list_product, new ProductRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product products, int position) {

            }
        });
        adapter.notifyDataSetChanged();
        recycler.setAdapter(adapter);



        pd.dismiss();
    }

    private void searchspeed(String s){

        ArrayList<Product> milista = new ArrayList<>();
        for(Product obj: productsList){
            if(obj.getNombre().toLowerCase().contains(s.toLowerCase())){
                milista.add(obj);
            }
        }

        adapter = new ProductRecyclerViewAdapter(milista, R.layout.list_product, new ProductRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product products, int position) {

            }
        });

        recycler.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }
}