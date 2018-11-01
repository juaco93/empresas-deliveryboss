package deliveryboss.com.empresas.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import deliveryboss.com.empresas.data.model.*;
import deliveryboss.com.empresas.data.model.Usuario_reparto;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private TextInputLayout mFloatLabelEmail;
    private TextInputLayout mFloatLabelPassword;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;

    String idempresaAdministrador ="";
    String logoEmpresaAdministrador;
    List<Roles> rolesUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Remover título de la action bar
        //getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Iniciar sesión");

        Gson gson =
                new GsonBuilder()
                        .registerTypeAdapter(Empresa_repartidor.class, new MiDeserializador<Empresa_repartidor>())
                        .create();

        // Crear conexión al servicio REST
        mRestAdapter = new Retrofit.Builder()
                .baseUrl(DeliverybossApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        // Crear conexión a la API de Deliveryboss
        mDeliverybossApi = mRestAdapter.create(DeliverybossApi.class);

        mEmailView = (EditText) findViewById(R.id.txtEmail);
        mPasswordView = (EditText) findViewById(R.id.txtPassword);
        mFloatLabelEmail = (TextInputLayout) findViewById(R.id.float_label_email);
        mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mFloatLabelEmail.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        String e_mail = mEmailView.getText().toString();
        String contrasena = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(contrasena) && !isPasswordValid(contrasena)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(e_mail)) {
            mEmailView.setError(getString(R.string.error_field_required));
            mFloatLabelEmail.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(e_mail)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mFloatLabelEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            //showProgress(true);
            Call<ApiResponseLogin> loginCall = mDeliverybossApi.loginUsuarioNuevo(new LoginBody(e_mail, contrasena));
            loginCall.enqueue(new Callback<ApiResponseLogin>() {
                @Override
                public void onResponse(Call<ApiResponseLogin> call, Response<ApiResponseLogin> response) {
                    // Ocultar progreso
                    //showProgress(false);
                    // Procesar errores
                    if (!response.isSuccessful()) {
                        String error;
                        if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("application/json")) {
                            Log.d("logindb", "se recibio respuesta json (con error): " + response.errorBody().toString());
                        } else {
                            error = response.message();
                            Log.d("logindb", "hubo un error: " + response.message());
                        }

                        showLoginError("Usuario o contraseña incorrectos");
                        return;
                    }

                    //
                    Log.d("logindb", "RAW: " + response.raw().toString());
                    //Log.d("logindb", "BODY: " + (response.body().toString()));
                    Log.d("logindb", "GSON: " + (new Gson()).toJson(response.body().toString()));
                    Log.d("logindb", "Nombre: " + response.body().getUsuario().getNombre());
                    Log.d("logindb", "Apellido: " + response.body().getUsuario().getApellido());
                    Log.d("logindb", "Logueado, Token: " + response.body().getUsuario().getToken());

                    // Guardar usuario en preferencias
                    SessionPrefs.get(LoginActivity.this).saveUsuario(response.body().getUsuario());

                    obtenerRoles();


                    if(SessionPrefs.get(LoginActivity.this).getPrefUsuarioEmpresaPorDefecto()!=null){
                        // Ir a la pantalla principal
                        mostrarPantallaPerfilUsuario();
                        // Guardar el regId para enviar notificaciones en la BD
                        displayFirebaseRegId();
                    }else{
                        showLoginError("Tu usuario no es Administrador!");
                    }


                }

                @Override
                public void onFailure(Call<ApiResponseLogin> call, Throwable t) {
                    //showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 3;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Integer> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            if (!mEmail.equals("")) {
                return 2;
            }

            if (!mPassword.equals("")) {
                return 3;
            }

            return 1;
        }

        @Override
        protected void onPostExecute(final Integer success) {
            mAuthTask = null;
            //showProgress(false);

            switch (success) {
                case 1:
                    showPrincipalScreen();
                    break;
                case 2:
                case 3:
                    showLoginError("Número de identificación o contraseña inválidos");
                    break;
                case 4:
                    showLoginError(getString(R.string.error_server));
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            //showProgress(false);
        }
    }

    private void showPrincipalScreen() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                Intent intent = new Intent(this,LoginActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {

        //final String idEmpresa = SessionPrefs.get(this).getPrefUsuarioEmpresaIdempresa();
        //

        String rolesCad = SessionPrefs.get(this).getPrefUsuarioRoles();
        String[] roles = rolesCad.split(",");

        for(int i=0; i<roles.length;i++){
            String[] tipo_rol = roles[i].split("-");
            if(tipo_rol[0].equals("Administrador")){
                idempresaAdministrador=tipo_rol[1];
            }
        }
        Log.d("regId","idempresa--topic-->"+idempresaAdministrador);

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
        FirebaseMessaging.getInstance().subscribeToTopic(idempresaAdministrador);
        //

        //String idusuario = SessionPrefs.get(this).getPrefUsuarioCiudad();
        //String regId = SessionPrefs.get(LoginActivity.this).getPrefUsuarioRegId();
        SharedPreferences pref1 = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, 0);
        //String idusuario = pref1.getString(SessionPrefs.PREF_EMPRESA_IDEMPRESA,null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.d("regId", "Firebase reg id: " + regId);
        //Log.d("regId", "Firebase idusuario: " + idusuario);
/*
        Call<ApiResponse> call = mDeliverybossApi.registrarRegId(new regIdBody(regId,idusuario));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                // Ocultar progreso
                // Procesar errores
                if (!response.isSuccessful()) {
                    String error;
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("application/json")) {
                        //Log.d("regId", "se recibio respuesta json (con error): " + response.errorBody().toString());
                    } else {
                        //POR ARQUITECTURA DEL SERVER NO PUEDE HABER EMAIL REPETIDO, ENTONCES INTENTAMOS LOGUEAR SI YA ESTA REGISTRADO
                        //Log.d("regId", "hubo un error, no pude registrar el RegId en la BD");
                        //FacebookLogin(e_mail, idfacebook);
                    }
                    return;
                }
                //Log.d("regId", "Registre correctamente el RegId en la BD");
                //Log.d("regId", "RAW: " + response.raw().toString());
                //Log.d("regId", "Parseada: " + response.body().getMensaje());
                //Log.d("logindb", "Logueado, Token: " + response.body().getToken());

                // Guardar usuario en preferencias
                //SessionPrefs.get(LoginActivity.this).saveUsuario(response.body());

                // Ir a la pantalla principal
                //FacebookLogin(e_mail,idfacebook);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    */
    }

    public void mostrarPantallaPerfilUsuario(){
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

/*
    public boolean chequearRoles(Usuario_logueado usuario){
        List<Roles> rolesDelUsuarioLogueado = new ArrayList<>();
        int c=0;
        if(usuario.getRoles()!=null){
            for(int i=0; i<usuario.getRoles().size();i++){
                if(usuario.getRoles().get(i).getRol_tipo().equals("Administrador")){
                    rolesDelUsuarioLogueado.add(new Roles(String.valueOf(c),usuario.getIdusuario(),usuario.getRoles().get(i).getIdempresa(),"Administrador",usuario.getRoles().get(i).getLogo()));
                }
            }
        }

        if(rolesDelUsuarioLogueado!=null){
            if(rolesDelUsuarioLogueado.size()>0){
                return true;
            }else return false;
        }else return false;
    }

    */

    private void obtenerRoles(){
        Boolean esAdmin= false;
        String rolesJson = SessionPrefs.get(getApplicationContext()).getPrefUsuarioRoles();
        Log.d("juaco93",rolesJson);
        rolesUsuario = (new Gson().fromJson(rolesJson,  new TypeToken<List<Roles>>(){}.getType()));

        if(rolesUsuario!=null){
            if(rolesUsuario.size()>0){
                for(int i=0;i<rolesUsuario.size();i++){
                    if(rolesUsuario.get(i).getRol_tipo().equals("Administrador")|| rolesUsuario.get(i).getRol_tipo().equals("Supervisor")){
                        SessionPrefs.get(getApplicationContext()).saveEmpresaPorDefecto(rolesUsuario.get(i));
                        idempresaAdministrador= rolesUsuario.get(i).getIdempresa();
                        logoEmpresaAdministrador = rolesUsuario.get(i).getLogo();
                    }
                }
            }
        }
    }

}
