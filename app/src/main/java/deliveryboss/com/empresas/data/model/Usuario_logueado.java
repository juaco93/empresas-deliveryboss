package deliveryboss.com.empresas.data.model;

import java.util.List;

public class Usuario_logueado {
    String idusuario;
    String nombre;
    String apellido;
    String e_mail;
    String imagen;
    String token;
    List<Roles> roles;

    public Usuario_logueado(String idusuario, String nombre, String apellido, String e_mail, String imagen, String token, List<Roles> roles) {
        this.idusuario = idusuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.e_mail = e_mail;
        this.imagen = imagen;
        this.token = token;
        this.roles = roles;
    }

    public String getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(String idusuario) {
        this.idusuario = idusuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }
}
