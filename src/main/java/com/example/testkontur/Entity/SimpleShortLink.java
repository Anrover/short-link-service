package com.example.testkontur.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SimpleShortLink {
    private String link;

    public SimpleShortLink(String link) {
        this.link = link;
    }

    public SimpleShortLink() {}

    public String getLink() {
        return link;
    }
//only for testing
    @JsonIgnore
    public String getId() {
        return link.split("/")[2];
    }

    public void setLink(String link) {
        this.link = link;
    }
}
