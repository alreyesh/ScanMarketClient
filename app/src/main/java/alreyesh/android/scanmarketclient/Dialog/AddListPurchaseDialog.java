package alreyesh.android.scanmarketclient.Dialog;

import static com.maltaisn.icondialog.IconDialog.VISIBILITY_ALWAYS;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog;
import com.github.dhaval2404.colorpicker.listener.ColorListener;
import com.github.dhaval2404.colorpicker.model.ColorShape;
import com.github.dhaval2404.colorpicker.model.ColorSwatch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.maltaisn.icondialog.Icon;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconView;

import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;

public class AddListPurchaseDialog extends DialogFragment implements IconDialog.Callback  {
    private TextView txtTitle;
    private EditText editTextName,editTextLimit;
    private int color;
    private Button btnRegistrar,btnColor,btnCancelar;



    private IconView iconView;


    private Icon selectedIcon;
    private Purchase purchase;
    private Realm realm;
    private int purchaseId;
    private boolean isCreation;
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;
    private static final String ICON_DIALOG_TAG = "icon-dialog";
    private FragmentManager fm;



    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View   view = inflater.inflate(R.layout.add_list_purchase_dialog_layout, null);

        txtTitle = view.findViewById(R.id.textViewListPurchase);
        editTextName = view.findViewById(R.id.editNameList);
        editTextLimit = view.findViewById(R.id.editLimitList);
        btnRegistrar = view.findViewById(R.id.btnRegistrarEdit);
        btnCancelar = view.findViewById(R.id.btnCancel);
        btnColor = view.findViewById(R.id.color_select_button);
        iconView = view.findViewById(R.id.icon_select_button);
        prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        Toast.makeText(getActivity(), "Se actualizo",
                Toast.LENGTH_SHORT).show();
        realm = Realm.getDefaultInstance();
        // Comprobar si va a ser una acción para editar o para creación
     if (getActivity().getIntent().getExtras() != null) {
            purchaseId = getActivity().getIntent().getExtras().getInt("id");
            isCreation = false;
        } else {
            isCreation = true;
        }
        setDialogTitle();
        if (!isCreation) {

            String email = Util.getUserMailPrefs(prefs);
            purchase = getPurchaseByIdAndUser(purchaseId,email);
            bindDataToFields();
        }

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidDataForNewPurchase()){
                    String name =  editTextName.getText().toString();
                    String limit = editTextLimit.getText().toString();
                    float parseLimit = Float.parseFloat(limit);
                    String email = Util.getUserMailPrefs(prefs);
                    Toast.makeText(getActivity(),email, Toast.LENGTH_SHORT).show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userEmail = user.getEmail();

                    if(name==null|| name.isEmpty()) {Toast.makeText(getActivity(),"Ingrese nombre de lista de compra",Toast.LENGTH_SHORT).show();}
                    else if(limit == null || limit.isEmpty()){Toast.makeText(getActivity(),"Ingrese limite de compra",Toast.LENGTH_SHORT).show();}
                    else{
                       if(color == 0 ){ color= R.color.md_green_100;}
                        Purchase purchase = new Purchase(name,parseLimit,color,userEmail,selectedIcon.getId());
                      if(!isCreation) purchase.setId(purchaseId);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(purchase);
                        realm.commitTransaction();
                        dismiss();
                    }
                }else{
                    Toast.makeText(getActivity(), "The data is not valid, please check the fields again", Toast.LENGTH_SHORT).show();
                }


            }
        });
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPicker();
            }
        });

      iconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               IconDialog iconDialog = new IconDialog();
               iconDialog.setTargetFragment( AddListPurchaseDialog.this,0);

               iconDialog.setTitle( VISIBILITY_ALWAYS,"Seleccione un icono");
               iconDialog.show(getActivity().getSupportFragmentManager(),"icon_dialog");
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);


        return builder.create();
    }
    /*
  @OnClick(R.id.icon_select_button)
    public void onIconButtonPressed() {
        IconDialog iconDialog = new IconDialog();
        iconDialog.setTargetFragment(this, 0);
        iconDialog.setTitle(VISIBILITY_ALWAYS, "Seleccionar Icono");
        iconDialog.show(getActivity().getSupportFragmentManager(), "icon_dialog");

    }
*/

    private void bindDataToFields(){
        editTextName.setText(purchase.getName());
        editTextLimit.setText((int) purchase.getLimit());
        btnColor.setBackgroundColor(purchase.getColor());

    }
    private void setDialogTitle() {
        String title = "Editar Listado de Compra";
        if (isCreation) title = "Crear Nuevo Listado de Compra";
        txtTitle.setText(title);
    }
    public void colorPicker(){
        new MaterialColorPickerDialog.Builder(getActivity())
                .setTitle("Elegir Color")
                .setColorShape(ColorShape.SQAURE)
                .setColorSwatch(ColorSwatch._300)

                .setColorRes(getResources().getIntArray(R.array.demo_colors))
                .setColorListener(new ColorListener() {
                    @Override
                    public void onColorSelected(int i, String s) {
                        btnColor.setBackgroundColor(i);

                            color = i;
                     //   Toast.makeText(getActivity(), color+"", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(getActivity(), selectedIcon.getId()+"", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }
    private void ReloadFragment(){
        Fragment frg = null;
        frg = getActivity(). getSupportFragmentManager().findFragmentByTag("addListPurchase");
        final FragmentTransaction ft = getActivity(). getSupportFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();

    }
    private boolean isValidDataForNewPurchase(){
        if(editTextName.getText().toString().length()>0 && editTextLimit.getText().toString().length()>0){
            return true;
        }else{
            return false;
        }
    }
    //Realm



    private Purchase getPurchaseByIdAndUser(int cityId, String userEmail){
       /* RealmQuery query = realm.where(Purchase.class);
        query.beginGroup();
        query.equalTo("id",cityId);
        query.equalTo("userEmail",userEmail);
        query.endGroup();
        return query.;
        */
        return realm.where(Purchase.class).equalTo("id",cityId).and().equalTo("emailID",userEmail).findFirst();
         }


    @Override
    public void onIconDialogIconsSelected(@NonNull Icon[] icons) {

        Icon icon = icons[0];
      iconView.setIcon(icon);
        selectedIcon = icon;
        Toast.makeText(getActivity(), selectedIcon+"", Toast.LENGTH_SHORT).show();
    }
}
