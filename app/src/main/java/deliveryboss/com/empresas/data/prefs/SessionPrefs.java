package deliveryboss.com.empresas.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

import deliveryboss.com.empresas.data.model.Empresa_repartidor;
import deliveryboss.com.empresas.data.model.Empresa_usuario;
import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.model.Usuario;
import deliveryboss.com.empresas.data.model.Usuario_logueado;
import deliveryboss.com.empresas.data.model.Usuario_reparto;

/**
 * Created by Joaquin on 23/6/2017.
 */

public class SessionPrefs {

    public static final String PREFS_NAME = "DELIVERYBOSS_PREFS";
    public static final String PREF_IDUSUARIO_REPARTO = "PREF_IDUSUARIO_REPARTO";
    public static final String PREF_REGID = "PREF_REGID";
    public static final String PREF_USUARIO_REPARTO_NOMBRE = "PREF_USUARIO_REPARTO_NOMBRE";
    public static final String PREF_USUARIO_REPARTO_APELLIDO = "PREF_USUARIO_REPARTO_APELLIDO";
    public static final String PREF_USUARIO_REPARTO_EMAIL = "PREF_USUARIO_REPARTO_EMAIL";
    public static final String PREF_USUARIO_REPARTO_TELEFONO = "PREF_USUARIO_REPARTO_TELEFONO";
    public static final String PREF_USUARIO_REPARTO_TOKEN = "PREF_USUARIO_REPARTO_TOKEN";
    public static final String PREF_USUARIO_REPARTO_EMPRESA_IDEMPRESA = "PREF_USUARIO_REPARTO_EMPRESA_IDEMPRESA";



    public static final String PREF_USUARIO_CONTRASENA = "PREF_USUARIO_CONTRASENA";
    public static final String PREF_USUARIO_ULTIMA_DIRECCION = "PREF_USUARIO_ULTIMA_DIRECCION";
    public static final String PREF_USUARIO_IDFACEBOOK = "PREF_USUARIO_IDFACEBOOK";
    public static final String PREF_USUARIO_IDCIUDAD = "PREF_USUARIO_IDCIUDAD";
    public static final String PREF_USUARIO_CIUDAD = "PREF_USUARIO_CIUDAD";
    public static final String DIRECCION_ID = "DIRECCION_ID";
    public static final String DIRECCION_IDCIUDAD = "DIRECCION_IDCIUDAD";
    public static final String DIRECCION_CALLE = "DIRECCION_CALLE";
    public static final String DIRECCION_NUMERO = "DIRECCION_NUMERO";
    public static final String DIRECCION_LATITUD = "DIRECCION_LATITUD";
    public static final String DIRECCION_LONGITUD = "DIRECCION_LONGITUD";

    public static final String PREF_USUARIO_NOMBRE = "PREF_USUARIO_NOMBRE";
    public static final String PREF_USUARIO_APELLIDO = "PREF_USUARIO_APELLIDO";
    public static final String PREF_USUARIO_EMAIL = "PREF_USUARIO_EMAIL";
    public static final String PREF_USUARIO_IMAGEN = "PREF_USUARIO_IMAGEN";
    public static final String PREF_USUARIO_TOKEN = "PREF_USUARIO_TOKEN";
    public static final String PREF_USUARIO_ROLES = "PREF_USUARIO_ROLES";
    public static final String PREF_USUARIO_EMPRESA_DEFECTO = "PREF_USUARIO_EMPRESA_DEFECTO";

    // VARIABLES ESTATICAS PARA ROLES - GUARDAN EL IDEMPRESA DEL ROL ESPECIFICADO EN SU NOMBRE //
    public static final String PREF_USUARIO_ROLES_ADMIN = "PREF_USUARIO_ROLES_ADMIN";
    public static final String PREF_USUARIO_ROLES_REPARTIDOR = "PREF_USUARIO_ROLES_REPARTIDOR";


    private static SessionPrefs INSTANCE;
    private final SharedPreferences mPrefs;
    private boolean mIsLoggedIn = false;

