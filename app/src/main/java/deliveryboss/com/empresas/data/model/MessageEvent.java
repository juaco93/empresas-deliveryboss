package deliveryboss.com.empresas.data.model;

/**
 * Created by Joaquin on 19/06/2018.
 */


public class MessageEvent {
    String idevento;
    String descripcion;

    public MessageEvent(String idevento, String descripcion) {
        this.idevento = idevento;
        this.descripcion = descripcion;
    }

    public String getIdevento() {
        return idevento;
    }

    public void setIdevento(String idevento) {
        this.idevento = idevento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
