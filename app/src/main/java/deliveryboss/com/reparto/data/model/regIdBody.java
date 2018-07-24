package deliveryboss.com.reparto.data.model;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class regIdBody {
    String regId;
    String idempresa_repartidor;

    public regIdBody(String regId, String idempresa_repartidor) {
        this.regId = regId;
        this.idempresa_repartidor = idempresa_repartidor;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getIdempresa_repartidor() {
        return idempresa_repartidor;
    }

    public void setIdempresa_repartidor(String idempresa_repartidor) {
        this.idempresa_repartidor = idempresa_repartidor;
    }
}
