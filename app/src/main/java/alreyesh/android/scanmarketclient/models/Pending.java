package alreyesh.android.scanmarketclient.models;



import alreyesh.android.scanmarketclient.app.MyApp;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Pending  extends RealmObject {
    @PrimaryKey
    private int id;
    private String codorder;
    private String user;
    private String fecha;
    private String total;
    private RealmList<Cart> productos;
    private byte[] bitmap;
    public Pending(){}

    public Pending(String codorder, String user, String fecha, String total, RealmList<Cart> productos,byte[] bitmap) {
        this.id = MyApp.pendingID.incrementAndGet();
        this.codorder = codorder;
        this.user = user;
        this.fecha = fecha;
        this.total = total;
        this.productos = productos;
        this.bitmap =bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodorder() {
        return codorder;
    }

    public void setCodorder(String codorder) {
        this.codorder = codorder;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public RealmList<Cart> getProductos() {
        return productos;
    }

    public void setProductos(RealmList<Cart> productos) {
        this.productos = productos;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }
}
