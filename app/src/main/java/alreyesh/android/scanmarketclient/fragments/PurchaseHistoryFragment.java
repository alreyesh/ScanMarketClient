package alreyesh.android.scanmarketclient.fragments;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.adapters.OrderAdapter;
import alreyesh.android.scanmarketclient.dialog.DetailOrderDialog;
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
        materialDateBuilder.setTitleText("SELECCIONAR FECHA");
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
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(selection.second);
                cal.add(Calendar.DATE,1);

                Date f1 = new Date(TimeUnit.SECONDS.toMillis(selection.first));
                Date f2 = new Date(TimeUnit.SECONDS.toMillis(selection.second));

                String fecha1 = sdf.format(f1);
                String fecha2 = sdf.format(f2);
                Toast.makeText(getActivity(),"F: " + sdf.format(cal.getTime()),Toast.LENGTH_SHORT).show();
            }
        });

        loadDataOrder();


        return v;
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
            }


        } ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
            }
        }) ;


    }


}