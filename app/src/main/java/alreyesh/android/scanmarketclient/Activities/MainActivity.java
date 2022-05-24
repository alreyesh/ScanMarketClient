package alreyesh.android.scanmarketclient.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import alreyesh.android.scanmarketclient.Dialog.AddListPurchaseDialog;
import alreyesh.android.scanmarketclient.Fragments.AccountInfoFragment;
import alreyesh.android.scanmarketclient.Fragments.HomeFragment;
import alreyesh.android.scanmarketclient.Fragments.ListPurchaseFragment;
import alreyesh.android.scanmarketclient.Fragments.PurchaseHistoryFragment;
import alreyesh.android.scanmarketclient.R;
import alreyesh.android.scanmarketclient.Utils.Util;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        createRequest();
        drawerLayout= (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView) findViewById(R.id.navview);
        txtUsername = (TextView)drawerLayout.findViewById(R.id.username);
        setFragmentByDefault();
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        String account = Util.getUserAccount(prefs);
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


                //txtUsername.setText(usern);

               // Toast.makeText(MainActivity.this,account,Toast.LENGTH_SHORT).show();

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
                    //Toast.makeText(MainActivity.this, "Soy "+ account,Toast.LENGTH_SHORT).show();
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
                    case R.id.menu_prueba:
                        fragment = new AccountInfoFragment();
                        fragmentTransaction=true;
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

        //google



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

       // getMenuInflater().inflate(R.menu.menu, menu);
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
                AddListPurchaseDialog addListPurchaseDialog = new AddListPurchaseDialog();
                addListPurchaseDialog.show(getSupportFragmentManager(),"addListPurchase");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void bindUI(){

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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



}