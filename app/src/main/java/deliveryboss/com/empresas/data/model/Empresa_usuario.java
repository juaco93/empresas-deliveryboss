package deliveryboss.com.empresas.data.model;

public class Empresa_usuario {

    String idempresa;
    String nombre_empresa;
    String token;
    String e_mail;
    String logo;

    public Empresa_usuario(String idempresa, String nombre_empresa, String token, String e_mail, String logo) {
        this.idempresa = idempresa;
        this.nombre_empresa = nombre_empresa;
        this.token = token;
        this.e_mail = e_mail;
        this.logo = logo;
    }

    public String getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(String idempresa) {
        this.idempresa = idempresa;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
