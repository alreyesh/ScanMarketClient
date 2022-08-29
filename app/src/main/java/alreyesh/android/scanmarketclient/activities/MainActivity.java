package alreyesh.android.scanmarketclient.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rowland.cartcounter.view.CartCounterActionView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import alreyesh.android.scanmarketclient.camara.CamaraActivity;
import alreyesh.android.scanmarketclient.dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.dialog.NotificationDialog;
import alreyesh.android.scanmarketclient.fragments.AccountInfoFragment;
import alreyesh.android.scanmarketclient.fragments.CartFragment;
import alreyesh.android.scanmarketclient.fragments.HistorialTabFragment;
import alreyesh.android.scanmarketclient.fragments.HomeFragment;
import alreyesh.android.scanmarketclient.fragments.ListProductFragment;
import alreyesh.android.scanmarketclient.fragments.ListPurchaseFragment;
import alreyesh.android.scanmarketclient.fragments.RecommentFragment;
import alreyesh.android.scanmarketclient.fragments.SettingsFragment;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import alreyesh.android.scanmarketclient.notifications.NotificacionPush;
import alreyesh.android.scanmarketclient.notifications.NotificationHandler;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.services.Notifyservice;
import alreyesh.android.scanmarketclient.utils.Util;
import io.realm.Realm;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity {
     SharedPreferences prefs;
     FirebaseAuth mAuth;
    private FirebaseFirestore db;
     DrawerLayout drawerLayout;
     NavigationView navigationView;
     ImageView imgUsername;
     TextView txtUsername;
      GoogleSignInAccount signInAccount;
    Boolean turnNoti;
    //google
    private GoogleSignInClient mGoogleSignInClient;
//Cart
    Purchase purchase;
     int purchaseId;
     RealmList<Cart> carts;
       CartCounterActionView actionviewCart;
     MenuItem menuId;
     MenuItem totalId;
    View v;
    Toolbar totalToolId;
     int cantCart;
     Realm realm;
//notificacion
     boolean isHighImportance = false;
     NotificationHandler notificacionHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mAuth = FirebaseAuth.getInstance();

        v = getWindow().getDecorView().findViewById(android.R.id.content);
        db = FirebaseFirestore.getInstance();

        prefs = Util.getSP(getApplication());
        turnNoti = Util.getNotiTurn(prefs);
        setToolbar();
        createRequest();
          ui();
        setFragmentByDefault();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user !=null){
            account(user);
           // checkRegister(user);
        }


        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                txtUsername = (TextView)drawerView.findViewById(R.id.username);
                imgUsername=(ImageView)drawerView.findViewById(R.id.usernameImg);
                 signInAccount = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                Uri gimg;
                String account = null;
                String name= null;
                try{
                    account = Util.getUserAccount(prefs);
                    if( account.equals("google")  && signInAccount !=null){

                        gimg=  signInAccount.getPhotoUrl();
                        name = signInAccount.getDisplayName();
                        txtUsername.setText(name);
                        imgUsername.setImageURI(null);
                        Picasso.get()
                                .load(gimg)
                                .placeholder(R.drawable.rolemarket)
                                .fit()
                                .centerCrop().into(imgUsername);
                    }else{
                        Drawable myImage = getResources().getDrawable(R.drawable.rolemarket,null);
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user !=null){
                            String usern = user.getEmail();
                            imgUsername.setImageDrawable(myImage);
                            txtUsername.setText(usern);
                        }
                    }


                }catch (NullPointerException e){

                }

            }
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //Empty
            }
            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                //Empty
            }
            @Override
            public void onDrawerStateChanged(int newState) {
                //Empty
            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            boolean fragmentTransaction = false;
            Fragment fragment = null;
            switch (item.getItemId()){
                case R.id.menu_home:
                    fragment = new HomeFragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_list_purchase:
                    fragment = new ListPurchaseFragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_products:
                    fragment = new ListProductFragment();
                    fragmentTransaction = true;
                    break;

                case R.id.menu_shop:
                    fragment = new HistorialTabFragment();
                    fragmentTransaction = true;
                    break;
                case R.id.menu_logout:
                    Util.removeSharedPreferences(prefs);
                    logOut();
                    break;
                case R.id.my_info:
                    fragment = new AccountInfoFragment();
                    fragmentTransaction = true;
                    break;
                case R.id.my_settings:
                    fragment = new SettingsFragment();
                    fragmentTransaction = true;
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
            if(fragmentTransaction){
                changeFragment(fragment,item);
                drawerLayout.closeDrawers();

            }
            return true;
        });
        Bundle bundle =  getIntent().getExtras();
        if(bundle !=null){
            String register  = getIntent().getExtras().getString("register","off");
            if(register.equals("on")){
                Intent intent = new Intent(MainActivity.this, AccountInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }


            String resultado= getIntent().getExtras().getString("cameras");
            if(resultado!= null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecommentFragment()).commit();

            }
            String noti = getIntent().getExtras().getString("notfy","off");
            if(noti.equals("on")){

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("turnnoti",true);
                editor.commit();


                NotificationDialog notificacion = new NotificationDialog();
                FragmentManager fragmentManager =  getSupportFragmentManager();
                notificacion.show(fragmentManager, "notificacionview");


            }


            boolean reloadFragmentFromNotification = getIntent().getExtras().getBoolean("showCartView",false);
            if (reloadFragmentFromNotification){
                Fragment fragment = new CartFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame,fragment)
                        .commit();
            }

        }

        if(turnNoti !=null) {
            if (Boolean.FALSE.equals(turnNoti)) {
                NotificationDialog notificacion = new NotificationDialog();
                FragmentManager fragmentManager = getSupportFragmentManager();
                notificacion.show(fragmentManager, "notificacionview");
            }
        }

        invalidateOptionsMenu();

        Boolean turn = Util.getTurnNotify(prefs);
        Intent intent = new Intent(this, Notifyservice.class);

   if(Boolean.TRUE.equals(turn)) {
   /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

           startForegroundService(intent);
       } else {
           startService(intent);
       }
