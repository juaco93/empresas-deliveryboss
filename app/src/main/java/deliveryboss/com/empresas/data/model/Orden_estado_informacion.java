package deliveryboss.com.empresas.data.model;

public class Orden_estado_informacion {
    String idorden_estado_informacion;
    String idorden_estado;
    String orden_estado_informacion;

    public Orden_estado_informacion(String idorden_estado_informacion, String idorden_estado, String orden_estado_informacion) {
        this.idorden_estado_informacion = idorden_estado_informacion;
        this.idorden_estado = idorden_estado;
        this.orden_estado_informacion = orden_estado_informacion;
    }

    public String getIdorden_estado_informacion() {
        return idorden_estado_informacion;
    }

    public void setIdorden_estado_informacion(String idorden_estado_informacion) {
        this.idorden_estado_informacion = idorden_estado_informacion;
    }

    public String getIdorden_estado() {
        return idorden_estado;
    }

    public void setIdorden_estado(String idorden_estado) {
        this.idorden_estado = idorden_estado;
    }

    public String getOrden_estado_informacion() {
        return orden_estado_informacion;
    }

    public void setOrden_estado_informacion(String orden_estado_informacion) {
        this.orden_estado_informacion = orden_estado_informacion;
    }
}
