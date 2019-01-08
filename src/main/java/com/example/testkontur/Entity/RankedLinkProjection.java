package com.example.testkontur.Entity;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "link", "original", "rank", "count" })
public interface RankedLinkProjection {
    String getLink();
    String getOriginal();
    long getCount();
    long getRank();
}
