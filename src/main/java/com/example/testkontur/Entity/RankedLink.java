package com.example.testkontur.Entity;

import java.util.Objects;

public class RankedLink implements RankedLinkProjection {
    private String link;
    private String original;
    private long rank;
    private long count;

    public RankedLink(String link, String original, long rank, long count) {
        this.link = link;
        this.original = original;
        this.rank = rank;
        this.count = count;
    }

    RankedLink() {}

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public String getOriginal() {
        return original;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public long getRank() {
        return rank;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if(this.getClass() != other.getClass()) return false;
        RankedLink otherObj = (RankedLink)other;
        return link.equals(otherObj.link) && original.equals(otherObj.original)
                && rank == otherObj.rank && count == otherObj.count;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(link, original, count, rank);
    }
}
