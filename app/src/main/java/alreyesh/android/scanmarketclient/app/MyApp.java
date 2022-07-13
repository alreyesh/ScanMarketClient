package alreyesh.android.scanmarketclient.app;

import android.app.Application;
import android.os.SystemClock;


import java.util.concurrent.atomic.AtomicInteger;

import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Purchase;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class MyApp extends Application {
    public static AtomicInteger purchaseID = new AtomicInteger();
    public static    AtomicInteger cartID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
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
