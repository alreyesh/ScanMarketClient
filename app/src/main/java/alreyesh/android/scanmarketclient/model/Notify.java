package alreyesh.android.scanmarketclient.model;
public class Notify {
private String titulo;
private String textocorto;
private String mensaje;
private String image;
public Notify(){

}
    public Notify(String titulo, String textocorto, String mensaje, String image ) {
        this.textocorto = textocorto;
        this.titulo = titulo;
        this.mensaje = mensaje;
        this.image = image;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTextocorto() {
        return textocorto;
    }

    public void setTextocorto(String textocorto) {
        this.textocorto = textocorto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
