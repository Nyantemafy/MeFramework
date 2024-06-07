package mg.itu.prom16;

import java.util.HashMap;

public class ModelView {
    private String url;
    private HashMap<String, Object> data;

    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }

    public String getUrl() {
        return url;
    }

    public HashMap<String, Object> getData() {
        return data;
    }

    public void addObject(String key, Object value) {
        data.put(key, value);
    }
}
