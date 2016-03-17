package kyvent;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonHelper<T> {
    private final ObjectMapper mapper;
    public JsonHelper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
    public T fromJson(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }
}
