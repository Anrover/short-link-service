package com.example.testkontur;


import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.MLink;
import com.example.testkontur.Entity.RankedLinkProjection;
import com.example.testkontur.Service.LinkService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class LinkStorage {
    private LinkService linkService;

    private Map<String, MLink> cache;
    private Map<String, MLink> linksForUpdate;
    private int limitLinksForUpdate;

    public LinkStorage(LinkService linkService) {
        this(linkService, 300);
    }

    public LinkStorage(LinkService linkService, int cacheSize) {
        if (cacheSize < 1) throw new IllegalArgumentException();
        this.linkService = linkService;
        cache = new LinkedHashMap<String, MLink>(cacheSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MLink> eldest) {
                return size() > cacheSize; // Size exceeded the max allowed.
            }
        };
        limitLinksForUpdate = cacheSize * 2;
        linksForUpdate = new HashMap<String, MLink>();
    }

    public synchronized boolean contains(String shortLink) {
        return linkService.contains(shortLink);
    }

    public synchronized Link getLink(String shortLink) {
        if (cache.containsKey(shortLink))
            return cache.get(shortLink);
        else {
            MLink link = linksForUpdate.getOrDefault(shortLink,
                    linkService.findByShortLink(shortLink));
            if (link != null)
                cache.put(shortLink, link);
            return link;
        }
    }

    public synchronized RankedLinkProjection getLinkStats(String shortLink) {
        synhronizeData();
        return linkService.findLinkStats(shortLink);
    }

    public synchronized List<RankedLinkProjection> getSubRankedLinks(int startIndex, int count) {
        synhronizeData();
        return linkService.findSubRankedLinks(startIndex, count);
    }

    private boolean needFreeUp() {
        return linksForUpdate.size() >= limitLinksForUpdate;
    }

    private void synhronizeData() {
        linkService.updateLinks(linksForUpdate.values());
        linksForUpdate.clear();
    }

    public synchronized void createNewLink(String originLink, String shortLink) {
        MLink link = new MLink(originLink, shortLink);
        linkService.createLink(link);
    }

    public synchronized void updateLink(Link link, long countRequests) {
        if(link instanceof MLink) {
            MLink mLink = (MLink) link;
            mLink.setCountRequests(countRequests);
            linksForUpdate.put(mLink.getShortLink(), mLink);
            if (this.needFreeUp())
                synhronizeData();
        }
        else throw new IllegalArgumentException("link doesn't instance of MLink!");
    }
}