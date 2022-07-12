package alreyesh.android.scanmarketclient.models;


import alreyesh.android.scanmarketclient.app.MyApp;
import io.realm.RealmList;
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
    private int icon;
    private RealmList<Cart> carts;


public Purchase(){

}

    public Purchase(String name, float limit, int color, String emailID,int icon) {
        this.id = MyApp.PurchaseID.incrementAndGet();
        this.name = name;
        this.limit = limit;
        this.color = color;
        this.emailID = emailID;
        this.icon = icon;

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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setCarts(RealmList<Cart> carts) {
        this.carts = carts;
    }

    public RealmList<Cart> getCarts() {
        return carts;
    }


}
