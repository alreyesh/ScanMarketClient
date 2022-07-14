package alreyesh.android.scanmarketclient.model;
public class User {
    private String uid;
    private String nombre;
    private String apellidos;
    private String celular;
    private String tipodocumento;
    private String documento;

    public User(){}

    public User(String uid, String nombre, String apellidos, String celular, String tipodocumento, String documento) {
        this.uid = uid;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.celular = celular;
        this.tipodocumento = tipodocumento;
        this.documento = documento;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getTipodocumento() {
        return tipodocumento;
    }

    public void setTipodocumento(String tipodocumento) {
        this.tipodocumento = tipodocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
}
