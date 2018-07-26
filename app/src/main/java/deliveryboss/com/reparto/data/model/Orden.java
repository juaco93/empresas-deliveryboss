package deliveryboss.com.reparto.data.model;

import java.util.List;

public class Orden {
    String idorden;
    String empresa_idempresa;
    String nombre_empresa;
    String usuario_idusuario;
    String usuario_direccion_idusuario_direccion;
    String orden_estado_idorden_estado;
    String nombre;
    String apellido;
    String telefono;
    String estado;
    String info_estado;
    String fecha_hora;
    String fecha_hora_estado;
    String precio_delivery;
    String importe_total;
    String nota;
    String tipo_entrega_idtipo_entrega;
    String paga_con;
    String recibida;
    String recibida_fecha_hora;
    String latitud;
    String longitud;
    String calle;
    String numero;
    String habitacion;
    String barrio;
    String indicaciones;
    List<Orden_detalle> orden_detalle;

    public Orden(String idorden, String empresa_idempresa, String nombre_empresa, String usuario_idusuario, String usuario_direccion_idusuario_direccion, String orden_estado_idorden_estado, String nombre, String apellido, String telefono, String estado, String info_estado, String fecha_hora, String fecha_hora_estado, String precio_delivery, String importe_total, String nota, String tipo_entrega_idtipo_entrega, String paga_con, String recibida, String recibida_fecha_hora, String latitud, String longitud, String calle, String numero, String habitacion, String barrio, String indicaciones, List<Orden_detalle> orden_detalle) {
        this.idorden = idorden;
        this.empresa_idempresa = empresa_idempresa;
        this.nombre_empresa = nombre_empresa;
        this.usuario_idusuario = usuario_idusuario;
        this.usuario_direccion_idusuario_direccion = usuario_direccion_idusuario_direccion;
        this.orden_estado_idorden_estado = orden_estado_idorden_estado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.estado = estado;
        this.info_estado = info_estado;
        this.fecha_hora = fecha_hora;
        this.fecha_hora_estado = fecha_hora_estado;
        this.precio_delivery = precio_delivery;
        this.importe_total = importe_total;
        this.nota = nota;
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
        this.paga_con = paga_con;
        this.recibida = recibida;
        this.recibida_fecha_hora = recibida_fecha_hora;
        this.latitud = latitud;
        this.longitud = longitud;
        this.calle = calle;
        this.numero = numero;
        this.habitacion = habitacion;
        this.barrio = barrio;
        this.indicaciones = indicaciones;
        this.orden_detalle = orden_detalle;
    }

    public String getIdorden() {
        return idorden;
    }

    public void setIdorden(String idorden) {
        this.idorden = idorden;
    }

    public String getEmpresa_idempresa() {
        return empresa_idempresa;
    }

    public void setEmpresa_idempresa(String empresa_idempresa) {
        this.empresa_idempresa = empresa_idempresa;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombre_empresa) {
        this.nombre_empresa = nombre_empresa;
    }

    public String getUsuario_idusuario() {
        return usuario_idusuario;
    }

    public void setUsuario_idusuario(String usuario_idusuario) {
        this.usuario_idusuario = usuario_idusuario;
    }

    public String getUsuario_direccion_idusuario_direccion() {
        return usuario_direccion_idusuario_direccion;
    }

    public void setUsuario_direccion_idusuario_direccion(String usuario_direccion_idusuario_direccion) {
        this.usuario_direccion_idusuario_direccion = usuario_direccion_idusuario_direccion;
    }

    public String getOrden_estado_idorden_estado() {
        return orden_estado_idorden_estado;
    }

    public void setOrden_estado_idorden_estado(String orden_estado_idorden_estado) {
        this.orden_estado_idorden_estado = orden_estado_idorden_estado;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getInfo_estado() {
        return info_estado;
    }

    public void setInfo_estado(String info_estado) {
        this.info_estado = info_estado;
    }

    public String getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(String fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public String getFecha_hora_estado() {
        return fecha_hora_estado;
    }

    public void setFecha_hora_estado(String fecha_hora_estado) {
        this.fecha_hora_estado = fecha_hora_estado;
    }

    public String getPrecio_delivery() {
        return precio_delivery;
    }

    public void setPrecio_delivery(String precio_delivery) {
        this.precio_delivery = precio_delivery;
    }

    public String getImporte_total() {
        return importe_total;
    }

    public void setImporte_total(String importe_total) {
        this.importe_total = importe_total;
    }

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public String getTipo_entrega_idtipo_entrega() {
        return tipo_entrega_idtipo_entrega;
    }

    public void setTipo_entrega_idtipo_entrega(String tipo_entrega_idtipo_entrega) {
        this.tipo_entrega_idtipo_entrega = tipo_entrega_idtipo_entrega;
    }

    public String getPaga_con() {
        return paga_con;
    }

    public void setPaga_con(String paga_con) {
        this.paga_con = paga_con;
    }

    public String getRecibida() {
        return recibida;
    }

    public void setRecibida(String recibida) {
        this.recibida = recibida;
    }

    public String getRecibida_fecha_hora() {
        return recibida_fecha_hora;
    }

    public void setRecibida_fecha_hora(String recibida_fecha_hora) {
        this.recibida_fecha_hora = recibida_fecha_hora;
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

    public List<Orden_detalle> getOrden_detalle() {
        return orden_detalle;
    }

    public void setOrden_detalle(List<Orden_detalle> orden_detalle) {
        this.orden_detalle = orden_detalle;
    }
}
