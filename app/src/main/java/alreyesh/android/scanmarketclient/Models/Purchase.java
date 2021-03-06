package alreyesh.android.scanmarketclient.Models;

import alreyesh.android.scanmarketclient.APP.MyApp;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Purchase  extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String name;

    private float limit;

    private int color;
    @Required
    private String emailID;

    public Purchase(){

    }

    public Purchase(String name, float limit, int color,String emailID) {

        this.id = MyApp.PurchaseID.incrementAndGet();
        this.name = name;
        this.limit = limit;
        this.color = color;
        this.emailID = emailID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLimit() {
        return limit;
    }

    public void setLimit(float limit) {
        this.limit = limit;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }
}
