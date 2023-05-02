package alreyesh.android.scanmarketclient.splash;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import alreyesh.android.scanmarketclient.activities.LoginActivity;
import alreyesh.android.scanmarketclient.activities.MainActivity;
import alreyesh.android.scanmarketclient.utils.Util;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences  prefs= Util.getSP(getApplication());
        Intent intentLogin = new Intent(this, LoginActivity.class);
        Intent intentMain = new Intent(this, MainActivity.class);


            if(!TextUtils.isEmpty(Util.getUserMailPrefs(prefs)) && !TextUtils.isEmpty(Util.getUserPassPrefs(prefs))){
                startActivity(intentMain);
            }else{
                startActivity(intentLogin);
            }
            finish();



    }
}
