package deliveryboss.com.empresas.data.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Joaquin on 19/06/2018.
 */

public class MiDeserializador<T> implements JsonDeserializer<Empresa_repartidor> {

    @Override
    public Empresa_repartidor deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Get the "content" element from the parsed JSON
        JsonElement empresa_repartidor = json.getAsJsonObject().get("empresa_repartidor");

        // Deserialize it. You use a new instance of Gson to avoid infinite recursion
        // to this deserializer
        return new Gson().fromJson(empresa_repartidor, typeOfT);
    }
}
