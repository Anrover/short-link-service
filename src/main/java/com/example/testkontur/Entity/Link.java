package com.example.testkontur.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "LINK")
public class Link {
    @Id
    @Column(name = "Short_Link", nullable = false)
    private String shortLink;
    @Column(name = "Original_Link", nullable = false)
    private String originLink;
    @Column(name = "Count_Requests", nullable = false)
    private int countRequests;

    public Link() {}

    public Link(String originLink, String shortLink) {
        this(originLink, shortLink, 0);
    }

    public Link(String originLink, String shortLink, int countRequests) {
        this.originLink = originLink;
        this.shortLink = shortLink;
        this.countRequests = countRequests;
    }

    public String getShortLink() {
        return shortLink;
    }

    public String getOriginLink() {
        return originLink;
    }

    public int getCountRequests() {
        return countRequests;
    }

    public void setCountRequests(int countRequests) {
        this.countRequests = countRequests;
    }
}
