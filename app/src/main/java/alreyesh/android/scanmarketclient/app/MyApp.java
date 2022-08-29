package alreyesh.android.scanmarketclient.app;

import android.app.Application;
import android.content.Context;
import android.os.SystemClock;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;


import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Pending;
import alreyesh.android.scanmarketclient.models.Purchase;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class MyApp extends Application {
    public static AtomicInteger pendingID = new AtomicInteger();
    public static AtomicInteger purchaseID = new AtomicInteger();
    public static    AtomicInteger cartID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        pendingID = getIdByTable(realm, Pending.class);
        purchaseID= getIdByTable(realm, Purchase.class);
        cartID= getIdByTable(realm, Cart.class);
        realm.close();
        SystemClock.sleep(3000);
        super.onCreate();
    }
    //Realm
    private void setUpRealmConfig(){

        Realm.init(this);
        RealmConfiguration config = new
                RealmConfiguration.Builder()

                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }
    private <T extends RealmObject>AtomicInteger getIdByTable(Realm realm, Class<T>anyClass){
        RealmResults<T> results = realm.where(anyClass).findAll();
        return(!results.isEmpty())? new AtomicInteger(results.max("id").intValue()): new AtomicInteger();
    }

}
