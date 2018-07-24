package deliveryboss.com.reparto.data.model;

import java.util.List;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class ApiResponseOrdenes {
    private List<Orden> datos;

    public ApiResponseOrdenes(List<Orden> datos) {
        this.datos = datos;
    }

    public List<Orden> getDatos() {
        return datos;
    }
}
