package deliveryboss.com.empresas.data.model;

import java.util.List;

public class ApiResponseLogin {
    private String estado;
    private Usuario_logueado usuario;

    public ApiResponseLogin(Usuario_logueado usuario) {
        this.usuario = usuario;
    }

    public Usuario_logueado getUsuario() {
        return usuario;
    }
}
