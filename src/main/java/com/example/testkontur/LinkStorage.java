package com.example.testkontur;


import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.RankedLink;
import com.example.testkontur.Service.LinkService;

import java.util.List;


public class LinkStorage {
    private LinkService linkService;

    private LRUCache<String, Link> cache;
    private static final int CACHE_SIZE = 300;

    public LinkStorage(LinkService linkService) {
        this.linkService = linkService;
        cache = new LRUCache<String, Link>(CACHE_SIZE);
    }

    public boolean contains(String shortLink) {
        return linkService.contains(shortLink);
    }

    public void putLink(Link link) {
        System.out.println(link.getShortLink());
        linkService.createLink(link);
    }

    public Link getLink(String shortLink) {
        return linkService.findByShortLink(shortLink);
    }

    public RankedLink getLinkStats(String shortLink) {
        return linkService.findLinkStats(shortLink);
    }

    public Link updateRequestsLink(String shortLink) {
        return linkService.updateRequestsLink(shortLink);
    }

    public List<RankedLink> getSubRankedLinks(int startIndex, int count) {
        return linkService.findSubRankedLinks(startIndex, count);
    }

    public void updateLink(Link link) {
        linkService.updateLink(link);
    }
}