package alreyesh.android.scanmarketclient.dialog;

import static com.maltaisn.icondialog.IconDialog.VISIBILITY_ALWAYS;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.FragmentManager;


import com.cazaea.sweetalert.SweetAlertDialog;
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;

import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconView;

import java.util.Scanner;

import alreyesh.android.scanmarketclient.fragments.CartFragment;
import alreyesh.android.scanmarketclient.fragments.ListProductFragment;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;

public class AddListPurchaseDialog extends DialogFragment implements IconDialog.Callback  {
    private TextView txtTitle;
    private EditText editTextName;
    private EditText editTextLimit;
    private int color;
     Button btnRegistrar;
    private Button btnColor;
     Button btnCancelar;



    private IconView iconView;


    private Icon selectedIcon;
    private Purchase purchase;
    private   Purchase purs;
    private Realm realm;
    private int purchaseId;
    private boolean isCreation;
     SharedPreferences prefs;
     FirebaseAuth mAuth;

     FragmentManager fm;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View   view = inflater.inflate(R.layout.add_list_purchase_dialog_layout, null);
        UI(view);
        Context context = getContext();
        // Comprobar si va a ser una acción para editar o para creación
        Boolean p = Util.getCreateOrEditPurchase(prefs);
        if(Boolean.TRUE.equals(p)) {
            purchaseId = Util.getSelectPurchase(prefs);
            isCreation = false;
        } else
            isCreation = true;

        setDialogTitle();
        btnRegistrar.setText("Registrar");
        if (!isCreation) {

            String email = Util.getUserMailPrefs(prefs);
            purchase = getPurchaseByIdAndUser(purchaseId,email);
            btnRegistrar.setText("Actualizar");
            bindDataToFields();
        }

