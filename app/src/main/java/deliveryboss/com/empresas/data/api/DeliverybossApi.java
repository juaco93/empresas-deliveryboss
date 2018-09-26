package deliveryboss.com.empresas.data.api;

/**
 * Created by Joaquin on 18/06/2018.
 */

import deliveryboss.com.empresas.data.model.ApiResponse;
import deliveryboss.com.empresas.data.model.ApiResponseDirecciones;
import deliveryboss.com.empresas.data.model.ApiResponseLogin;
import deliveryboss.com.empresas.data.model.ApiResponseOrdenes;
import deliveryboss.com.empresas.data.model.ApiResponseOrdenesEstadoInformacion;
import deliveryboss.com.empresas.data.model.Empresa_repartidor;
import deliveryboss.com.empresas.data.model.Empresa_repartidor_mensaje;
import deliveryboss.com.empresas.data.model.Empresa_usuario;
import deliveryboss.com.empresas.data.model.FbLoginBody;
import deliveryboss.com.empresas.data.model.FbRegisterBody;
import deliveryboss.com.empresas.data.model.LoginBody;
import deliveryboss.com.empresas.data.model.Orden;
import deliveryboss.com.empresas.data.model.Usuario;
import deliveryboss.com.empresas.data.model.Usuario_logueado;
import deliveryboss.com.empresas.data.model.regIdBody;
import deliveryboss.com.empresas.data.model.Usuario_reparto;
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

    @POST("empresas/login")
    Call<Empresa_usuario> loginUsuarioEmpresa(@Body LoginBody loginBody);

    @POST("usuarios/login_nuevo")
    Call<ApiResponseLogin> loginUsuarioNuevo(@Body LoginBody loginBody);

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

    @PUT("ordenes/{idorden}")
    Call<ApiResponse> modificarOrden(@Header("Authorization") String authorization,
                                     @Path(value = "idorden", encoded = true) String idorden,
                                     @Body Orden orden
    );


    @GET("ordenes_estado_informacion/")
    Call<ApiResponseOrdenesEstadoInformacion> obtenerEstadosOrdenes(@Header("Authorization") String authorization
    );



}
