package com.valdemar.model;

/**
 * Created by CORAIMA on 01/11/2017.
 */

public class ItemFeed {


    private String title;
    private String desc;
    private String image;
    private String category;
    private String author;
    private String resumen;
    private String vistas;



    public ItemFeed(){

    }

    public ItemFeed(String title, String desc, String image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }
}
