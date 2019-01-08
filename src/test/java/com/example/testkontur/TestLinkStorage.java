package com.example.testkontur;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.RankedLinkProjection;
import com.example.testkontur.Service.LinkService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TestLinkStorage {
    @Autowired
    LinkService linkService;

    @Test
    public void testCreateLink() {
        String url = "http://rs.ru";
        String shortLink = "qwerty1";
        LinkStorage storage = new LinkStorage(linkService, 2);
        storage.createNewLink(url, "qwerty1");
        Link link = storage.getLink(shortLink);
        assertEquals(url, link.getOriginLink());
        assertEquals(shortLink, link.getShortLink());
        assertEquals(0, link.getCountRequests());
    }

    @Test
    public void testContains() {
        String url = "http://rs.ru";
        String shortLink = "qwerty1";
        String badShortLink = "qzxccxz";
        LinkStorage storage = new LinkStorage(linkService, 2);
        storage.createNewLink(url, "qwerty1");
        assertTrue(storage.contains(shortLink));
        assertFalse(storage.contains(badShortLink));
    }

    @Test
    public void testGetLinkStats() {
        String url = "http://rs.ru";
        String shortLink = "qwerty1";
        int countRequests = 100;
        LinkStorage storage = new LinkStorage(linkService, 2);
        storage.createNewLink(url, "qwerty1");
        Link link = storage.getLink(shortLink);
        storage.updateLink(link, countRequests);
        RankedLinkProjection rankedLink = storage.getLinkStats(shortLink);
        assertEquals(url, rankedLink.getOriginal());
        assertEquals(shortLink, rankedLink.getLink());
        assertEquals(countRequests, rankedLink.getCount());
        assertEquals(1, rankedLink.getRank());
    }

    @Test
    public void testCache() throws NoSuchFieldException, IllegalAccessException {
        LinkStorage storage = new LinkStorage(linkService, 2);
        Field field = LinkStorage.class.getDeclaredField("cache");
        field.setAccessible(true);
        Map<String, Link> cache = (Map<String, Link>)field.get(storage);
        storage.createNewLink("url1", "link1");
        storage.createNewLink("url2", "link2");
        storage.createNewLink("url3", "link3");
        assertEquals(0, cache.size());
        storage.getLink("link1");
        assertEquals(1, cache.size());
        storage.getLink("link2");
        assertTrue(Arrays.equals(cache.keySet().toArray(), new String[] {"link1", "link2"}));
        storage.getLink("link1");
        assertTrue(Arrays.equals(cache.keySet().toArray(), new String[] {"link2", "link1"}));
        storage.getLink("link3");
        assertTrue(Arrays.equals(cache.keySet().toArray(), new String[] {"link1", "link3"}));
    }
}
