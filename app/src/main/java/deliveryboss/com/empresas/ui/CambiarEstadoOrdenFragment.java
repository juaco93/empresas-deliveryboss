package deliveryboss.com.empresas.ui;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import deliveryboss.com.empresas.R;
import deliveryboss.com.empresas.data.api.DeliverybossApi;
import deliveryboss.com.empresas.data.model.ApiResponse;
import deliveryboss.com.empresas.data.model.Empresa_repartidor_mensaje;
import deliveryboss.com.empresas.data.model.Orden;
import deliveryboss.com.empresas.data.model.Orden_detalle;
import deliveryboss.com.empresas.data.model.Orden_estado_informacion;
import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CambiarEstadoOrdenFragment extends DialogFragment {

    Spinner spEstadosPosibles;
    Button btnAceptar;
    Button btnCancelar;
    Orden orden;
    String valorEstado;
    List<Orden_estado_informacion> opciones;
    private boolean ordenRecibida;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private Context mContext;


    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.fragment_cambiar_estado_orden, null);

        mContext = getActivity();

        // Get your views by using view.findViewById() here and do your listeners.
        spEstadosPosibles = (Spinner) view.findViewById(R.id.spEstadosPosibles);
        btnAceptar = (Button) view.findViewById(R.id.btnAceptar);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelar);

        if(getArguments().getString("orden")!=null){
            orden = (new Gson()).fromJson((getArguments().getString("orden")),Orden.class);
        }

        if(getArguments().getString("valorEstado")!=null){
            valorEstado = getArguments().getString("valorEstado");
        }

        if(getArguments().getString("opciones")!=null){
            opciones = (new Gson()).fromJson((getArguments().getString("opciones")),new TypeToken<List<Orden_estado_informacion>>(){}.getType());
            mostrarOpciones(opciones);
        }else{
            mostrarOpcionesEmpty();
        }


        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cambiarEstadoOrden(orden,valorEstado);
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

    private void cambiarEstadoOrden(Orden orden, String idEstado){
        String authorization = SessionPrefs.get(getContext()).getPrefUsuarioToken();
        String idorden = orden.getIdorden();

        List<Orden_detalle> vacia = null;
        //vacia.add(new Orden_detalle("","","","","","","",""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());

        //Orden ordenMod = new Orden(idorden,"","","","","","","","","","","","","1","","","","",recibida,fechaHora,"","","","","","",vacia);
        Orden ordenMod = orden;

        ordenMod.setOrden_estado_idorden_estado(idEstado);
        ordenMod.setFecha_hora_estado(fechaHora);
        ordenMod.setInfo_estado(spEstadosPosibles.getSelectedItem().toString());

        if(idEstado.equals("7")){
            ordenMod.setRecibida("1");
            ordenMod.setRecibida_fecha_hora(fechaHora);
        }else{
            ordenMod.setRecibida("0");
        }

        Log.d("juaco93",(new Gson()).toJson(ordenMod));
        showErrorMessage("Se cambió el estado de la Orden!");

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
                            Log.d("juaco93", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("juaco93", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                Log.d("juaco93", "Respuesta del SV:" + response.body().getMensaje());
                //  showErrorMessage(response.body().getMensaje());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("juaco93", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }

    private void mostrarOpciones(List<Orden_estado_informacion> opciones) {
        List<String> list = new ArrayList<String>();

        list.add("Seleccioná una opción");
        //Traversing through the whole list to get all the names
        for(int i=0; i<opciones.size(); i++){
                if (opciones.get(i).getIdorden_estado().equals(valorEstado)) {
                    list.add(opciones.get(i).getOrden_estado_informacion());
                }
        }
        Log.d("juaco93","Tamanio de opciones correspondientes a accion-->"+list.size());

        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinner
        spEstadosPosibles.setAdapter(adapter);

    }

    private void mostrarOpcionesEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "No hay opciones disponibles";
        }
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spEstadosPosibles.setAdapter(adapter);
    }

    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }
}
