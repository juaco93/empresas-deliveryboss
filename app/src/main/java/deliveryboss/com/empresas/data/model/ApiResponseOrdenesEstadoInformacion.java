package deliveryboss.com.empresas.data.model;

import java.util.List;

public class ApiResponseOrdenesEstadoInformacion {
    private List<Orden_estado_informacion> datos;

    public ApiResponseOrdenesEstadoInformacion(List<Orden_estado_informacion> datos) {
        this.datos = datos;
    }

    public List<Orden_estado_informacion> getDatos() {
        return datos;
    }
}
