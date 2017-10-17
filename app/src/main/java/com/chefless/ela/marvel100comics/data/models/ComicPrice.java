package com.chefless.ela.marvel100comics.data.models;
import java.io.Serializable;

/**
 * Created by ela on 09/10/2017.
 */
public class ComicPrice implements Serializable {

    private String type;
    private float price;

    public ComicPrice(String type, float price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