    public static SessionPrefs get(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SessionPrefs(context);
        }
        return INSTANCE;
    }

    private SessionPrefs(Context context) {
        mPrefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USUARIO_NOMBRE, null));
    }


    public boolean isLoggedIn(){
        return mIsLoggedIn;
    }

    public void saveUsuario(Usuario_logueado usuario) {
        if (usuario != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            //editor.putString(PREF_EMPRESA_IDEMPRESA, usuario.getRoles().);
            editor.putString(PREF_USUARIO_NOMBRE, usuario.getNombre());
            editor.putString(PREF_USUARIO_APELLIDO, usuario.getApellido());
            editor.putString(PREF_USUARIO_EMAIL, usuario.getE_mail());
            editor.putString(PREF_USUARIO_IMAGEN, usuario.getImagen());
            editor.putString(PREF_USUARIO_TOKEN, usuario.getToken());

            String rolesJson = null;
            if(usuario.getRoles()!=null)
                rolesJson = new Gson().toJson(usuario.getRoles());

            /*
            String rolesConcat = "";
            if(usuario.getRoles()!=null){
                for(int i=0; i<usuario.getRoles().size();i++){
                    rolesConcat+= usuario.getRoles().get(i).getRol_tipo()+"-"+usuario.getRoles().get(i).getIdempresa()+"-"+usuario.getRoles().get(i).getLogo();
                    if(i<usuario.getRoles().size()-1){
                        rolesConcat+=", ";
                    }
                }
            }*/

            editor.putString(PREF_USUARIO_ROLES,rolesJson);
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void modificarUsuario(Empresa_repartidor usuario) {
        if (usuario != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            //editor.putString(PREF_IDUSUARIO, usuario.getIdusuario());
            editor.putString(PREF_USUARIO_REPARTO_APELLIDO, usuario.getApellido());
            editor.putString(PREF_USUARIO_REPARTO_EMAIL, usuario.getE_mail());
            editor.putString(PREF_USUARIO_REPARTO_TELEFONO, usuario.getTelefono());
            editor.putString(PREF_USUARIO_REPARTO_NOMBRE, usuario.getNombre());
            //editor.putString(PREF_USUARIO_TOKEN, usuario.getToken());
            //editor.putString(PREF_USUARIO_CONTRASENA, usuario.getContrasena());
            //editor.putString(PREF_USUARIO_ULTIMA_DIRECCION, usuario.getUltima_direccion());
            editor.apply();

            mIsLoggedIn = true;
        }
    }

    public void saveRegId(String regId) {
        if (regId != null) {
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_REGID, regId);
            editor.apply();
        }
    }

    public void saveEmpresaPorDefecto(Roles rol) {
        if (rol != null) {
            String rolPorDefectoJson = new Gson().toJson(rol);
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USUARIO_EMPRESA_DEFECTO, rolPorDefectoJson);
            editor.apply();
        }
    }

    public void saveCiudad(String idciudad, String ciudad){
        if(idciudad!=null){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USUARIO_IDCIUDAD, idciudad);
            editor.putString(PREF_USUARIO_CIUDAD, ciudad);
            editor.apply();
        }
    }

    public void saveDireccion(String iddireccion, String idciudad, String ciudad,String calle, String numero, String latitud, String longitud){
        if(iddireccion!=null){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(DIRECCION_ID, iddireccion);
            editor.putString(DIRECCION_IDCIUDAD, idciudad);
            editor.putString(PREF_USUARIO_CIUDAD,ciudad);
            editor.putString(DIRECCION_CALLE, calle);
            editor.putString(DIRECCION_NUMERO, numero);
            editor.putString(DIRECCION_LATITUD, latitud);
            editor.putString(DIRECCION_LONGITUD, longitud);

            editor.apply();
        }
    }


    public void logOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USUARIO_NOMBRE, null);
        editor.putString(PREF_USUARIO_APELLIDO, null);
        editor.putString(PREF_USUARIO_EMAIL, null);
        editor.putString(PREF_USUARIO_IMAGEN, null);
        editor.putString(PREF_USUARIO_TOKEN, null);
        editor.putString(PREF_USUARIO_ROLES, null);
        editor.apply();
    }

    public String getPrefUsuarioToken(){
        return mPrefs.getString(PREF_USUARIO_TOKEN, null);
    }

    public String getPrefUsuarioRoles(){
        return mPrefs.getString(PREF_USUARIO_ROLES, null);
    }

    public String getPrefUsuarioEmpresaPorDefecto(){
        return mPrefs.getString(PREF_USUARIO_EMPRESA_DEFECTO, null);
    }

    public String getPrefUsuarioNombreyApellido(){
        return mPrefs.getString(PREF_USUARIO_NOMBRE, null)+" "+mPrefs.getString(PREF_USUARIO_APELLIDO, null);
    }
    /*
    public String getPrefUsuarioEmpresaIdempresa(){
        return mPrefs.getString(PREF_USU, null);
    }*/

    public String getPrefUsuarioEmail(){
        return mPrefs.getString(PREF_USUARIO_EMAIL, null);
    }

    public String getPrefUsuarioImagen(){
        return mPrefs.getString(PREF_USUARIO_IMAGEN, null);
    }

    public String getPrefUsuarioIdCiudad(){
        return mPrefs.getString(PREF_USUARIO_IDCIUDAD, null);
    }
    public String getPrefUsuarioCiudad(){
        return mPrefs.getString(PREF_USUARIO_CIUDAD, null);
    }
    public String getPrefUsuarioRegId(){
        return mPrefs.getString(PREF_REGID, null);
    }

    // PREFS DE UBICACION //
    public String getPrefUsuarioIdDireccion(){
        return mPrefs.getString(DIRECCION_ID, null);
    }
    public String getPrefUsuarioDireccionIdCiudad(){
        return mPrefs.getString(DIRECCION_IDCIUDAD, null);
    }
    public String getPrefUsuarioDireccionCalle(){
        return mPrefs.getString(DIRECCION_CALLE, null);
    }
    public String getPrefUsuarioDireccionNumero(){
        return mPrefs.getString(DIRECCION_NUMERO, null);
    }
    public String getPrefUsuarioDireccionLatitud(){
        return mPrefs.getString(DIRECCION_LATITUD, null);
    }
    public String getPrefUsuarioDireccionLongitud(){
        return mPrefs.getString(DIRECCION_LONGITUD, null);
    }

}
