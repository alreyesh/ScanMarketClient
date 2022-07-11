package alreyesh.android.scanmarketclient.Activities;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
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
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.rowland.cartcounter.view.CartCounterActionView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import alreyesh.android.scanmarketclient.Camara.CamaraActivity;
import alreyesh.android.scanmarketclient.Dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.Fragments.AccountInfoFragment;
import alreyesh.android.scanmarketclient.Fragments.CartFragment;
import alreyesh.android.scanmarketclient.Fragments.HomeFragment;
import alreyesh.android.scanmarketclient.Fragments.ListProductFragment;
import alreyesh.android.scanmarketclient.Fragments.ListPurchaseFragment;
import alreyesh.android.scanmarketclient.Fragments.PurchaseHistoryFragment;
import alreyesh.android.scanmarketclient.Fragments.RecommentFragment;
import alreyesh.android.scanmarketclient.Models.Cart;
import alreyesh.android.scanmarketclient.Models.Purchase;
import alreyesh.android.scanmarketclient.Notifications.NotificationHandler;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;
import io.realm.Realm;
import io.realm.RealmList;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imgUsername;
    private TextView txtUsername;
    private  GoogleSignInAccount signInAccount;

    //google
    private GoogleSignInClient mGoogleSignInClient;
//Cart
private Purchase purchase;
    private int purchaseId;
    private RealmList<Cart> carts;
    private   CartCounterActionView actionviewCart;
    private MenuItem menuId,totalId;
    private Toolbar totalToolId;
    private int cantCart;
    private Realm realm;
//notificacion
    private boolean isHighImportance = false;
    private NotificationHandler notificacionHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        setToolbar();
        createRequest();
        notificacionHandler = new NotificationHandler(this);
        realm = Realm.getDefaultInstance();
        drawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView) findViewById(R.id.navview);
        txtUsername = (TextView)drawerLayout.findViewById(R.id.username);
        setFragmentByDefault();

        mAuth = FirebaseAuth.getInstance();
        String account = Util.getUserAccount(prefs);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email",email);
        editor.commit();
        //Navegacion
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                txtUsername = (TextView)drawerView.findViewById(R.id.username);
                imgUsername=(ImageView)drawerView.findViewById(R.id.usernameImg);
                //Firebase Account




                //Google Account
                 signInAccount = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                Uri gimg;
                String emailG;
                String name;



                if( account == "google" && signInAccount !=null){
                  //  Toast.makeText(MainActivity.this, "Soy "+ account,Toast.LENGTH_SHORT).show();
                    gimg=  signInAccount.getPhotoUrl();
                    // Picasso.get().invalidate(gimg);
                    emailG = signInAccount.getEmail();
                    name = signInAccount.getDisplayName();
                    txtUsername.setText(name);
                   imgUsername.setImageURI(null);
                    Picasso.get()
                            .load(gimg)
                            .placeholder(R.drawable.rolemarket)
                            .fit()
                            .centerCrop().into(imgUsername);
                   // imgUsername.setImageURI(gimg);

                }else{
                      Drawable myImage = getResources().getDrawable(R.drawable.rolemarket);
                    FirebaseUser user = mAuth.getCurrentUser();
                    if(user !=null){
                        String usern = user.getEmail();
                        imgUsername.setImageDrawable(myImage);
                        txtUsername.setText(usern);
                    }

                }

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {




            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
                        fragment = new PurchaseHistoryFragment();
                        fragmentTransaction = true;
                        break;
                    case R.id.menu_logout:
                        Util.removeSharedPreferences(prefs);
                        logOut();
                        break;
                }
                if(fragmentTransaction){
                    changeFragment(fragment,item);
                    drawerLayout.closeDrawers();

                }
                return true;
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String resultado= getIntent().getExtras().getString("cameras");
            if(resultado!= null) {
                String resutado = getIntent().getExtras().getString("cameras");


                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new RecommentFragment()).commit();

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














    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
      getMenuInflater().inflate(R.menu.menulistpurchase, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_delete_all).setVisible(false);
        menu.findItem(R.id.add_list_purchase).setVisible(false);
        menu.findItem(R.id.shopping_total).setVisible(false);
        if (Util.getPurchaseId(prefs) != null){
            purchaseId = Util.getPurchaseId(prefs);

            String namepurchase = Util.getPurchaseName(prefs);
            purchase = realm.where(Purchase.class).equalTo("id", purchaseId).findFirst();
            menuId=  menu.findItem(R.id.action_addcart);
            totalId = menu.findItem(R.id.shopping_total);

            actionviewCart = (CartCounterActionView)menuId.getActionView();
            if(purchase !=null){
                cantCart = purchase.getCarts().size();

                   actionviewCart.setItemData(menu,menuId);
                if(cantCart>0) {
                    menu.findItem(R.id.action_addcart).setVisible(true);
                    menu.findItem(R.id.shopping_total).setVisible(true);
                    actionviewCart.setCount(cantCart);
                    carts = purchase.getCarts();
                    List<Cart> list = new ArrayList<Cart>();
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
                    if(totalCart>=limit){
                          s.setSpan(new ForegroundColorSpan(Color.RED), 0, s.length(), 0);
                        Toast.makeText(this, "Se Excedio ", Toast.LENGTH_SHORT).show();
                        if (viewtexttotal != null && viewtexttotal instanceof TextView) {
                            ((TextView) viewtexttotal).setTextColor( Color.RED ); // Make text colour blue
                            ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Increase font size
                        }
                        totalId.setTitle(s);
                    }else{
                        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
                         if (viewtexttotal != null && viewtexttotal instanceof TextView) {
                            ((TextView) viewtexttotal).setTextColor( Color.WHITE ); // Make text colour blue
                            ((TextView) viewtexttotal).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // Increase font size
                        }
                        totalId.setTitle(s);
                    }



                        boolean notify = Util.getStartNotification(prefs);
                        if(notify == true){
                            float  porcent = totalCart /limit;
                            if(porcent >=0.90 && porcent <1.00){
                                isHighImportance = false;
                                sendNotification(namepurchase,totalCart,limit,isHighImportance);
                            }else if(porcent>1.00){
                                isHighImportance = true;
                                sendNotification(namepurchase,totalCart,limit,isHighImportance);
                            }
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("startnotify",false);
                            editor.commit();
                        }

                   }
                else   {
                    menu.findItem(R.id.action_addcart).setVisible(false);
                    actionviewCart.setCount(0);
                }
            }else{
                menu.findItem(R.id.action_addcart).setVisible(false);
                actionviewCart.setCount(0);
            }

        }
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
            default:
                return super.onOptionsItemSelected(item);
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

        if(account=="google"){

            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
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
        Notification.Builder nb;
         if(isHighImportance == true){
             nb   = notificacionHandler.createNotification(name,"Con S/."+total +" excedio el limite de compra de S/."+limit,isHighImportance);
         }else{
             nb   = notificacionHandler.createNotification(name,"Con S/."+total +" excedio el limite de compra de S/."+limit,isHighImportance);

         }
            notificacionHandler.getManager().notify(1,nb.build());


    }
    private void drawableColorselected(){
        Integer colorparse = Util.getPurchaseColor(prefs);
        if(colorparse !=0){
              getSupportActionBar().setBackgroundDrawable(new ColorDrawable(colorparse));
        }else{
             getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary,null)));
        }
    }
}