package deliveryboss.com.reparto.ui;

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
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import deliveryboss.com.reparto.R;
import deliveryboss.com.reparto.data.adapter.OrdenesAdapter;
import deliveryboss.com.reparto.data.api.DeliverybossApi;
import deliveryboss.com.reparto.data.app.Config;
import deliveryboss.com.reparto.data.model.ApiResponseOrdenes;
import deliveryboss.com.reparto.data.model.CircleTransform;
import deliveryboss.com.reparto.data.model.Empresa_repartidor;
import deliveryboss.com.reparto.data.model.MessageEvent;
import deliveryboss.com.reparto.data.model.Orden;
import deliveryboss.com.reparto.data.prefs.SessionPrefs;
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
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private int[] tabIcons = {
            R.mipmap.ic_list,
            R.drawable.ic_info_white_36dp,
            R.drawable.ic_star
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        //collapsing_container = (CollapsingToolbarLayout) findViewById(R.id.collapsing_container);
        //collapsing_container.setTitle(getResources().getString(R.string.title_activity_detalle_empresa));
        //collapsing_container.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3-1);
        /*
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        //mSharedFab.hide(); // Hide animation
                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        switch (viewPager.getCurrentItem()) {
                            case 0:
                                //fragmentCalificaciones.shareFab(null); // Remove FAB from fragment
                                fragmentInfoEmpresa.shareFab(null);
                                //fragmentMenu.shareFab(mSharedFab); // Share FAB to new displayed fragment
                                //mSharedFab.show();
                                EventBus.getDefault().post(new MessageEvent("11", "Tab en menu"));
                                break;
                            case 1:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                            case 2:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                            default:
                                fragmentMenu.shareFab(null); // Remove FAB from fragment
                                //fragmentCalificaciones.shareFab(null); // Share FAB to new displayed fragment
                                fragmentInfoEmpresa.shareFab(null);
                                EventBus.getDefault().post(new MessageEvent("12", "Tab NO en menu"));
                                break;
                        }
                        //mSharedFab.show(); // Show animation
                        break;
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
        });*/

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(fragmentMenu, "A Resolver");
/*        adapter.addFragment(fragmentInfoEmpresa, "Informacion");
        adapter.addFragment(fragmentCalificaciones, "Calificacion");
  */      viewPager.setAdapter(adapter);

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
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
       /* tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
        //Log.d("sessioncheck","isLoggedIn? -->"+SessionPrefs.get(this).isLoggedIn());
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            getMenuInflater().inflate(R.menu.menu_empresa_repartidor, menu);
            //return true;
        }
        if(MenuItemCompat.getActionView(menu.findItem(R.id.action_search))!=null)searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MenuItem checkboxEnTransito = menu.getItem(1);
        checkboxEnTransito.setChecked(true);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    //obtenerOrdenes();
                } else {
                    buscar(newText);
                }
                return false;
            }
        });
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

}

