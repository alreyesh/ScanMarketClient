package alreyesh.android.scanmarketclient.models;


import java.util.ArrayList;
import java.util.Date;


public class Order {
    private String codorder;
    private String user;
    private Date fecha;
    private String total;
    private ArrayList<DetailProduct> productos;
    public Order(){
    }
    public Order(String codorder, String user, Date fecha, String total, ArrayList<DetailProduct> productos) {
        this.codorder = codorder;
        this.user = user;
        this.fecha = fecha;
        this.total = total;
        this.productos = productos;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public ArrayList<DetailProduct> getProductos() {
        return productos;
    }

    public void setProductos(ArrayList<DetailProduct> productos) {
        this.productos = productos;
    }
}
