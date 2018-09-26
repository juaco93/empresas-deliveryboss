package deliveryboss.com.empresas.ui;

import android.app.SearchManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import deliveryboss.com.empresas.R;
import deliveryboss.com.empresas.data.adapter.OrdenesAdapter;
import deliveryboss.com.empresas.data.api.DeliverybossApi;
import deliveryboss.com.empresas.data.model.ApiResponseOrdenes;
import deliveryboss.com.empresas.data.model.Orden;
import deliveryboss.com.empresas.data.model.Roles;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.SEARCH_SERVICE;

public class OrdenesEntregadas extends Fragment {

    Context context;
    private RecyclerView mListaOrdenes;
    private OrdenesAdapter mOrdenesAdapter;
    private View mEmptyStateContainer;
    private TextView txtEmptyContainer;
    private Retrofit mRestAdapter;
    private DeliverybossApi mDeliverybossApi;
    List<Orden> serverOrdenes;
    String authorization;
    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean isOpen = false;
    private boolean isFirstOpening = true;

    String idempresaAdministrador;
    String logoEmpresaAdministrador;
    List<Roles> rolesUsuario;


    public OrdenesEntregadas() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_ordenes_entregadas, container, false);

        mListaOrdenes = (RecyclerView) v.findViewById(R.id.list_ordenes_entregadas);
        mOrdenesAdapter = new OrdenesAdapter(MainActivity.getmContext(), new ArrayList<Orden>(0));
        mListaOrdenes.setAdapter(mOrdenesAdapter);
        mOrdenesAdapter.setOnItemClickListener(new OrdenesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Orden clickedOrden) {
                //showInfoEstadoOrden((new Gson()).toJson(clickedOrden));
            }
        });
        mEmptyStateContainer = v.findViewById(R.id.empty_state_containerOrdenes);
        txtEmptyContainer = (TextView) v.findViewById(R.id.txtOrdenesEmptyContainer);

        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_content_ordenes_entregadas);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Pedir al servidor información reciente
                obtenerOrdenes();
            }
        });



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



        obtenerOrdenes();



        // Inflate the layout for this fragment
        return v;


    }

    @Override
    public void onResume() {
        super.onResume();

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_empresa_repartidor, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setQueryHint("Buscar orden...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (TextUtils.isEmpty(newText)) {
                    filtrarListaPorParametro();

                    //Volver a cargar lista sin filtrar
                } else {
                    buscar(newText);
                }
                return false;
            }
        });
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
                if(serverOrdenes.get(i).getNombre_empresa()!=null){
                    rubrosConcat+= (", "+serverOrdenes.get(i).getNombre_empresa());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void obtenerOrdenes(){
        authorization = "7777";

        Log.d("juaco93", "Recuperando Ordenes desde el Server");

        String rolesJson = SessionPrefs.get(getContext()).getPrefUsuarioEmpresaPorDefecto();
        Roles rolDefecto = new Gson().fromJson(rolesJson,Roles.class);
        idempresaAdministrador = rolDefecto.getIdempresa();

        // Realizar petición HTTP
        Call<ApiResponseOrdenes> call = mDeliverybossApi.obtenerOrdenesEmpresa(authorization,idempresaAdministrador);
        call.enqueue(new Callback<ApiResponseOrdenes>() {
            @Override
            public void onResponse(Call<ApiResponseOrdenes> call,
                                   Response<ApiResponseOrdenes> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {

                        //Log.d("gson", response.errorBody().toString());
                        //ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                        //error = apiError.getMessage();
                        //Log.d(TAG, apiError.getDeveloperMessage());
                    } else {
                        //Log.d("gson", response.errorBody().toString());
                        /*try {
                            // Reportar causas de error no relacionado con la API
                            Log.d(TAG, response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                    }
                    //showLoadingIndicator(false);
                    //showErrorMessage(error);
                    //Log.d("gson", response.message());
                    //Log.d("gson", response.raw().toString());
                    // Mostrar empty state
                    mostrarOrdenesEmpty();
                    return;
                }

                serverOrdenes = response.body().getDatos();
                Log.d("gson", "toido bien, recibido: " + response.body().getDatos().toString());
                if (serverOrdenes.size() > 0) {
                    // Mostrar lista de ordenes
                    mostrarOrdenes(serverOrdenes);
                    showLoadingIndicator(false);
                } else {
                    // Mostrar empty state
                    mostrarOrdenesEmpty();
                    showLoadingIndicator(false);
                }

                /*
                if(getContext().getApplicationInfo()!=null){
                    //Log.d("notinoti","Recibimos notificacion, ingresando a orden");
                    if(getIntent().getStringExtra("idorden")!=null){
                        int cant = serverOrdenes.size();
                        for(int i=0;i<cant;i++){
                            // Chequeamos el idorden para ver si esta en las listadas, y si está actuamos según el estado de la orden
                            // Si estado='confirmada' o estado='cancelada' mostramos el estado de la orden
                            // Si estado='entregada' entonces mostramos el dialogo para calificar la orden
                            if(serverOrdenes.get(i).getIdorden().equals(getIntent().getStringExtra("idorden"))){
                                if(getIntent().getStringExtra("estado").equals("entregada")){
                                    //if(serverOrdenes.get(i).getCalificado()==null)showDialogCalificar((new Gson()).toJson(serverOrdenes.get(i)));
                                }
                                if(getIntent().getStringExtra("estado").equals("confirmada")||getIntent().getStringExtra("estado").equals("cancelada")||getIntent().getStringExtra("estado").equals("anulada")||getIntent().getStringExtra("estado").equals("enviada")){
                                    //showInfoEstadoOrden((new Gson()).toJson(serverOrdenes.get(i)));
                                }
                            }
                        }
                    }
                }*/
            }

            @Override
            public void onFailure(Call<ApiResponseOrdenes> call, Throwable t) {
                //showLoadingIndicator(false);
                //Log.d("gson", "Petición rechazada:" + t.getMessage());
                showLoadingIndicator(false);
                //showErrorMessage("Error de comunicación");
            }
        });
    }

    private void mostrarOrdenes(List<Orden> ordenesServer) {
        Log.d("gson2", (new Gson().toJson(ordenesServer)));

        mOrdenesAdapter.swapItems(ordenesServer);
        mListaOrdenes.setVisibility(View.VISIBLE);
        mEmptyStateContainer.setVisibility(View.GONE);

        // Por defecto, la primera vez mostramos la lista filtrada
        if(isFirstOpening) {
            //filtrarListaPorParametro("en transito");
            isFirstOpening = false;
        }

        filtrarListaPorParametro();
    }

    private void mostrarOrdenesEmpty() {
        mListaOrdenes.setVisibility(View.GONE);
        mEmptyStateContainer.setVisibility(View.VISIBLE);
        txtEmptyContainer.setText(getResources().getString(R.string.mensajeSinOrdenes));
    }

    private void showLoadingIndicator(final boolean show) {
        swipeRefreshLayout.setRefreshing(show);
    }


    public void filtrarListaPorParametro(){
        //Log.d("juaco1993","Filtrando por: "+filtro);
        int cant=0;
        final List<Orden> filteredList = new ArrayList<>();

        for (int i = 0; i < serverOrdenes.size(); i++) {
            // Si la orden esta en estado "Enviada" o sea, en camino
            if (serverOrdenes.get(i).getOrden_estado_idorden_estado().equals("7")) {
                filteredList.add(serverOrdenes.get(i));
                cant++;
            }
        }
        mOrdenesAdapter.swapItems(filteredList);
        //if(cant<=0)mostrarOrdenesEmpty();
    }

}