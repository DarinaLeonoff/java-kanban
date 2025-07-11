package handlers;

import com.google.gson.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

//временная альтернатива замене Optional
public class OptionalAdapter<T> implements JsonSerializer<Optional<T>>, JsonDeserializer<Optional<T>> {

    @Override
    public Optional<T> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        if (!(typeOfT instanceof ParameterizedType parameterizedType)) {
            throw new JsonParseException("Expected Optional with generic type");
        }

        Type actualType = parameterizedType.getActualTypeArguments()[0];

        if (json == null || json.isJsonNull()) {
            return Optional.empty();
        }

        T value = context.deserialize(json, actualType);
        return Optional.ofNullable(value);
    }

    @Override
    public JsonElement serialize(Optional<T> src, Type typeOfSrc, JsonSerializationContext context) {
        return src.map(context::serialize).orElse(JsonNull.INSTANCE);
    }
}
