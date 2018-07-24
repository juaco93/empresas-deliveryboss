package deliveryboss.com.reparto.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import deliveryboss.com.reparto.R;
import deliveryboss.com.reparto.data.api.DeliverybossApi;
import deliveryboss.com.reparto.data.model.ApiResponse;
import deliveryboss.com.reparto.data.model.Empresa_repartidor_mensaje;
import deliveryboss.com.reparto.data.model.Orden;
import deliveryboss.com.reparto.data.model.Orden_detalle;
import deliveryboss.com.reparto.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EnviarMensajeFragment extends DialogFragment {


    EditText txtMensaje;
    Button btnEnviarMensaje;
    Orden orden;
    private boolean ordenRecibida;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private Context mContext;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_enviar_mensaje, null);

        mContext = getActivity();

        // Get your views by using view.findViewById() here and do your listeners.
        txtMensaje = (EditText) view.findViewById(R.id.txtMensajeCuerpo);
        btnEnviarMensaje = (Button) view.findViewById(R.id.btnMensajeEnviar);

        if(getArguments().getString("orden")!=null){
            orden = (new Gson()).fromJson((getArguments().getString("orden")),Orden.class);
        }

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarMensaje();
            }
        });

        // Set the dialog layout
        builder.setView(view);

        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);

        return builder.create();
    }



    private void enviarMensaje(){
        Log.d("mensaje","Enviando Mensaje al server");

        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idusuario = orden.getUsuario_idusuario();
        String idrepartidor = SessionPrefs.get(getActivity()).getPrefUsuarioIdUsuario();
        String mensaje = "";
        if(txtMensaje.getText()!=null)
            mensaje = txtMensaje.getText().toString();

        String idempresa = orden.getEmpresa_idempresa();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());

        String orden_idorden = orden.getIdorden();

        Empresa_repartidor_mensaje objeto = new Empresa_repartidor_mensaje(mensaje,"4",idrepartidor,"2",idusuario);

        Log.d("mensaje","JSON->"+new Gson().toJson(objeto));

        // Realizar petición HTTP
        Call<ApiResponse> call = mDeliverybossApi.enviarMensaje(authorization,orden_idorden,idusuario ,objeto);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    //String error = "Ha ocurrido un error. Contacte al administrador";
                    String error = "Ocurrió un error. Contactanos a info@deliveryboss.com.ar";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("mensaje", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    return;
                }
                Log.d("mensaje", "Respuesta del SV:" + response.body().getMensaje());
                showErrorMessage(response.body().getMensaje());
                //EventBus.getDefault().post(new MessageEvent("1","Dialogo calificar cerrado"));
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("mensaje", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Mensaje enviado");
            }
        });
    }

    private void marcarOrdenComoEntregada(){
        Log.d("calificacion","Modificando el estado de la Orden");

        String authorization = SessionPrefs.get(getActivity()).getPrefUsuarioToken();
        String idorden = orden.getIdorden();

        List<Orden_detalle> vacia = null;
        //vacia.add(new Orden_detalle("","","","","","","",""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());
        String recibida="";
        if(ordenRecibida){
            recibida="1";
        }else{
            recibida="0";
        }

        Orden ordenMod = new Orden(idorden,"","","","","","","","","","","","","1","","","","",recibida,fechaHora,"","","","","","",vacia);

        Log.d("calificacion",(new Gson()).toJson(ordenMod));

        // Realizar petición HTTP
        Call<ApiResponse> call2 = mDeliverybossApi.modificarOrden(authorization,idorden,ordenMod);
        call2.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call,
                                   Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    //String error = "Ha ocurrido un error. Contacte al administrador";
                    String error = "Ocurrió un error. Contactanos a info@deliveryboss.com.ar";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                        try {
                            Log.d("mensaje", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("calificacion", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                Log.d("calificacion", "Respuesta del SV:" + response.body().getMensaje());
                //  showErrorMessage(response.body().getMensaje());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("calificacion", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Mensaje enviado");
            }
        });
    }


    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }


}
