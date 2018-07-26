package deliveryboss.com.reparto.data.adapter;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import deliveryboss.com.reparto.R;
import deliveryboss.com.reparto.data.api.DeliverybossApi;
import deliveryboss.com.reparto.data.model.ApiResponse;
import deliveryboss.com.reparto.data.model.MessageEvent;
import deliveryboss.com.reparto.data.model.Orden;
import deliveryboss.com.reparto.data.model.Orden_detalle;
import deliveryboss.com.reparto.data.prefs.SessionPrefs;
import deliveryboss.com.reparto.ui.EnviarMensajeFragment;
import deliveryboss.com.reparto.ui.MainActivity;
import deliveryboss.com.reparto.ui.MapsActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class OrdenesAdapter extends RecyclerView.Adapter<OrdenesAdapter.ViewHolder> {
    private Context context;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.orden_item_list, parent, false);
        this.context = mContext;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Orden orden = mItems.get(position);
        holder.idorden.setText("Orden #" + orden.getIdorden());
        holder.nombreEmpresa.setText(orden.getNombre_empresa());
        holder.nombreCliente.setText(orden.getNombre() + " " + orden.getApellido());
        holder.fecha.setText(orden.getFecha_hora());
        holder.estado.setText(orden.getEstado());
        holder.direccion.setText(orden.getCalle() + " " +orden.getNumero() + " " + orden.getHabitacion() + " - Bº" + orden.getBarrio());
        holder.monto.setText("Total: $"+ orden.getImporte_total());
        holder.pagaCon.setText("Paga con: $"+orden.getPaga_con());

        if(orden.getEstado().equals("Confirmada")||orden.getEstado().equals("Terminada")||orden.getEstado().equals("Enviada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenConfirmada));
        if(orden.getEstado().equals("Pendiente"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenPendiente));
        if(orden.getEstado().equals("Cancelada")||orden.getEstado().equals("Anulada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenCancelada));
        if(orden.getEstado().equals("Entregada"))holder.estado.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOrdenEntregada));
        //holder.info_estado.setText(orden.getInfo_estado());

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
        if(orden.getEstado().equals("En tránsito")) {
            holder.marcarComoEntregada.setTextColor(Color.BLACK);
            holder.marcarComoEntregada.setOnClickListener(new View.OnClickListener() {
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
        }else{
            holder.marcarComoEntregada.setTextColor(Color.GRAY);
        }

        //METODO PARA ENVIAR MENSAJE
        holder.verMapa.setTextColor(Color.BLACK);
        holder.enviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        public TextView marcarComoEntregada;


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
            marcarComoEntregada = (TextView) itemView.findViewById(R.id.txtOrdenMarcarComoEntregada);

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

    private void marcarOrdenComoEntregada(Orden orden){
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

        Log.d("entregada","Modificando el estado de la Orden");

        String authorization = SessionPrefs.get(context).getPrefUsuarioToken();
        String idorden = orden.getIdorden();

        List<Orden_detalle> vacia = null;
        //vacia.add(new Orden_detalle("","","","","","","",""));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fechaHora = sdf.format(new Date());
        String recibida="1";

        //Orden ordenMod = new Orden(idorden,"","","","","","","","","","","","","1","","","","",recibida,fechaHora,"","","","","","",vacia);
        Orden ordenMod = orden;
        ordenMod.setRecibida("1");
        ordenMod.setRecibida_fecha_hora(fechaHora);

        Log.d("entregada",(new Gson()).toJson(ordenMod));
        showErrorMessage("Orden entregada");

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
                            Log.d("entregada", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            // Reportar causas de error no relacionado con la API
                            Log.d("entregada", response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return;
                }
                Log.d("entregada", "Respuesta del SV:" + response.body().getMensaje());
                //  showErrorMessage(response.body().getMensaje());
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //showLoadingIndicator(false);
                Log.d("entregada", "Petición rechazada:" + t.getMessage());
                showErrorMessage("Comprueba tu conexión a Internet");
            }
        });
    }


    private void showErrorMessage(String error) {
        Toast.makeText(mContext, error, Toast.LENGTH_LONG).show();
    }
}
