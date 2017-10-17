package com.chefless.ela.marvel100comics.data.models;

import java.io.Serializable;

/**
 * Created by ela on 09/10/2017.
 */

public class ComicCreator implements Serializable {

    public class ComicCreatorItem implements Serializable{
        public String resourceURI;
        public String name;
        public String role;
    }

    private int returned;
    private ComicCreatorItem[] items;

    public int getReturned() {
        return returned;
    }

    public void setReturned(int returned) {
        this.returned = returned;
    }

    public ComicCreatorItem[] getItems() {
        return items;
    }

    public void setItems(ComicCreatorItem[] items) {
        this.items = items;
    }
}