        btnRegistrar.setOnClickListener(v -> {
            if(isValidDataForNewPurchase()){
                String name =  editTextName.getText().toString();
                String limit = editTextLimit.getText().toString();


                 FirebaseUser user = mAuth.getCurrentUser();

            if(user !=null){
                String userEmail = user.getEmail();
                        if(name==null|| name.isEmpty()) {Toast.makeText(getActivity(),"Ingrese nombre de cesta de compra",Toast.LENGTH_SHORT).show();}
                        else if(limit == null || limit.isEmpty()){Toast.makeText(getActivity(),"Ingrese limite de compra",Toast.LENGTH_SHORT).show();}
                        else{
                           if(color == 0 ){
                               if(!isCreation){
                                   color = purchase.getColor();
                               }else
                               color= R.color.md_green_100;

                           }
                           boolean isLimitFloat = isStringFloat(limit);
                           if(isLimitFloat){
                               float parseLimit = Float.parseFloat(limit);
                               if(parseLimit >0.0) {

                                    int icono;
                                   if(selectedIcon != null){ icono = selectedIcon.getId();}
                                   else{
                                       if(!isCreation){
                                           icono =purchase.getIcon();
                                       }else{
                                           icono = 471;

                                       }

                                   }


                                   purs =new Purchase(name,parseLimit,color,userEmail,icono);
                                   if(!isCreation){
                                       purs.setId(purchaseId);
                                       purs.setCarts(purchase.getCarts());
                                   }
                                   realm.beginTransaction();
                                   realm.copyToRealmOrUpdate(purs);
                                   realm.commitTransaction();


                                   Boolean decision = Util.getDecisionPurchase(prefs);
                                   if(Boolean.TRUE.equals(decision)){
                                       SharedPreferences.Editor editor = prefs.edit();
                                       editor.putBoolean("newp",false);
                                       editor.commit();
                                       new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                               .setTitleText("Registrar")
                                               .setContentText("Desea seleccionar el listado creado")
                                               .setConfirmText("Si")
                                               .setCancelText("No")
                                               .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                   @Override
                                                   public void onClick(SweetAlertDialog sDialog) {
                                                       sDialog.dismissWithAnimation();
                                                       editor.putInt("idp", purs.getId());
                                                       editor.putString("np", purs.getName());
                                                       editor.putInt("cp", purs.getColor());
                                                       editor.putFloat("limitp", purs.getLimit());
                                                       editor.putInt("iconp",purs.getIcon());
                                                       editor.commit();


                                                       getActivity().invalidateOptionsMenu();

                                                       dismiss();

                                                   }
                                               })
                                               .showCancelButton(true)
                                               .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                   @Override
                                                   public void onClick(SweetAlertDialog sDialog) {
                                                       sDialog.cancel();

                                                   }
                                               })
                                               .show();





                                   }else{
                                       dismiss();
                                   }

                               }else{
                                   Toast.makeText(getActivity(),"Ingresar  un valor de limite  mayor a 0.0",Toast.LENGTH_SHORT).show();

                               }


                           }else{
                               Toast.makeText(getActivity(),"Ingresar  un valor de limite, numero decimal y mayor a 0.0",Toast.LENGTH_SHORT).show();
                           }



                        }
                }
            }else{
                Toast.makeText(getActivity(), "The data is not valid, please check the fields again", Toast.LENGTH_SHORT).show();
            }

            getActivity().invalidateOptionsMenu();
        });
        btnColor.setOnClickListener(v -> colorPicker());

      iconView.setOnClickListener(v -> {
         IconDialog iconDialog = new IconDialog();

         iconDialog.setTargetFragment( AddListPurchaseDialog.this,0);

         iconDialog.setTitle( VISIBILITY_ALWAYS,"Seleccione un icono");
         iconDialog.show(getActivity().getSupportFragmentManager(),"icon_dialog");
      });

        btnCancelar.setOnClickListener(v -> dismiss());

        builder.setView(view);


        return builder.create();
    }
    private void UI(View view) {
        txtTitle = view.findViewById(R.id.textViewListPurchase);
        editTextName = view.findViewById(R.id.editNameList);
        editTextLimit = view.findViewById(R.id.editLimitList);
        btnRegistrar = view.findViewById(R.id.btnRegistrarEditPurchase);
        btnCancelar = view.findViewById(R.id.btnCancel);
        btnColor = view.findViewById(R.id.color_select_button);
        iconView = view.findViewById(R.id.icon_select_button);
        prefs = Util.getSP(getActivity());
        mAuth = FirebaseAuth.getInstance();

        realm = Realm.getDefaultInstance();
    }
    private void bindDataToFields(){
        editTextName.setText(purchase.getName());
        editTextLimit.setText(String.valueOf(purchase.getLimit()));
        btnColor.setBackgroundColor(purchase.getColor());
        iconView.setIcon(purchase.getIcon());
    }
    private void setDialogTitle() {
        String title = "Editar Cesta de Compra";
        if (isCreation) title = "Crear Nueva Cesta de Compra";
        txtTitle.setText(title);
    }
    public void colorPicker(){
        new MaterialColorPickerDialog.Builder(getActivity())

                .setTitle("Elegir Color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._300)

                .setColorRes(getResources().getIntArray(R.array.demo_colors))
                .setColorListener((i, s) -> {
                    btnColor.setBackgroundColor(i);
                    Toast.makeText(getActivity(),"color: "+i, Toast.LENGTH_SHORT).show();
                        color = i;
                        })
                .show();
    }

    private boolean isValidDataForNewPurchase(){
        if(editTextName.getText().toString().length()>0 && editTextLimit.getText().toString().length()>0){
            return true;
        }
            return false;

    }
    //Realm



    private Purchase getPurchaseByIdAndUser(int cityId, String userEmail){

        return realm.where(Purchase.class).equalTo("id",cityId).and().equalTo("emailID",userEmail).findFirst();
         }


    @Override
    public void onIconDialogIconsSelected(@NonNull Icon[] icons) {

        Icon icon = icons[0];
      iconView.setIcon(icon);
        selectedIcon = icon;
     }
    public static boolean isStringFloat(String stringToCheck){
        try(  Scanner sc = new Scanner(stringToCheck.trim())){
            boolean is =  sc.hasNextFloat();
            if(!is) return false;
            return true;
        }
    }

}
