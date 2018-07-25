package deliveryboss.com.reparto.data.api;

/**
 * Created by Joaquin on 18/06/2018.
 */

import deliveryboss.com.reparto.data.model.ApiResponse;
import deliveryboss.com.reparto.data.model.ApiResponseDirecciones;
import deliveryboss.com.reparto.data.model.ApiResponseOrdenes;
import deliveryboss.com.reparto.data.model.Empresa_repartidor;
import deliveryboss.com.reparto.data.model.Empresa_repartidor_mensaje;
import deliveryboss.com.reparto.data.model.FbLoginBody;
import deliveryboss.com.reparto.data.model.FbRegisterBody;
import deliveryboss.com.reparto.data.model.LoginBody;
import deliveryboss.com.reparto.data.model.Orden;
import deliveryboss.com.reparto.data.model.Usuario;
import deliveryboss.com.reparto.data.model.regIdBody;
import deliveryboss.com.reparto.data.model.Usuario_reparto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Joaquin on 23/6/2017.
 */

public interface  DeliverybossApi {

    public static final String BASE_URL = "http://api.deliveryboss.com.ar/v1/";

    @POST("empresas_repartidor/login")
    Call<Empresa_repartidor> loginUsuarioReparto(@Body LoginBody loginBody);

    @POST("empresas_repartidor/login")
    Call<Empresa_repartidor> loginFb(@Body FbLoginBody fbLoginBody);

    @POST("ordenes_mensajes/enviar/{idorden}/{idusuario}")
    Call<ApiResponse> enviarMensaje(@Header("Authorization") String authorization,
                                    @Path(value = "idorden", encoded = true) String idorden,
                                    @Path(value = "idusuario", encoded = true) String idusuario,
                                    @Body Empresa_repartidor_mensaje mensaje
    );

    @POST("empresas_repartidor/registro")
    Call<Empresa_repartidor> registroFb(@Body FbRegisterBody fbRegisterBody);


    @GET("ordenes_reparto/{idempresa}")
    Call<ApiResponseOrdenes> obtenerOrdenesEmpresa(@Header("Authorization") String authorization,
                                                   @Path(value = "idempresa", encoded = true) String idempresa
    );


    @POST("empresas_repartidor/registrationId")
    Call<ApiResponse> registrarRegId(@Body regIdBody regIdBody);
/*
    @GET("direcciones/{idusuario}")
    Call<ApiResponseDirecciones> obtenerDireccionesUsuario(@Header("Authorization") String authorization,
                                                           @Path(value = "idusuario", encoded = true) String idusuario
    );
    */

    @PUT("ordenes/{idorden}")
    Call<ApiResponse> modificarOrden(@Header("Authorization") String authorization,
                                     @Path(value = "idorden", encoded = true) String idorden,
                                     @Body Orden orden
    );



}
