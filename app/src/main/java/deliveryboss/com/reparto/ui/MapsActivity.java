package deliveryboss.com.reparto.ui;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import deliveryboss.com.reparto.R;
import deliveryboss.com.reparto.data.api.DeliverybossApi;
import deliveryboss.com.reparto.data.model.ApiResponseDirecciones;
import deliveryboss.com.reparto.data.model.Orden;
import deliveryboss.com.reparto.data.model.Usuario_direccion;
import deliveryboss.com.reparto.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private RecyclerView mListaDirecciones;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    private static final int FRAGMENTO_AGREGAR_DIRECCION = 3;
    private SwipeRefreshLayout swipeRefreshLayout;


    List<Usuario_direccion> serverDirecciones;
    Usuario_direccion direccionGuardada;
    Usuario_direccion direccionRecibidaDialog;
    Orden orden;
    String authorization;
    Context context;
    String ordenJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        LatLng larioja = new LatLng(-29.412296, -66.856449);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(larioja, 15));

        ordenJSON = getIntent().getExtras().getString("ordenJSON");
        //Log.d("direcciones",ordenJSON);
        orden = new Gson().fromJson(ordenJSON, Orden.class);
        irADireccionDestino(orden);

    }

    private void irADireccionDestino(Orden orden_recibida){
        //Log.d("direcciones","Entramos a mostrar la direccion destino");
        if(orden_recibida.getLatitud()!=null && orden_recibida.getLongitud()!=null) {
            if (!orden_recibida.getLatitud().equals("") && !orden_recibida.getLongitud().equals("")) {
                Double latGuar = Double.valueOf(orden_recibida.getLatitud());
                Double lonGuar = Double.valueOf(orden_recibida.getLongitud());
                LatLng destino = new LatLng(latGuar, lonGuar);
                mMap.addMarker(new MarkerOptions().position(destino)
                        .title(orden_recibida.getNombre() + " " +orden_recibida.getApellido())
                        .snippet(orden_recibida.getCalle() + " " + orden_recibida.getNumero() + " " +orden_recibida.getHabitacion() + ". " + orden_recibida.getIndicaciones()
                        )
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 15));
            }
        }
    }


    /*
    private void obtenerDirecciones(){
        authorization = "1001";

        String idusuario = orden.getUsuario_idusuario();
        String iddireccion = orden.getUsuario_direccion_idusuario_direccion();


        //String idusuario = SessionPrefs.get(getApplicationContext()).getPrefUsuarioIdUsuario();
        Log.d("gson", "Recuperando Direcciones desde el Server");

        // Realizar petición HTTP
        Call<ApiResponseDirecciones> call = mDeliverybossApi.obtenerDireccionesUsuario(authorization,idusuario);
        call.enqueue(new Callback<ApiResponseDirecciones>() {
            @Override
            public void onResponse(Call<ApiResponseDirecciones> call,
                                   Response<ApiResponseDirecciones> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        Log.d("gson", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        Log.d("gson", response.errorBody().toString());
                    }

                    return;
                }

                serverDirecciones = response.body().getDatos();
                Log.d("gson", "tod bien, recibido: " + response.body().getDatos().toString());
                if (serverDirecciones.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarDirecciones(serverDirecciones);
                } else {
                    // Mostrar empty state
                    mostrarDireccionesEmpty();
                }
            }

            @Override
            public void onFailure(Call<ApiResponseDirecciones> call, Throwable t) {
                //showLoadingIndicator(false);
                //Log.d("gson", "Petición rechazada:" + t.getMessage());
                //showErrorMessage("Error de comunicación");
            }
        });
    }
    private void mostrarDirecciones(List<Usuario_direccion> direccionesServer) {
        Log.d("direcciones","Tamanio de direcciones>"+direccionesServer.size());
        for(int i=0;i<direccionesServer.size();i++){
            Log.d("direcciones","Iddireccion en array>"+direccionesServer.get(i).getIdusuario_direccion());
            if(direccionesServer.get(i).getIdusuario_direccion().equals(orden.getUsuario_direccion_idusuario_direccion())){
                Log.d("direcciones", "Latitud: "+direccionesServer.get(i).getLatitud() + " Longitud: "+direccionesServer.get(i).getLongitud());
                Double latGuar = Double.valueOf(direccionesServer.get(i).getLatitud());
                Double lonGuar = Double.valueOf(direccionesServer.get(i).getLongitud());
                LatLng destino = new LatLng(latGuar, lonGuar);
                mMap.addMarker(new MarkerOptions().position(destino).title(direccionesServer.get(i).getCalle() + " " + direccionesServer.get(i).getNumero()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, 15));
            }
        }
    }

    private void mostrarDireccionesEmpty() {
    }
    */

}
