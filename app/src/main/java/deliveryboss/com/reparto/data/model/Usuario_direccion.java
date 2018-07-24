package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class Usuario_direccion {
    String idusuario_direccion;
    String usuario_idusuario;
    String ciudad_idciudad;
    String ciudad;
    String calle;
    String numero;
    String habitacion;
    String barrio;
    String indicaciones;
    String latitud;
    String longitud;

    public Usuario_direccion(String idusuario_direccion, String usuario_idusuario, String ciudad_idciudad, String ciudad, String calle, String numero, String habitacion, String barrio, String indicaciones, String latitud, String longitud) {
        this.idusuario_direccion = idusuario_direccion;
        this.usuario_idusuario = usuario_idusuario;
        this.ciudad_idciudad = ciudad_idciudad;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
        this.habitacion = habitacion;
        this.barrio = barrio;
        this.indicaciones = indicaciones;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getIdusuario_direccion() {
        return idusuario_direccion;
    }

    public void setIdusuario_direccion(String idusuario_direccion) {
        this.idusuario_direccion = idusuario_direccion;
    }

    public String getUsuario_idusuario() {
        return usuario_idusuario;
    }

    public void setUsuario_idusuario(String usuario_idusuario) {
        this.usuario_idusuario = usuario_idusuario;
    }

    public String getCiudad_idciudad() {
        return ciudad_idciudad;
    }

    public void setCiudad_idciudad(String ciudad_idciudad) {
        this.ciudad_idciudad = ciudad_idciudad;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(String habitacion) {
        this.habitacion = habitacion;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
}
