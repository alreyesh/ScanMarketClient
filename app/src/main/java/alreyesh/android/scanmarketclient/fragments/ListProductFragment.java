package alreyesh.android.scanmarketclient.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

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
import alreyesh.android.scanmarketclient.dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.model.Product;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.notifications.NotificacionPush;
import alreyesh.android.scanmarketclient.utils.Util;


public class ListProductFragment extends Fragment {
    ArrayList<Product> productsList;
    ArrayList<Product> productsTestList;
    ArrayList<Product> productsSearchTest;
    private FirebaseFirestore db;
    private GridView gView;
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

        db = FirebaseFirestore.getInstance();
        gView =(GridView) view.findViewById(R.id.am_gv_gridview);
        productsList = new ArrayList<>();
        productsTestList = new ArrayList<>();

        loadDatainGridView();
        Integer purchase = Util.getPurchaseId(prefs);
        if( purchase.equals(0)){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Registrar")
                    .setContentText("Registre y seleccione un listado de compras antes de añadir los productos")
                    .setConfirmText("Ir")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("cep",false);
                            editor.putBoolean("newp",true);
                            editor.commit();

                            AddListPurchaseDialog addListPurchaseDialog = new AddListPurchaseDialog();
                            addListPurchaseDialog.show(getActivity().getSupportFragmentManager(),"addListPurchase");

                        }
                    })
                    .show();


        }


        registerForContextMenu(gView);
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

                ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                gView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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

    private void loadDatainGridViewRep() {

        db.collection(PRODUCTOS).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
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
                ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                gView.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }else {
                // if the snapshot is empty we are displaying a toast message.

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
                      loadDatainGridView();
                }else{
                     searchspeed(newText);
                }

                return false;
            }
        });


       super.onCreateOptionsMenu(menu, inflater);
    }
/*
    private void searchTest(String s){
        if (s.length() > 0){

            productsSearchTest = new ArrayList<>();
            for(Product p: productsTestList){
                String title = p.getNombre().toLowerCase();
                if(title.contains(s.toLowerCase())){
                    productsSearchTest.add(p);

                }
            }
            ProductAdapter adapter = new ProductAdapter(getActivity(),productsSearchTest);
            gView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else{
            productsSearchTest = new ArrayList<>();
            for(Product p: productsTestList){

                    productsSearchTest.add(p);

            }
            ProductAdapter adapter = new ProductAdapter(getActivity(),productsSearchTest);
            gView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        }

    }
*/
    private void searchData(String s){
        pd.setTitle("Buscando...");
        pd.show();
          db.collection(PRODUCTOS).orderBy("nombre").startAt(s).endAt(s + "\uf8ff").get().addOnSuccessListener(queryDocumentSnapshots -> {
              if (!queryDocumentSnapshots.isEmpty()){
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
                  ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                  gView.setAdapter(adapter);
                  adapter.notifyDataSetChanged();
                  pd.dismiss();

              }else {
                  // if the snapshot is empty we are displaying a toast message.
                  productsList.clear();
                  loadDatainGridViewRep();
                  pd.dismiss();
                  Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
              }


          }).addOnFailureListener(e -> {
              pd.dismiss();
              Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
          });



    }

    private void searchspeed(String s){
        db.collection(PRODUCTOS).orderBy("nombre").startAt(s).endAt(s + "\uf8ff").get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
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
                ProductAdapter adapter = new ProductAdapter(getActivity(),productsList);
                gView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                pd.dismiss();

            }else {
                // if the snapshot is empty we are displaying a toast message.
                productsList.clear();
                loadDatainGridViewRep();
                pd.dismiss();
                Toast.makeText(getActivity(), MESSAGE, Toast.LENGTH_SHORT).show();
            }


        }).addOnFailureListener(e -> {
            pd.dismiss();
            Toast.makeText(getActivity(), ERROR, Toast.LENGTH_SHORT).show();
        });
    }
}