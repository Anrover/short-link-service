package com.example.testkontur.Entity;

import java.util.Objects;

public class RankedLink implements RankedLinkProjection {
    private String link;
    private String original;
    private int rank;
    private int count;

    public RankedLink(String link, String original, int rank, int count) {
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
    public int getCount() {
        return count;
    }

    @Override
    public int getRank() {
        return rank;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRank(int rank) {
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
