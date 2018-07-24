package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 18/06/2018.
 */


public class LoginBody {
    private String e_mail;
    private String contrasena;

    public LoginBody(String e_mail, String contrasena) {
        this.e_mail = e_mail;
        this.contrasena = contrasena;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

}

