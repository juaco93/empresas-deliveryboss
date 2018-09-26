package deliveryboss.com.empresas.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import deliveryboss.com.empresas.R;
import deliveryboss.com.empresas.data.api.DeliverybossApi;
import deliveryboss.com.empresas.data.app.Config;
import deliveryboss.com.empresas.data.model.ApiResponseDirecciones;
import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.model.Usuario_direccion;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import deliveryboss.com.empresas.data.util.Utilidades;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SeleccionarEmpresa extends AppCompatActivity {

    Spinner spDirecciones;
    CardView btnContinuar;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    List<Usuario_direccion> serverDirecciones;
    Roles rolElegido;
    String idempresaAdministrador;
    String logoEmpresaAdministrador;
    List<Roles> rolesUsuario;
    List<Roles> rolesUsuarioAdmin;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String stDireccion;
    String authorization;
    //Button btnAgregarDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_empresa);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        spDirecciones = (Spinner) findViewById(R.id.spSeleccionarEmpresa);
        btnContinuar = (CardView) findViewById(R.id.btnSeleccionarEmpresaContinuar);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spDirecciones.getSelectedItem().equals("Seleccioná tu dirección")){
                    if(spDirecciones.getSelectedItem().equals("No tenés direcciones guardadas")){
                        //Intent intent = new Intent(SeleccionarEmpresa.this, MainActivity.class);
                        //startActivity(intent);
                        //finish();
                    }else {
                        Intent intent = new Intent(SeleccionarEmpresa.this, MainActivity.class);

                        // Guardar usuario en preferencias
                        //SessionPrefs.get(SeleccionarDireccion.this).saveDireccion(direccionElegida.getIdusuario_direccion(), direccionElegida.getCiudad_idciudad(), direccionElegida.getCiudad(), direccionElegida.getCalle(), direccionElegida.getNumero(), direccionElegida.getLatitud(), direccionElegida.getLongitud());
                        Utilidades.setearEmpresaPorDefecto(SeleccionarEmpresa.this,(new Gson()).toJson(rolElegido));
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(SeleccionarEmpresa.this, "Seleccioná tu dirección para continuar", Toast.LENGTH_LONG).show();
                }
            }
        });

        obtenerRoles();

        /// SPINNER DE EMPRESAS
        spDirecciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    rolElegido = rolesUsuarioAdmin.get(position-1);
                    suscribirseATopic(rolElegido.getIdempresa());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkUserSession();

        //// RETROFIT
        // Interceptor para log del Request
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Inicializar GSON
        Gson gson =
                new GsonBuilder()
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);


        //obtenerDirecciones();

    }


    private void mostrarEmpresas(List<Roles> roles) {
        Log.d("juacoRoles", "Cantidad de roles en spinner-->"+roles.size());

        // String[] items = new String[direccionesServer.size()];
        String[] items = new String[roles.size()+1];

        int c=1;
        items[0] = "Seleccioná tu empresa";
        //Traversing through the whole list to get all the names
        for(int i=0; i<roles.size(); i++){
            //Storing names to string array

            items[c] = roles.get(i).getNombre_empresa() + "->" + roles.get(i).getRol_tipo();
            c++;

            //items[i] = direccionesServer.get(i).getCalle() + " " + direccionesServer.get(i).getNumero();
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //setting adapter to spinner
        spDirecciones.setAdapter(adapter);

    }

    private void mostrarEmpresasEmpty() {
        String[] items = new String[1];

        //Traversing through the whole list to get all the names
        for(int i=0; i<items.length; i++){
            //Storing names to string array
            items[i] =  "No tenés roles asignados";
        }

        //Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //setting adapter to spinner
        spDirecciones.setAdapter(adapter);
    }

    private void checkUserSession(){
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }



    private void obtenerRoles(){
        String rolesJson = SessionPrefs.get(this).getPrefUsuarioRoles();
        Log.d("juacoRoles","Roles guardados en prefs->"+rolesJson);
        rolesUsuario = (new Gson().fromJson(rolesJson,  new TypeToken<List<Roles>>(){}.getType()));
        obtenerRoldeAdmin();
    }

    private void obtenerRoldeAdmin(){
        rolesUsuarioAdmin = new ArrayList<>();
        if(rolesUsuario!=null){
            if(rolesUsuario.size()>0){
                for(int i=0;i<rolesUsuario.size();i++){
                    if(rolesUsuario.get(i).getRol_tipo().equals("Administrador")){
                        idempresaAdministrador= rolesUsuario.get(i).getIdempresa();
                        logoEmpresaAdministrador = rolesUsuario.get(i).getLogo();
                        rolesUsuarioAdmin.add(rolesUsuario.get(i));
                        SessionPrefs.get(this).saveEmpresaPorDefecto(rolesUsuario.get(i));
                    }
                }

                if(rolesUsuarioAdmin!=null){
                    if(rolesUsuarioAdmin.size()>0){
                        mostrarEmpresas(rolesUsuarioAdmin);
                    }else{
                        mostrarEmpresasEmpty();
                    }
                }
            }
        }
    }


    private void suscribirseATopic(String idempresa) {

        //// PARTE DE MENSAJERIA VIA FCM
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    //displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        //displayFirebaseRegId();

        // REGISTRACION EN TOPICS //
        Log.d("regId","Dispositivo registrado en los topics correspondientes");
        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_EMPRESAS);
        FirebaseMessaging.getInstance().subscribeToTopic(idempresa);

        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, 0);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.d("regId", "Firebase reg id: " + regId);

    }

}
