package com.example.testkontur.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LINK")
public class MLink implements Link {
    @Id
    @Column(name = "Short_Link", nullable = false)
    private String shortLink;
    @Column(name = "Original_Link", nullable = false)
    private String originLink;
    @Column(name = "Count_Requests", nullable = false)
    private int countRequests;

    public MLink() {}

    public MLink(String originLink, String shortLink) {
        this(originLink, shortLink, 0);
    }

    public MLink(String originLink, String shortLink, int countRequests) {
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