*/
       NotificacionPush noti = new NotificacionPush();
       noti.onNotiPause(getApplicationContext());

        }else{
      // stopService(intent);


        }







    }

    private void checkRegister(FirebaseUser user) {

        db.collection("usuarios").whereEqualTo("uid", user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().isEmpty()){

                            }
                        }
                    }
                });


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onRestart() {
        super.onRestart();



    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menulistpurchase, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.add_list_purchase).setVisible(false);
        menu.findItem(R.id.shopping_total).setVisible(false);
        menu.findItem(R.id.action_camera).setVisible(false);
        colorbar(menu);
        drawableColorselected();
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_logout:
                logOut();
                return true;
            case R.id.menu_forget_logout:
                Util.removeSharedPreferences(prefs);
                logOut();
                return true;
            case R.id.add_list_purchase:
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("cep",false);
                editor.commit();
                AddListPurchaseDialog addListPurchaseDialog = new AddListPurchaseDialog();
                addListPurchaseDialog.show(getSupportFragmentManager(),"addListPurchase");
                return true;
            case R.id.action_addcart:
                CartFragment cartFragment = new CartFragment();
                 getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_frame,cartFragment)
                        .commit();
                return true;
            case R.id.action_camera:
                Intent intent = new Intent(this, CamaraActivity.class);
                startActivity(intent);
                return true;
            case R.id.my_info:
                AccountInfoFragment accountInfoFragment = new AccountInfoFragment();
                getSupportFragmentManager()
                        .beginTransaction().replace(R.id.content_frame,accountInfoFragment)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void account(FirebaseUser user){



        String email = user.getEmail();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email",email);
        editor.commit();
    }

   private void ui(){
       notificacionHandler = new NotificationHandler(this);

       realm = Realm.getDefaultInstance();
       drawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);
       navigationView=(NavigationView) findViewById(R.id.navview);
       txtUsername = (TextView)drawerLayout.findViewById(R.id.username);
   }
 private void colorbar( Menu menu){
     if (Util.getPurchaseId(prefs) != null){
         purchaseId = Util.getPurchaseId(prefs);
         FirebaseUser user = mAuth.getCurrentUser();
         if(user !=null) {
             String userEmail = user.getEmail();
             purchase = realm.where(Purchase.class).equalTo("id", purchaseId).equalTo("emailID", userEmail).findFirst();
             menuId = menu.findItem(R.id.action_addcart);
             totalId = menu.findItem(R.id.shopping_total);
             menu.findItem(R.id.action_camera).setVisible(true);
             actionviewCart = (CartCounterActionView) menuId.getActionView();
             if (purchase != null) {
                 cantCart = purchase.getCarts().size();

                 actionviewCart.setItemData(menu, menuId);
                 if (cantCart > 0) {
                     menu.findItem(R.id.action_addcart).setVisible(true);
                     menu.findItem(R.id.shopping_total).setVisible(true);
                     cartexist();
                 } else {
                     menu.findItem(R.id.action_addcart).setVisible(false);
                     actionviewCart.setCount(0);
                 }
             } else {
                 menu.findItem(R.id.action_addcart).setVisible(false);
                 actionviewCart.setCount(0);
             }
         }
     }
 }
    private void cartexist(){
        String namepurchase = Util.getPurchaseName(prefs);
        actionviewCart.setCount(cantCart);
        carts = purchase.getCarts();
        List<Cart> list = new ArrayList<>();
        list.addAll(carts);
        float totalCart = 0;
        for(int i =0; i<list.size();i++){

            String subtotal =  list.get(i).getSubPrice();
            float parsesubtotal = Float.parseFloat(subtotal);
            totalCart+= parsesubtotal;
        }
        SpannableString s = new SpannableString("S/. "+totalCart);

        float limit = Util.getPurchaseLimit(prefs);

        View viewtexttotal = findViewById(R.id.shopping_total);
       colorCart(viewtexttotal,limit,totalCart);

        totalId.setTitle( s );

        boolean notify = Util.getStartNotification(prefs);
        if(notify){
            float  porcent = totalCart /limit;
            if ( viewtexttotal instanceof TextView){
                if(porcent >=0.90 && porcent <1.00){
                    isHighImportance = false;
                    sendNotification(namepurchase,totalCart,limit,isHighImportance);
                    ((TextView) viewtexttotal).setTextColor( Color.WHITE ); // Make text colour blue
                    ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
                }else if(porcent>1.00){
                    isHighImportance = true;
                    sendNotification(namepurchase,totalCart,limit,isHighImportance);
                    ((TextView) viewtexttotal).setTextColor( Color.RED ); // Make text colour blue
                    ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Increase font size
                }else{
                    ((TextView) viewtexttotal).setTextColor( Color.WHITE ); // Make text colour blue
                    ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
                }
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("startnotify",false);
            editor.putString("total",String.valueOf(totalCart));
            editor.commit();
        }

    }
    private void colorCart( View viewtexttotal, float limit,float totalCart){
        if(totalCart>=limit){

            Toast.makeText(this, "Se Excedio ", Toast.LENGTH_SHORT).show();
            if ( viewtexttotal instanceof TextView) {
                ((TextView) viewtexttotal).setTextColor( Color.RED ); // Make text colour blue
                ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Increase font size
            }

        }else{

            if ( viewtexttotal instanceof TextView) {
                ((TextView) viewtexttotal).setTextColor( Color.WHITE ); // Make text colour blue
                ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
            }

        }
    }
    private void createRequest(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id2))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }
    private void logOut(){
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(this,LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String account = Util.getUserAccount(prefs);

        if(account.equals("google") ){

            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {

            });
        }


        startActivity(intent);



    }
    private void setToolbar(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_sand);
        getSupportActionBar().setHomeActionContentDescription(R.string.burger_descripcion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawableColorselected();
    }
    private void setFragmentByDefault(){
        changeFragment(new HomeFragment(),navigationView.getMenu().getItem(0));
    }
    private void changeFragment(Fragment fragment, MenuItem item){
        getSupportFragmentManager()
                .beginTransaction().replace(R.id.content_frame,fragment)
                .commit();
        item.setChecked(true);
        getSupportActionBar().setTitle(item.getTitle());

    }
    private void sendNotification(String name ,float total ,float limit,boolean isHighImportance){
        Notification.Builder nb  = notificacionHandler.createNotification(name,"Con S/."+total +" excedio el limite de compra de S/."+limit,isHighImportance,"alarma");

         notificacionHandler.getManager().notify(1,nb.build());


    }
    private void drawableColorselected(){
        try{
            Integer colorparse = Util.getPurchaseColor(prefs);
            if(colorparse !=0){
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
            }else{
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary,null)));
            }
        }catch(NullPointerException e){
            System.out.print("NullPointerException caught");
        }




    }
}