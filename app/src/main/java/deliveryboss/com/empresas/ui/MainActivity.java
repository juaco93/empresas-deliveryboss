package deliveryboss.com.empresas.ui;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import deliveryboss.com.empresas.R;
import deliveryboss.com.empresas.data.adapter.OrdenesAdapter;
import deliveryboss.com.empresas.data.api.DeliverybossApi;
import deliveryboss.com.empresas.data.app.Config;
import deliveryboss.com.empresas.data.model.ApiResponseOrdenes;
import deliveryboss.com.empresas.data.model.CircleTransform;
import deliveryboss.com.empresas.data.model.Empresa_repartidor;
import deliveryboss.com.empresas.data.model.MessageEvent;
import deliveryboss.com.empresas.data.model.Orden;
import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RecyclerView mListaOrdenes;
    private OrdenesAdapter mOrdenesAdapter;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    InfoMenuFragment fragmentMenu = new InfoMenuFragment();
    OrdenesRechazadas fragmentRechazadas = new OrdenesRechazadas();
    OrdenesEntregadas fragmentEntregadas = new OrdenesEntregadas();


    List<Orden> serverOrdenes;
    String authorization;
    SearchView searchView;

    // CODIGO DEL NAV DRAWER
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout layoutContent;
    private RelativeLayout layoutButtons;
    private boolean isOpen = false;
    private boolean isFirstOpening = true;
    private DrawerLayout drawerLayout;
    private String drawerTitle;
    ImageView fotoPerfil;
    TextView userName;
    TextView userEmail;
    View rootView;
    String idempresaAdministrador;
    String logoEmpresaAdministrador;
    List<Roles> rolesUsuario;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int[] tabIcons = {
            R.mipmap.ic_list,
            R.drawable.ic_info_white_36dp,
            R.drawable.ic_star
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_main);


        // Inicio Toolbar
        // No creamos toolbar en la activity contenedora. Solo en cada fragment, para que
        // tengan busquedas y filtros separados por cada tab
        setToolbar();

        checkUserSession();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3-1);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        fotoPerfil = (ImageView) headerView.findViewById(R.id.circle_image);
        userName = (TextView) headerView.findViewById(R.id.userName);
        userEmail = (TextView) headerView.findViewById(R.id.userEmail);

        obtenerRoles();

        if (navigationView != null) {
            setupDrawerContent(navigationView);
            // Añadir carácteristicas

            String nombre = SessionPrefs.get(this).getPrefUsuarioNombreyApellido();
            String email = SessionPrefs.get(this).getPrefUsuarioEmail();
            //String imagen = SessionPrefs.get(this).getPrefUsuarioImagen();
            String imagen = logoEmpresaAdministrador;

            if(imagen!=null){
                if(!imagen.isEmpty()) {
                    Picasso.with(this).setLoggingEnabled(true);
                    Picasso
                            .with(this)
                            .load(imagen)
                            .fit()
                            .transform(new CircleTransform())
                            .into(fotoPerfil);
                }}
            userName.setText(nombre);
            userEmail.setText(email);

        }

    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(fragmentMenu, "A Resolver");
        adapter.addFragment(fragmentRechazadas, "Rechazadas");
        adapter.addFragment(fragmentEntregadas, "Entregadas");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private void setupTabIcons() {
        /*
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                // app icon in action bar clicked; go home
                //Intent intent = new Intent(this, PrincipalActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                //intent.putExtra("rubro",codRubro);
                //startActivity(intent);
                //   EventBus.getDefault().post(new MessageEvent("13", "Presionado Back"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Log.d("eventbus","evento recibido, descripcion: " + event.getDescripcion());

        // Si es 1, el usuario hizo click en "Ver mapa" en una orden
        if(event.getIdevento().equals("1")){
            //obtenerOrdenes();
            //Orden ordenSeleccionada = new Gson().fromJson(event.getDescripcion(), Orden.class);
            Intent intent = new Intent(this,MapsActivity.class);
            intent.putExtra("ordenJSON",event.getDescripcion());
            startActivity(intent);
        }
        if(event.getIdevento().equals("2")){

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }




    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            finishAffinity();
        }else {
            Toast.makeText(this, R.string.toastPresionarAtras, Toast.LENGTH_LONG).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }


    private void checkUserSession(){
        Log.d("sessioncheck","isLoggedIn? -->"+SessionPrefs.get(this).isLoggedIn());
        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootView = null;
        System.gc();
    }

    // A PARTIR DE ACA EMPIEZA CODIGO RELATIVO AL NAV DRAWER
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMenu);
        setSupportActionBar(toolbar);

        final android.support.v7.app.ActionBar ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.d("pruebas","Ingreso a onCreateOptionsMenu");
        /*
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_empresa_repartidor, menu);
            //return true;
        }*/


        return true;
    }

    public void buscar(String query){
        query = query.toString().toLowerCase();

        final List<Orden> filteredList = new ArrayList<>();
        String rubrosConcat ="";

        for (int i = 0; i < serverOrdenes.size(); i++) {

            if(serverOrdenes.get(i).getNombre()!=null){
                if(serverOrdenes.get(i).getNombre()!=null){
                    rubrosConcat= (serverOrdenes.get(i).getNombre());
                }
                if(serverOrdenes.get(i).getApellido()!=null){
                    rubrosConcat +=(", "+serverOrdenes.get(i).getApellido());
                }
                if(serverOrdenes.get(i).getCalle()!=null){
                    rubrosConcat +=(", "+serverOrdenes.get(i).getCalle());
                }
                if(serverOrdenes.get(i).getIdorden()!=null){
                    rubrosConcat +=(", "+serverOrdenes.get(i).getIdorden());
                }
            }

            final String nombre = serverOrdenes.get(i).getNombre().toLowerCase();
            final String rubro = rubrosConcat.toLowerCase();
            if (nombre.contains(query)|| rubro.contains(query)) {

                filteredList.add(serverOrdenes.get(i));
            }
        }
        mOrdenesAdapter.swapItems(filteredList);

    }

    public void filtrarListaPorParametro(String filtro){
        filtro = filtro.toString().toLowerCase();
        //Log.d("juaco1993","Filtrando por: "+filtro);
        int cant=0;

        final List<Orden> filteredList = new ArrayList<>();

        if(filtro.equals("en transito")) {
            for (int i = 0; i < serverOrdenes.size(); i++) {
                // Si la orden esta en estado "Enviada" o sea, en camino
                if (serverOrdenes.get(i).getOrden_estado_idorden_estado().equals("6")) {
                    filteredList.add(serverOrdenes.get(i));
                    cant++;
                }
            }
        }
        mOrdenesAdapter.swapItems(filteredList);
        //if(cant<=0)mostrarOrdenesEmpty();

    }


    public void showDialogEnviarMensaje(String orden) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EnviarMensajeFragment newFragment = new EnviarMensajeFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);
        newFragment.show(fragmentManager.beginTransaction(), "Enviar mensaje al cliente");

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Marcar item presionado
                        menuItem.setChecked(true);

                        //Hacer lo que tenga que hacer
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.nav_empresas:
                                // launch new intent instead of loading fragment
                                //startActivity(new Intent(MainActivity.this, MisOrdenesActivity.class));
                                break;
                            case R.id.nav_ayuda:
                                //startActivity(new Intent(PrincipalActivity.this, MisDireccionesActivity.class));
                                break;
                            case R.id.nav_sugerirempresa:
                                //startActivity(new Intent(PrincipalActivity.this, SugerirEmpresa.class));
                                break;
                            case R.id.nav_logout:
                                SessionPrefs.get(getApplicationContext()).logOut();
                                checkUserSession();
                                break;

                        }

                        // Desmarcar item presionado
                        menuItem.setChecked(false);
                        // Cerrar drawer
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    private void obtenerRoles(){
        String rolesJson = SessionPrefs.get(getApplicationContext()).getPrefUsuarioRoles();
        Log.d("juaco93",rolesJson);
        rolesUsuario = (new Gson().fromJson(rolesJson,  new TypeToken<List<Roles>>(){}.getType()));
        obtenerRoldeAdmin();
    }

    private void obtenerRoldeAdmin(){
        if(rolesUsuario!=null){
            if(rolesUsuario.size()>0){
                for(int i=0;i<rolesUsuario.size();i++){
                    if(rolesUsuario.get(i).getRol_tipo().equals("Administrador")){
                        idempresaAdministrador= rolesUsuario.get(i).getIdempresa();
                        logoEmpresaAdministrador = rolesUsuario.get(i).getLogo();
                    }
                }
            }
        }
    }

}

