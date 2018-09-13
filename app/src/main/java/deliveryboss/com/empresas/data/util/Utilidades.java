package deliveryboss.com.empresas.data.util;

import android.content.Context;

import com.google.gson.Gson;

import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;

public class Utilidades {

    public static void setearEmpresaPorDefecto(Context context, String rol){
        if(rol!=null){
            Roles rolUsuario = (new Gson()).fromJson(rol,Roles.class);
            SessionPrefs.get(context).saveEmpresaPorDefecto(rolUsuario);
        }
    }
}
