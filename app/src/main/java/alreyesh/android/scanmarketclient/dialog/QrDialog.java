package alreyesh.android.scanmarketclient.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.gson.Gson;

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
import java.util.Locale;
import java.util.stream.Stream;

import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.DetailProduct;
import alreyesh.android.scanmarketclient.models.Order;
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
    public int purchaseId;
    private Purchase purchase;
    FirebaseAuth mAuth;
    String fechaactual;
    Order order;
    List<DetailProduct> list;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.detail_qr,null);
        UI(view);
        mAuth = FirebaseAuth.getInstance();
        purchaseId =  Util.getPurchaseId(prefs);
        FirebaseUser user = mAuth.getCurrentUser();
        String userEmail = user.getEmail();

        realm = Realm.getDefaultInstance();
        purchase = realm.where(Purchase.class).equalTo("id",purchaseId).equalTo("emailID",userEmail).findFirst();
      carts = purchase.getCarts();
       /*   list = new ArrayList<>();
       list.addAll(carts);

       */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date today = new Date();
          fechaactual = sdf.format(today);
        String total = Util.getTotalCart(prefs);

        pruebaJson(userEmail,total, fechaactual);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        builder.setView(view);
        return builder.create();
    }

    private void pruebaJson(String userEmail,String total, String fechaactual) {
        String codorder = generatorCodOrder(userEmail,fechaactual);
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
            ordenJson.put("codorder",codorder);
            ordenJson.put("user",userEmail);
            ordenJson.put("date",fechaactual);
            ordenJson.put("total",total);
            ordenJson.put("productos",carrito);

        }catch (JSONException e){
            e.printStackTrace();
        }

        String jsonStr = ordenJson.toString();
      //txtTexto.setText(jsonStr);
        Bitmap bitmap = QRCode.from(jsonStr).bitmap();

        imgQr.setImageBitmap(bitmap);

       // System.out.println("jsonString: "+jsonStr);



    }

    private String generatorCodOrder(String userEmail, String fechaactual) {
        String email = convertirEmail(userEmail);
        String u1;
        String u2;
        if(email.length()>4){
            u1 =  email.substring(0,2);
            u2 = email.substring(2,4);
        }else{
            u1 =  email.substring(0,1);
            u2 =  email.substring(1,2);
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
        int year = cal.get(Calendar.YEAR);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        String codigo = hour+u2+day+mounth+u1+minute;
        return codigo;
    }
    private String convertirEmail(String user){
        String result= null;
        for(int i=0; i< user.length();i++ ){
            String c = user.substring(i,i);
                if(c != "." && c!="_"&& c!="-")
                result += c;
        }


        return result;
    }
    private void UI(View view) {
        imgQr =(ImageView) view.findViewById(R.id.imgQR);
        btnAceptar= (Button) view.findViewById(R.id.btnAceptar);
        txtTexto = (TextView)view.findViewById(R.id.txtTexto);
        prefs = Util.getSP(getContext());

    }

    private void GenerateQR(){
        Gson gson = new Gson();
        Float limit =Util.getPurchaseLimit(prefs);
        String totalidad = Util.getTotalCart(prefs);
      //  Toast.makeText(getActivity(),"total:"+ limit +"y "+ totalidad, Toast.LENGTH_SHORT).show();
        String json = gson.toJson(order);

     /*   ByteArrayOutputStream stream = QRCode.from(json).withSize(150,150).stream();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

      */
      //  Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);


       // imgQr.setImageBitmap(bitmap);
    }
}
