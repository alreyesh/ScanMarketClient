package alreyesh.android.scanmarketclient.dialog;

import static com.maltaisn.icondialog.IconDialog.VISIBILITY_ALWAYS;
import android.app.AlertDialog;
import android.app.Dialog;

import android.content.SharedPreferences;
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
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconView;
import java.util.Scanner;
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
    private Purchase pexist;
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
        uis(view);

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
                pexist = realm.where(Purchase.class).equalTo("name",name).equalTo("emailID",user.getEmail()).findFirst();

                if(user !=null){
                    String userEmail = user.getEmail();
                    if(pexist == null){
                        if(name==null|| name.isEmpty()) {Toast.makeText(getActivity(),"Ingrese nombre de cesta de compra",Toast.LENGTH_SHORT).show();}
                        else{
                            if(color == 0 ){
                                if(!isCreation){
                                    color = purchase.getColor();
                                }else
                                    color= R.color.md_green_100;

                            }
                            float parseLimit = 0;
                            if(!limit.isEmpty()){
                                boolean isLimitFloat = isStringFloat(limit);
                                if(isLimitFloat){
                                    parseLimit = Float.parseFloat(limit);
                                    if(0.0>= parseLimit  ) {
                                        Toast.makeText(getActivity(),"Ingresar  un valor de limite  mayor a 0.0",Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(getActivity(),"Ingresar  un valor de limite, numero entero o decimal y mayor a 0.0",Toast.LENGTH_SHORT).show();
                                }
                            }
                            int icono;
                            if(selectedIcon != null){ icono = selectedIcon.getId();}
                            else{
                                if(!isCreation){
                                    icono =purchase.getIcon();
                                }else{
                                    icono = 471;

                                }

                            }

                            createPurchase(name,parseLimit,color,userEmail,icono);

                        }
                    }else{
                        Toast.makeText(getActivity(), "El nombre de la cesta ya existe", Toast.LENGTH_SHORT).show();

                    }


                }else{
                    Toast.makeText(getActivity(), "Error con la base de datos, inicie sesión nuevamente", Toast.LENGTH_SHORT).show();
                }


            }else{
                Toast.makeText(getActivity(), "Ingrese un nombre para la cesta de compra", Toast.LENGTH_SHORT).show();
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

    private void createPurchase(String name, float limit, int color, String emailID, int icon) {
        Purchase purs =new Purchase(name,limit,color,emailID,icon);
        if(!isCreation){
            purs.setId(purchaseId);
            purs.setCarts(purchase.getCarts());
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(purs);
        realm.commitTransaction();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("newp",false);

        editor.putInt("idp", purs.getId());
        editor.putString("np", purs.getName());
        editor.putInt("cp", purs.getColor());
        editor.putFloat("limitp", purs.getLimit());
        editor.putInt("iconp",purs.getIcon());
        editor.commit();

        dismiss();


    }

    private void uis(View view) {
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
                         color = i;
                        })
                .show();
    }

    private boolean isValidDataForNewPurchase(){
        return  (editTextName.getText().toString().length()>0)?true:false;
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

            return (!is)?false:true;
        }
    }

}
