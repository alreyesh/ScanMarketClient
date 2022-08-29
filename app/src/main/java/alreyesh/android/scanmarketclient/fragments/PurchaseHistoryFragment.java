package alreyesh.android.scanmarketclient.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.DateTime;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.adapters.OrderAdapter;
import alreyesh.android.scanmarketclient.dialog.DetailOrderDialog;
import alreyesh.android.scanmarketclient.models.DetailProduct;
import alreyesh.android.scanmarketclient.models.Order;
import alreyesh.android.scanmarketclient.utils.Util;


public class PurchaseHistoryFragment extends Fragment {
    ArrayList<Order> orderList;
    RecyclerView recyclerhistory;
    private OrderAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    private FirebaseFirestore db;
    SharedPreferences prefs;
    ProgressDialog pd = null;
    ImageButton imgDatePicker;
    public PurchaseHistoryFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        pd= new ProgressDialog(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_purchase_history, container, false);
        recyclerhistory = v.findViewById(R.id.recyclerhistory);
        imgDatePicker = v.findViewById(R.id.imgDatePicker);
        orderList = new ArrayList<>();

        recyclerhistory.setHasFixedSize(true);
        recyclerhistory.setItemAnimator(new DefaultItemAnimator());
        mLayoutManager = new LinearLayoutManager(getActivity());
        db = FirebaseFirestore.getInstance();
        recyclerhistory.setLayoutManager(mLayoutManager);
       // adapter = new OrderAdapter()
        prefs = Util.getSP(getActivity());
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        CalendarConstraints.Builder calendarConstraintBuilder = new CalendarConstraints.Builder();
        calendarConstraintBuilder.setValidator(DateValidatorPointBackward.now());
        materialDateBuilder.setTitleText("SELECCIONAR FECHA DE INICIO Y FIN");
        materialDateBuilder.setCalendarConstraints(calendarConstraintBuilder.build());
        final MaterialDatePicker<Pair<Long,Long> > materialDatePicker = materialDateBuilder.build();
        imgDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {


                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTimeInMillis(selection.first  );
                calendar1.set(Calendar.HOUR_OF_DAY, 0);
                calendar1.add(Calendar.DATE, 1);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTimeInMillis(selection.second  );
                calendar2.set(Calendar.AM_PM, Calendar.PM );
                calendar2.set(Calendar.HOUR_OF_DAY,23);
                calendar2.set(Calendar.MINUTE,59);
                calendar2.set(Calendar.SECOND,59);
                calendar2.add(Calendar.DATE, 1);

                Date date1 = calendar1.getTime();
                Date date2 = calendar2.getTime();

                Long r1 = date1.getTime();
                Long r2 = date2.getTime();

                loadDataOrderByDates(r1,r2);

            }
        });

     loadDataOrder();
       // loadDataOrderTest();

        return v;
    }

    private void loadDataOrderTest() {
        ArrayList<DetailProduct> detailproducts = new ArrayList<>();
        detailproducts.add(new DetailProduct("cod","LECHE","10","45.0","https://firebasestorage.googleapis.com/v0/b/role-market.appspot.com/o/FotosProducto%2Fcod55_1669521510?alt=media&token=171db8d1-31f8-41b3-8363-a763d630071b"));
        orderList.add(new Order("3u276n51","alfreyh@gmail.com", new Date() ,"386.4", detailproducts));
        adapter = new OrderAdapter(orderList, new OrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Order order, int position) {
                DetailOrderDialog detailOrderDialog = new DetailOrderDialog();
                detailOrderDialog.show(getActivity().getSupportFragmentManager(),"detailOrderDialog");
            }
        });
        recyclerhistory.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void loadDataOrder(){
        pd.setTitle("Cargando Historial");
        pd.show();
        String email = Util.getUserMailPrefs(prefs);
        db.collection("order").whereEqualTo("user",email).get().addOnSuccessListener(queryDocumentSnapshots ->{
            if(!queryDocumentSnapshots.isEmpty()){
                if(orderList != null)
                    orderList.clear();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d: list){
                    Order oders =  d.toObject(Order.class);
                    orderList.add(oders);
                }

                 adapter = new OrderAdapter(orderList, new OrderAdapter.OnItemClickListener() {
                     @Override
                     public void onItemClick(Order order, int position) {
                         DetailOrderDialog detailOrderDialog = new DetailOrderDialog();
                         detailOrderDialog.show(getActivity().getSupportFragmentManager(),"detailOrderDialog");
                     }
                 });
                recyclerhistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                pd.dismiss();
            }else{
                orderList.clear();
                pd.dismiss();
                Toast.makeText(getActivity(),"No hay datos de Compras",Toast.LENGTH_SHORT).show();
            }


        } ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        }) ;


    }

    private void loadDataOrderByDates(Long  d1, Long d2){
        //Toast.makeText(getActivity(),"d1: " + d1,Toast.LENGTH_SHORT).show();
        String email = Util.getUserMailPrefs(prefs);
        Log.d("email","email: " +  email);
       db.collection("order").whereGreaterThanOrEqualTo("longtime",d1).whereLessThan("longtime",d2).get().addOnSuccessListener(queryDocumentSnapshots ->{
            if(!queryDocumentSnapshots.isEmpty()){
                if(orderList != null)
                    orderList.clear();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d: list){
                    Order oders =  d.toObject(Order.class);
                    Log.d("orderemail","orderemail: " +  oders.getUser());
                    if(oders.getUser().equals(email)){
                        orderList.add(oders);
                    }
                }

                adapter = new OrderAdapter(orderList, new OrderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Order order, int position) {
                        DetailOrderDialog detailOrderDialog = new DetailOrderDialog();
                        detailOrderDialog.show(getActivity().getSupportFragmentManager(),"detailOrderDialog");
                    }
                });
                recyclerhistory.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }else{
                orderList.clear();
                recyclerhistory.setAdapter(null);
                adapter.notifyDataSetChanged();
                Toast.makeText(getActivity(),"No hay datos de Compras",Toast.LENGTH_SHORT).show();

            }


        } ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(getActivity(),"Error de Data ",Toast.LENGTH_SHORT).show();
            }
        }) ;





    }
}