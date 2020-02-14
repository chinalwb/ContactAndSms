package com.chinalwb.contactandsms;

public class ContactModel {
    private String number;
    private String display_name;
    private String first_name;
    private String last_name;
    private String photo_thumbnail_uri;
    private String photo_uri;

    public String getNumber() {
        return number == null ? "" : number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDisplay_name() {
        return display_name == null ? "" : display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhoto_thumbnail_uri() {
        return photo_thumbnail_uri == null ? "" : photo_thumbnail_uri;
    }

    public void setPhoto_thumbnail_uri(String photo_thumbnail_uri) {
        this.photo_thumbnail_uri = photo_thumbnail_uri;
    }

    public String getPhoto_uri() {
        return photo_uri == null ? "" : photo_uri;
    }

    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }
}