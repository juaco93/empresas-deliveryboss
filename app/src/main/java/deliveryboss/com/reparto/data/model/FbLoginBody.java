package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class FbLoginBody {
    String e_mail;
    String idfacebook;

    public FbLoginBody(String e_mail, String idfacebook) {
        this.e_mail = e_mail;
        this.idfacebook = idfacebook;
    }

    public String getE_mail() {
        return e_mail;
    }

    public void setE_mail(String e_mail) {
        this.e_mail = e_mail;
    }

    public String getIdfacebook() {
        return idfacebook;
    }

    public void setIdfacebook(String idfacebook) {
        this.idfacebook = idfacebook;
    }
}
