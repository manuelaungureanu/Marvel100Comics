package com.chefless.ela.marvel100comics.data.models;

import android.text.TextUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ela on 17/10/2017.
 */

public class ComicDate implements Serializable {

    private String type;
    private String date;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Date parseComicDate(){

        if(TextUtils.isEmpty(date))
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        try {
            return sdf.parse(this.date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
