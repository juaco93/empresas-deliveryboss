package deliveryboss.com.reparto.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import deliveryboss.com.reparto.R;
import deliveryboss.com.reparto.data.api.DeliverybossApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.ArrayList;
import java.util.List;


import deliveryboss.com.reparto.data.model.Orden;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import static android.content.Context.SEARCH_SERVICE;


public class InfoMenuFragment extends Fragment {

Context context;
private Retrofit mRestAdapter;
private DeliverybossApi mDeliverybossApi;
    public InfoMenuFragment() {
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

        View v = inflater.inflate(R.layout.fragment_info_menu, container, false);





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
                    //obtenerProductos("");

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
    /*
        for (int i = 0; i < serverProductos.size(); i++) {

            final String nombre = serverProductos.get(i).getProducto().toLowerCase();
            if (nombre.contains(query)) {
                filteredList.add(serverProductos.get(i));
            }
        }*/


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



}
