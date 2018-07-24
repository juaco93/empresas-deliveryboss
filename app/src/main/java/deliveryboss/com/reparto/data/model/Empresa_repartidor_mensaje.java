package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 03/07/2018.
 */

public class Empresa_repartidor_mensaje {
    String mensaje;
    String idtipo_emisor;
    String idemisor;
    String idtipo_receptor;
    String idreceptor;

    public Empresa_repartidor_mensaje(String mensaje, String idtipo_emisor, String idemisor, String idtipo_receptor, String idreceptor) {
        this.mensaje = mensaje;
        this.idtipo_emisor = idtipo_emisor;
        this.idemisor = idemisor;
        this.idtipo_receptor = idtipo_receptor;
        this.idreceptor = idreceptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getIdtipo_emisor() {
        return idtipo_emisor;
    }

    public void setIdtipo_emisor(String idtipo_emisor) {
        this.idtipo_emisor = idtipo_emisor;
    }

    public String getIdemisor() {
        return idemisor;
    }

    public void setIdemisor(String idemisor) {
        this.idemisor = idemisor;
    }

    public String getIdtipo_receptor() {
        return idtipo_receptor;
    }

    public void setIdtipo_receptor(String idtipo_receptor) {
        this.idtipo_receptor = idtipo_receptor;
    }

    public String getIdreceptor() {
        return idreceptor;
    }

    public void setIdreceptor(String idreceptor) {
        this.idreceptor = idreceptor;
    }
}
