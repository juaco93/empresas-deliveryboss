package deliveryboss.com.empresas.data.model;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class ApiResponse {
    String estado;
    String mensaje;

    public ApiResponse(String estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
