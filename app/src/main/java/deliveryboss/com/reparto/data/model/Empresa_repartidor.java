package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 21/06/2018.
 */

public class Empresa_repartidor {
    String idempresa_repartidor;
    String empresa_idempresa;
    String nombre;
    String apellido;
    String logo;
    String e_mail;
    String telefono;
    String token;

    public Empresa_repartidor(String idempresa_repartidor, String empresa_idempresa, String nombre, String apellido, String logo, String e_mail, String telefono, String token) {
        this.idempresa_repartidor = idempresa_repartidor;
        this.empresa_idempresa = empresa_idempresa;
        this.nombre = nombre;
        this.apellido = apellido;
        this.logo = logo;
        this.e_mail = e_mail;
        this.telefono = telefono;
        this.token = token;
    }

    public String getIdempresa_repartidor() {
        return idempresa_repartidor;
    }

    public void setIdempresa_repartidor(String idempresa_repartidor) {
        this.idempresa_repartidor = idempresa_repartidor;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
