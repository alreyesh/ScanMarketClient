package alreyesh.android.scanmarketclient.models;
public class DetailProduct {
    private String cod;
    private String name;
    private String cantidad;
    private String subtotal;
    private  String link;
    public DetailProduct(){}
    public DetailProduct(String cod, String name, String cantidad, String subtotal, String link) {
        this.cod = cod;
        this.name = name;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.link = link;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
