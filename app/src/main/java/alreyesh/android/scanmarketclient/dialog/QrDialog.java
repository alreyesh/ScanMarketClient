package alreyesh.android.scanmarketclient.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import net.glxn.qrgen.android.QRCode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.DetailProduct;
import alreyesh.android.scanmarketclient.models.Order;
import alreyesh.android.scanmarketclient.models.Pending;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;
import io.realm.RealmList;

public class QrDialog extends DialogFragment {

    ImageView imgQr;
    Button btnAceptar;
    TextView txtTexto;
    SharedPreferences prefs;
    private RealmList<Cart> carts;
    private Realm realm;
    int purchaseId;
    private Purchase purchase;
    FirebaseAuth mAuth;
    String fechaactual;
    Order order;
    List<DetailProduct> list;
    Bitmap bitmap;
    private Pending pending;

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        String qrfill = Util.getQRCode(prefs);
        Boolean stateqr = Util.getStateQr(prefs);
        String total = Util.getTotalCart(prefs);
        String qr = Util.getQRCode(prefs);
        if(Boolean.FALSE.equals(stateqr) || qr.equals("") ){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            pending = new Pending(qrfill,purchase.getEmailID(),fechaactual,total,purchase.getCarts(), byteArray);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(pending);
            realm.commitTransaction();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("qrstate",true);
            editor.commit();
        }


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.detail_qr,null);
        uis(view);
        mAuth = FirebaseAuth.getInstance();
        purchaseId =  Util.getPurchaseId(prefs);
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();

        realm = Realm.getDefaultInstance();
        purchase = realm.where(Purchase.class).equalTo("id",purchaseId).equalTo("emailID",userEmail).findFirst();
      carts = purchase.getCarts();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();
          fechaactual = sdf.format(today);
        String total = Util.getTotalCart(prefs);
        Boolean stateqr = Util.getStateQr(prefs);
        String qr = Util.getQRCode(prefs);
        pruebaJson(stateqr , userEmail,total, fechaactual);

        btnAceptar.setOnClickListener(v -> {
            String qrfill = Util.getQRCode(prefs);
            Toast.makeText(getContext(),"QR: "+ stateqr,Toast.LENGTH_SHORT).show();
            if(Boolean.FALSE.equals(stateqr)   ){
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Toast.makeText(getContext(),"pruebaQR",Toast.LENGTH_SHORT).show();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                pending = new Pending(qrfill,purchase.getEmailID(),fechaactual,total,purchase.getCarts(), byteArray);
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(pending);
                purchase.getCarts().deleteAllFromRealm();
                realm.commitTransaction();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("qrstate",true);
                editor.commit();
            }
            dismiss();
        });


        builder.setView(view);
        return builder.create();
    }

    private void pruebaJson( Boolean stateqr ,String userEmail,String total, String fechaactual) {

        if(Boolean.FALSE.equals(stateqr)   ){
            String codorder = generatorCodOrder(userEmail,fechaactual);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("qr",codorder);
            editor.commit();
        }

        JSONArray carrito = new JSONArray();
        list = new ArrayList<>();
        for(int i=0; i< carts.size();i++){
            try{
                JSONObject producto = new JSONObject();
                String codigo = carts.get(i).getProductID();
                String nombre = carts.get(i).getProductName();
                String subprice = carts.get(i).getSubPrice();
                String  countproduct = carts.get(i).getCountProduct();
                String link = carts.get(i).getImagenProduct();
                producto.put("cod",codigo);
                producto.put("name",nombre);
                producto.put("link",link);
                producto.put("cantidad",countproduct);
                producto.put("subtotal",subprice);
                carrito.put(producto);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }



        JSONObject ordenJson = new JSONObject();
        try{
            String qrfill = Util.getQRCode(prefs);
            ordenJson.put("codorder",qrfill);
            ordenJson.put("user",userEmail);
            ordenJson.put("date",fechaactual);
            ordenJson.put("total",total);
            ordenJson.put("productos",carrito);
            txtTexto.setText(qrfill);
        }catch (JSONException e){
            e.printStackTrace();
        }

        String jsonStr = ordenJson.toString();

          bitmap = QRCode.from(jsonStr).bitmap();

        imgQr.setImageBitmap(bitmap);




    }

    private String generatorCodOrder(String userEmail, String fechaactual) {
        String email = convertirEmail(userEmail);
        String u1;
        String u2;

        if(email !=null) {
            if (email.length() > 4) {
                u1 = email.substring(0, 2);
                u2 = email.substring(2, 4);
            } else {
                u1 = email.substring(0, 1);
                u2 = email.substring(1, 2);
            }

            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                cal.setTime(sdf.parse(fechaactual));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int day = cal.get(Calendar.DAY_OF_MONTH);
            int mounth = cal.get(Calendar.MONTH);

            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);

            return  hour+u2+day+mounth+u1+minute;


        }
        return  null;

    }
    private String convertirEmail(String user){
        String result= null;
        for(int i=0; i< user.length();i++ ){
            String c = user.substring(i,i);
                if(!c.equals(".")  && !c.equals("_") && !c.equals("-") && !c.equals("@"))
                result += c;
        }


        return result;
    }
    private void uis(View view) {
        imgQr =(ImageView) view.findViewById(R.id.imgQR);
        btnAceptar= (Button) view.findViewById(R.id.btnAceptar);
        txtTexto = (TextView)view.findViewById(R.id.txtCodigo);
        prefs = Util.getSP(getContext());

    }


}
