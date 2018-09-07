package deliveryboss.com.empresas.data.model;

import java.util.List;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class ApiResponseDirecciones {
    private List<Usuario_direccion> datos;

    public ApiResponseDirecciones(List<Usuario_direccion> datos) {
        this.datos = datos;
    }

    public List<Usuario_direccion> getDatos() {
        return datos;
    }
}