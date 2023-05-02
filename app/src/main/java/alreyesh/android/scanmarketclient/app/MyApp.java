package alreyesh.android.scanmarketclient.app;

import android.app.Application;
import android.os.SystemClock;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import java.util.concurrent.atomic.AtomicInteger;
import alreyesh.android.scanmarketclient.models.Cart;
import alreyesh.android.scanmarketclient.models.Pending;
import alreyesh.android.scanmarketclient.models.Purchase;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class MyApp extends Application {
    public static   AtomicInteger pendingID = new AtomicInteger();
    public static  AtomicInteger purchaseID = new AtomicInteger();
    public static    AtomicInteger cartID = new AtomicInteger();

    @Override
    public void onCreate() {
        setUpRealmConfig();
        Realm realm = Realm.getDefaultInstance();
        pendingID = getIdByTable(realm, Pending.class);
        purchaseID= getIdByTable(realm, Purchase.class);
        cartID= getIdByTable(realm, Cart.class);
        realm.close();
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();

        Picasso.setSingletonInstance(built);



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
