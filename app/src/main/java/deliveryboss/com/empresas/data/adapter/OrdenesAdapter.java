package deliveryboss.com.empresas.data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import deliveryboss.com.empresas.R;
import deliveryboss.com.empresas.data.api.DeliverybossApi;
import deliveryboss.com.empresas.data.model.ApiResponse;
import deliveryboss.com.empresas.data.model.ApiResponseOrdenesEstadoInformacion;
import deliveryboss.com.empresas.data.model.MessageEvent;
import deliveryboss.com.empresas.data.model.Orden;
import deliveryboss.com.empresas.data.model.Orden_detalle;
import deliveryboss.com.empresas.data.model.Orden_estado_informacion;
import deliveryboss.com.empresas.data.prefs.SessionPrefs;
import deliveryboss.com.empresas.ui.CambiarEstadoOrdenFragment;
import deliveryboss.com.empresas.ui.EnviarMensajeFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OrdenesAdapter extends RecyclerView.Adapter<OrdenesAdapter.ViewHolder> {
    private Context context;
    final static String[] Estados0 = new String[] { "Acciones" };
    final static String[] Estados1 = new String[] { "Acciones","Confirmar", "Cancelar" };
    final static String[] Estados2 = new String[] { "Acciones","Asignar a delivery"};
    final static String[] Estados3 = new String[] { "Acciones","Entregar" };
    String opciones = null;
    List <Orden_estado_informacion> serverEstados;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.orden_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final String estado="";
        final String fechaHora="";

        final Orden orden = mItems.get(position);
        holder.idorden.setText("Orden #" + orden.getIdorden());
        holder.nombreEmpresa.setText(orden.getNombre_empresa());
        holder.nombreCliente.setText(orden.getNombre() + " " + orden.getApellido());
        holder.fecha.setText(orden.getFecha_hora());
        holder.estado.setText(orden.getEstado());
        holder.direccion.setText(orden.getCalle() + " " +orden.getNumero() + " " + orden.getHabitacion() + " - Bº" + orden.getBarrio());
        holder.monto.setText("Total: $"+ orden.getImporte_total());
        holder.pagaCon.setText("Paga con: $"+orden.getPaga_con());


        if(orden.getEstado().equals("Confirmada")||orden.getEstado().equals("Terminada")||orden.getEstado().equals("Enviada"))
        {
            holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenConfirmada));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, Estados2);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.cambiarEstado.setAdapter(adapter);

        }
        if(orden.getEstado().equals("Pendiente"))
        {
            holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenPendiente));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, Estados1);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.cambiarEstado.setAdapter(adapter);
        }
        if(orden.getEstado().equals("En tránsito"))
        {
            holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenPendiente));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, Estados3);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.cambiarEstado.setAdapter(adapter);
        }
        if(orden.getEstado().equals("Cancelada")||orden.getEstado().equals("Anulada"))
        {
            holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenCancelada));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, Estados0);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.cambiarEstado.setEnabled(false);
            holder.cambiarEstado.setAdapter(adapter);
        }
        if(orden.getEstado().equals("Entregada"))
        {
            holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenEntregada));
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, Estados0);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.cambiarEstado.setEnabled(false);
            holder.cambiarEstado.setAdapter(adapter);
        }

        holder.cambiarEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(holder.cambiarEstado.getSelectedItem()!=null){
                    String ordenJson = new Gson().toJson(orden);
                    if(holder.cambiarEstado.getSelectedItem().toString().equals("Cancelar"))
                        showDialogCambiarEstado(ordenJson,"2",opciones);
                    if(holder.cambiarEstado.getSelectedItem().toString().equals("Confirmar"))
                        showDialogCambiarEstado(ordenJson,"3",opciones);
                    if(holder.cambiarEstado.getSelectedItem().toString().equals("Asignar a delivery"))
                        showDialogCambiarEstado(ordenJson,"6",opciones);
                    if(holder.cambiarEstado.getSelectedItem().toString().equals("Entregar"))
                        showDialogCambiarEstado(ordenJson,"7",opciones);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int i = 0;
        String previo = "";
        while (i < orden.getOrden_detalle().size()) {
            previo += orden.getOrden_detalle().get(i).getCantidad() + " " + orden.getOrden_detalle().get(i).getProducto_nombre() + "\n";
            i++;
        }
        holder.orden_detalle.setText(previo);


        //METODO PARA QUE PUEDA LLAMAR UNICAMENTE CUANDO ESTE DENTRO DE LAS 2H SIGUIENTES A REALIZADO EL PEDIDO
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date fechaOrden = null;
        try {
            fechaOrden = formatter.parse(orden.getFecha_hora());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar limiteLlamada = Calendar.getInstance();
        Calendar limiteCalificacion = Calendar.getInstance();
        limiteCalificacion.setTime(fechaOrden);
        limiteCalificacion.add(Calendar.HOUR,168);    //limite para calificar 168h = 1 semana
        limiteLlamada.setTime(fechaOrden);
        limiteLlamada.add(Calendar.HOUR, 24);         //agregamos 24h a partir de realizada la orden como nuestro "limite" para llamar
        Calendar ahora = Calendar.getInstance();

        int diffLlamada = ahora.compareTo(limiteLlamada); // da 0 si es igual, menor a 0 si now es menor a su argumento y mayor a 0 si es mayor
        final int diffCalificacion = ahora.compareTo(limiteCalificacion);
        if(diffLlamada<0){
            //holder.verMapa.setTextColor(Color.BLACK);

        }else{
            //holder.enviarMensaje.setTextColor(Color.LTGRAY);
        }

        //METODO PARA MARCAR COMO ENTREGADA (Solo cuando esta en transito, sino deshabilitado)
        /*
        if(orden.getEstado().equals("En tránsito")) {
            holder.cambiarEstado.setTextColor(Color.BLACK);
            holder.cambiarEstado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("¿Marcar orden como ENTREGADA?");
                    builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            marcarOrdenComoEntregada(orden);
                        }
                    });
                    builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        if(orden.getEstado().equals("Pendiente")) {

            }


            else{
            holder.cambiarEstado.setTextColor(Color.GRAY);
        }*/

        //METODO PARA ENVIAR MENSAJE
        holder.verMapa.setTextColor(Color.BLACK);
        holder.enviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showDialog(new Gson().toJson(orden));
                showDialog(new Gson().toJson(orden));
            }
        });

        //METODO PARA BOTON VER MAPA
            holder.verMapa.setText("VER MAPA");
            holder.verMapa.setTextColor(Color.BLACK);
            holder.verMapa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String ordenJson = new Gson().toJson(orden);
                    EventBus.getDefault().post(new MessageEvent("1", ordenJson));
                }
            });


        //METODO PARA INFORMACION DEL ESTADO DE LA ORDEN
        holder.btnOrdenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showInfoEstadoOrden((new Gson()).toJson(orden));
            }
        });


    }

    public void showDialog(String orden) {
        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        EnviarMensajeFragment newFragment = new EnviarMensajeFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Enviar mensaje");
    }

    public void showDialogCambiarEstado(String orden, String valorEstado, String opciones) {
        android.support.v4.app.FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        CambiarEstadoOrdenFragment newFragment = new CambiarEstadoOrdenFragment();

        Bundle args = new Bundle();
        if(!orden.isEmpty() && !orden.equals(""))args.putString("orden", orden);
        if(!valorEstado.isEmpty() && !valorEstado.equals(""))args.putString("valorEstado", valorEstado);
        if(opciones!=null){
            if(!opciones.isEmpty() && !opciones.equals(""))args.putString("opciones", opciones);
        }
        newFragment.setArguments(args);

        newFragment.show(fragmentManager.beginTransaction(), "Cabmiar Estado Orden");
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private List<Orden> mItems;
    private Context mContext;

    public OrdenesAdapter(Context context, List<Orden> items) {
        mItems = items;
        mContext = context;
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Orden clickedOrden);
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView idorden;
        public TextView nombreEmpresa;
        public TextView nombreCliente;
        public TextView fecha;
        public TextView orden_detalle;
        public TextView estado;
        //public TextView info_estado;
        public ImageView btnOrdenInfo;
        public TextView direccion;
        public TextView monto;
        public TextView pagaCon;
        public TextView verMapa;
        public TextView enviarMensaje;
        //public TextView cambiarEstado;
        public Spinner cambiarEstado;



        public ViewHolder(View itemView) {
            super(itemView);
            idorden = (TextView) itemView.findViewById(R.id.txtOrdenIdOrden);
            nombreEmpresa = (TextView) itemView.findViewById(R.id.txtOrdenNombreEmpresa);
            nombreCliente = (TextView) itemView.findViewById(R.id.txtOrdenNombreCliente);
            fecha = (TextView) itemView.findViewById(R.id.txtOrdenFechaHora) ;
            orden_detalle = (TextView) itemView.findViewById(R.id.txtOrdenDetalle);
            estado = (TextView) itemView.findViewById(R.id.txtOrdenEstado);
            //info_estado = (TextView) itemView.findViewById(R.id.txtOrdenInfoEstado);
            btnOrdenInfo = (ImageView) itemView.findViewById(R.id.btnOrdenInfo);
            direccion = (TextView) itemView.findViewById(R.id.txtOrdenDireccion);
            monto = (TextView) itemView.findViewById(R.id.txtOrdenDetalleMonto);
            pagaCon = (TextView) itemView.findViewById(R.id.txtOrdenDetallePagaCon);
            verMapa = (TextView) itemView.findViewById(R.id.txtOrdenVerMapa);
            enviarMensaje = (TextView) itemView.findViewById(R.id.txtOrdenLlamar);
            cambiarEstado = (Spinner) itemView.findViewById(R.id.txtOrdenCambiarEstado);

            obtenerEstadosOrdenes();

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                mOnItemClickListener.onItemClick(mItems.get(position));
            }
        }
    }

    public void swapItems(List<Orden> ordenes) {
        if (ordenes == null) {
            mItems = new ArrayList<>(0);
        } else {
            mItems = ordenes;
        }
        notifyDataSetChanged();
    }

    private void obtenerEstadosOrdenes(){
        Retrofit mRestAdapter;
        DeliverybossApi mDeliverybossApi;

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


        String authorization = SessionPrefs.get(context).getPrefUsuarioToken();

        Log.d("juaco93", "Descargados estados de ordenes");

        // Realizar petición HTTP
        Call<ApiResponseOrdenesEstadoInformacion> call = mDeliverybossApi.obtenerEstadosOrdenes(authorization);
        call.enqueue(new Callback<ApiResponseOrdenesEstadoInformacion>() {
            @Override
            public void onResponse(Call<ApiResponseOrdenesEstadoInformacion> call,
                                   Response<ApiResponseOrdenesEstadoInformacion> response) {
                if (!response.isSuccessful()) {
                    // Procesar error de API
                    String error = "Ha ocurrido un error. Contacte al administrador";
                    if (response.errorBody()
                            .contentType()
                            .subtype()
                            .equals("json")) {
                    } else {
                    }
                    // Mostrar empty state
                    //mostrarOrdenesEmpty();
                    return;
                }

                serverEstados = response.body().getDatos();
                Log.d("gson", "toido bien, recibido: " + response.body().getDatos().toString());
                if (serverEstados.size() > 0) {
                    // Mostrar lista de ordenes
                    //mostrarOrdenes(serverEstados);
                   opciones = new Gson().toJson(serverEstados);
                } else {
                    // Mostrar empty state
                    //mostrarOrdenesEmpty();
                }

            }

            @Override
            public void onFailure(Call<ApiResponseOrdenesEstadoInformacion> call, Throwable t) {
                //showLoadingIndicator(false);
            }
        });
    }

    private void cambiarEstadoOrden(Orden orden, String idEstado){
        Retrofit mRestAdapter;
        DeliverybossApi mDeliverybossApi;

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

        Log.d("juaco93","Modificando el estado de la Orden");

        String authorization = SessionPrefs.get(context).getPrefUsuarioToken();
        String idorden = orden.getIdorden();

        List<Orden_detalle> vacia = null;
        //vacia.add(new Orden_detalle("","","","","","","",""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());

        //Orden ordenMod = new Orden(idorden,"","","","","","","","","","","","","1","","","","",recibida,fechaHora,"","","","","","",vacia);
        Orden ordenMod = orden;

        ordenMod.setOrden_estado_idorden_estado(idEstado);
        ordenMod.setFecha_hora_estado(fechaHora);

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


    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }
}
