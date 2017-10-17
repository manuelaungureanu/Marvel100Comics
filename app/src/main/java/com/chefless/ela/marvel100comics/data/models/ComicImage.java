package com.chefless.ela.marvel100comics.data.models;
import java.io.Serializable;

/**
 * Created by ela on 09/10/2017.
 */

public class ComicImage implements Serializable {

    private String path;
    private String extension;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
