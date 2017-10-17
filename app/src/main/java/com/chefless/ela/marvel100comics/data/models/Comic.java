package com.chefless.ela.marvel100comics.data.models;

import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ela on 08/10/2017.
 */
public class Comic implements Serializable, Comparable {

    static final String PRINT_PRICE = "printPrice";
    static final String ONSALE_DATE = "onsaleDate";

    public Comic(String id, String title, int pageCount, ComicPrice[] prices) {
        this.id = id;
        this.title = title;
        this.pageCount = pageCount;
        this.prices = prices;
    }

    private String id;
    private String title;
    private ComicImage thumbnail;
    private String description;
    private int pageCount;
    private ComicPrice[] prices;
    private ComicCreator  creators;
    private ComicImage[] images;
    private ComicDate[] dates;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ComicImage getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(ComicImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public ComicPrice[] getPrices() {
        return prices;
    }

    public void setPrices(ComicPrice[] prices) {
        this.prices = prices;
    }

    public ComicCreator getCreators() {
        return creators;
    }

    public void setCreators(ComicCreator creators) {
        this.creators = creators;
    }

    public ComicImage[] getImages() {
        return images;
    }

    public void setImages(ComicImage[] images) {
        this.images = images;
    }

    public ComicDate[] getDates() {
        return dates;
    }

    public void setDates(ComicDate[] dates) {
        this.dates = dates;
    }

    public String getMainImageUrl(){

        if(this.images==null || this.images.length==0)
            return getThumbnailImageUrl();

        String url = String.format("%s.%s", this.images[0].getPath(), images[0].getExtension());
        if(!URLUtil.isValidUrl(url))
            return getThumbnailImageUrl();
        return url;
    }

    public String getThumbnailImageUrl(){

        if(this.thumbnail==null)
            return "";

        String url = String.format("%s.%s", this.thumbnail.getPath(), thumbnail.getExtension());
        if(!URLUtil.isValidUrl(url))
            return "";
        return url;
    }

    public Float getPrintPrice(){

        if(this.prices==null || this.prices.length==0)
            return 0f;

        for (ComicPrice price:this.prices) {
            if(price.getType().contentEquals(PRINT_PRICE))
                return price.getPrice();
        }
        return 0f;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Comic other = (Comic)o;
        return getPrintPrice().compareTo(other.getPrintPrice());
    }

    public String getSalesDate(){

        if(dates==null || dates.length==0)
            return "";

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());

        for (ComicDate comicDate:this.dates) {
            if(comicDate.getType().contentEquals(ONSALE_DATE)) {
                Date d = comicDate.parseComicDate();
                return d==null ? "" : sdf.format(d);
            }
        }
        return "";
    }
}
