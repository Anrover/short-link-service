package com.example.testkontur.Controller;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.LinkStorage;
import com.example.testkontur.RandomString;
import com.example.testkontur.Entity.RankedLink;
import com.example.testkontur.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LinkController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
    private static final int lenShortLink = 7;
    private static final RandomString randomString = new RandomString(lenShortLink);
    @Autowired
    private LinkStorage storage;

    @RequestMapping(method=RequestMethod.GET, path = "/generate")//, produces = MediaType.APPLICATION_JSON_VALUE)// change to Post
    public ResponseEntity getShortLink(@RequestParam(value="original") String originLink) {
        String validUrl = UrlValidator.getValidUrlIfPossible(originLink).orElse(null);
        if (validUrl != null) {
            String shortLink;
            do {
                shortLink = "/l/" + randomString.nextString();
            } while (storage.contains(shortLink));
            storage.putLink(new Link(validUrl, shortLink));
            return ResponseEntity.ok(Collections.singletonMap("link", shortLink));
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
            link.setCountRequests(link.getCountRequests() + 1);
            storage.updateLink(link);
            String originLink = link.getOriginLink();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(originLink));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    @RequestMapping(method=RequestMethod.GET, path = "/stats/{shortLink}")//, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getStats(@PathVariable String shortLink) {
        RankedLink link = storage.getLinkStats("/l/" + shortLink);
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
        else {
            return ResponseEntity.ok(storage.getSubRankedLinks(
                    (numPage-1)*countEntities + 1, countEntities
            ));
        }
    }
}
