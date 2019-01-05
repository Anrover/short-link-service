package com.example.testkontur.Service;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.RankedLink;
import com.example.testkontur.Repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LinkService {
    @Autowired
    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository){
        this.linkRepository = linkRepository;
    }

    public void createLink(Link link) {
        linkRepository.save(link);
    }

    public List<Link> findAll(){
        return linkRepository.findAll();
    }

    public Link findByShortLink(String shortLink){
        return linkRepository.findById(shortLink).orElse(null);
    }

    public boolean contains(String shortLink){
        return linkRepository.findById(shortLink).isPresent();
    }

    public Link updateRequestsLink(String shortLink) {
        return linkRepository.updateRequestsLink(shortLink);
    }

    public void updateLink(Link link) {
        linkRepository.save(link);
    }

    public List<RankedLink> findSubRankedLinks(int startIndex, int count) {
        return linkRepository.findSubRankedLinks(startIndex, startIndex + count - 1);
    }

//    public List<Link> findAllByName(String name){
//        return linkRepository.findAllByName(name);
//    }
    public RankedLink findLinkStats(String shortLink) {
        return linkRepository.findLinkStats(shortLink);
    }
}
