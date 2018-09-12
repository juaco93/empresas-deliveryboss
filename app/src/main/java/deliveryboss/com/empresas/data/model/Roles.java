package deliveryboss.com.empresas.data.model;

public class Roles {
    String idrol;
    String idusuario;
    String idempresa;
    String rol_tipo;
    String logo;


    public Roles(String idrol, String idusuario, String idempresa, String rol_tipo, String logo) {
        this.idrol = idrol;
        this.idusuario = idusuario;
        this.idempresa = idempresa;
        this.rol_tipo = rol_tipo;
        this.logo = logo;
    }

    public String getIdrol() {
        return idrol;
    }

    public void setIdrol(String idrol) {
        this.idrol = idrol;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getIdempresa() {
        return idempresa;
    }

    public void setIdempresa(String idempresa) {
        this.idempresa = idempresa;
    }

    public String getRol_tipo() {
        return rol_tipo;
    }

    public void setRol_tipo(String rol_tipo) {
        this.rol_tipo = rol_tipo;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
