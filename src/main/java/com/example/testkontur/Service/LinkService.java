package com.example.testkontur.Service;

import com.example.testkontur.Entity.MLink;
import com.example.testkontur.Entity.RankedLinkProjection;
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

    public void createLink(MLink link) {
        linkRepository.save(link);
    }

    public List<MLink> findAll(){
        return linkRepository.findAll();
    }

    public MLink findByShortLink(String shortLink){
        return linkRepository.findById(shortLink).orElse(null);
    }

    public boolean contains(String shortLink){
        return linkRepository.existsById(shortLink);
    }

    public void updateLink(MLink link) {
        linkRepository.save(link);
    }

    public void updateLinks(Iterable<MLink> links){
        linkRepository.saveAll(links);
    }

    public List<RankedLinkProjection> findSubRankedLinks(long startIndex, int count) {
        return linkRepository.findSubRankedLinks(startIndex, startIndex + count - 1);
    }

    public RankedLinkProjection findLinkStats(String shortLink) {
        return linkRepository.findLinkStats(shortLink);
    }
}
