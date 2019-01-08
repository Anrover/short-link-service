package com.example.testkontur.Controller;

import java.net.URI;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.SimpleLink;
import com.example.testkontur.Entity.SimpleShortLink;
import com.example.testkontur.LinkStorage;
import com.example.testkontur.RandomString;
import com.example.testkontur.Entity.RankedLinkProjection;
import com.example.testkontur.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LinkController {
    private static final int lenShortLink = 7;
    private static final RandomString randomString = new RandomString(lenShortLink);
    @Autowired
    private LinkStorage storage;

    @RequestMapping(method=RequestMethod.POST, path = "/generate")
        public ResponseEntity getShortLink(@RequestBody SimpleLink link) {
        String validUrl = UrlValidator.getValidUrlIfPossible(link.getOriginal()).orElse(null);
        if (validUrl != null) {
            String shortLink;
            do {
                shortLink = "/l/" + randomString.nextString();
            } while (storage.contains(shortLink));
            storage.createNewLink(validUrl, shortLink);
            return ResponseEntity.ok(new SimpleShortLink(shortLink));
        }
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad url");
    }

    @RequestMapping(method=RequestMethod.GET, path = "/l/{shortLink}")
    public ResponseEntity redirectLink(@PathVariable String shortLink) {
        Link link = storage.getLink("/l/" + shortLink);
        if (link == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad short link");
        else {
            storage.updateLink(link, link.getCountRequests() + 1);
            String originLink = link.getOriginLink();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originLink));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @RequestMapping(method=RequestMethod.GET, path = "/stats/{shortLink}")
    public ResponseEntity getStats(@PathVariable String shortLink) {
        RankedLinkProjection link = storage.getLinkStats("/l/" + shortLink);
        if (link == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("bad short link");
        else
            return ResponseEntity.ok(link);
    }

    @RequestMapping(method=RequestMethod.GET, path = "/stats")
    public ResponseEntity getPageStats(@RequestParam(value="page") int numPage,
                               @RequestParam(value="count") int countEntities) {
        if (numPage <= 0 || countEntities <= 0 || countEntities > 100)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad  params");
        else
            return ResponseEntity.ok(storage.getSubRankedLinks(
                    (numPage-1)*countEntities + 1, countEntities));
    }
}
