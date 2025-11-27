package model;

import java.util.HashMap;
import java.util.Map;

public class ModelVue {
    private String vue;
    private Map<String, Object> data = new HashMap<>();

    public ModelVue() {}

    public ModelVue(String vue) {
        this.vue = vue;
    }

    public String getVue() {
        return vue;
    }

    public void setVue(String vue) {
        this.vue = vue;
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public Map<String, Object> getData() {
        return data;
    }
}
