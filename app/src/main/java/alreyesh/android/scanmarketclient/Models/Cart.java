package alreyesh.android.scanmarketclient.Models;

import alreyesh.android.scanmarketclient.APP.MyApp;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Cart  extends RealmObject {
    @PrimaryKey
    private int id;
    private String productID;
    private String productName;
    private String imagenProduct;
    private String productPrice;
    private String countProduct;
    private String subPrice;


    public Cart(){
    }


    public Cart(String productID, String productName, String imagenProduct, String productPrice, String countProduct, String subPrice) {
        this.id = MyApp.CartID.incrementAndGet();;
        this.productID = productID;
        this.productName = productName;
        this.imagenProduct = imagenProduct;
        this.productPrice = productPrice;
        this.countProduct = countProduct;
        this.subPrice = subPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getImagenProduct() {
        return imagenProduct;
    }

    public void setImagenProduct(String imagenProduct) {
        this.imagenProduct = imagenProduct;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCountProduct() {
        return countProduct;
    }

    public void setCountProduct(String countProduct) {
        this.countProduct = countProduct;
    }

    public String getSubPrice() {
        return subPrice;
    }

    public void setSubPrice(String subPrice) {
        this.subPrice = subPrice;
    }
}
