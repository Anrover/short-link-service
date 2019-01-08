package com.example.testkontur;

import com.example.testkontur.Entity.RankedLink;
import com.example.testkontur.Entity.SimpleLink;
import com.example.testkontur.Entity.SimpleShortLink;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestKonturApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@WebAppConfiguration
public class TestKonturApplicationTests {
	private MockMvc mockMvc;
	private String uriGenerate = "/generate";

	@Autowired
	private WebApplicationContext webApplicationContext;

	private String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}
	private <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
	}

	private SimpleShortLink generateLink(String url) throws Exception {
		MvcResult answGenerate = mockMvc.perform(post(uriGenerate)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(new SimpleLink(url))))
				.andReturn();
		return mapFromJson(answGenerate.getResponse()
				.getContentAsString(), SimpleShortLink.class);
	}

	private RankedLink getStatsLink(String idLink) throws Exception {
		MvcResult answStats = mockMvc.perform(get("/stats/" + idLink))
				.andExpect(status().isOk())
				.andReturn();
		return mapFromJson(answStats.getResponse().getContentAsString(), RankedLink.class);
	}

	private RankedLink[] getStatsLinks(int numPage,int count) throws Exception {
		MvcResult answStats = mockMvc
				.perform(get(String.format("/stats?page=%d&count=%d", numPage, count)))
				.andExpect(status().isOk())
				.andReturn();
		return mapFromJson(answStats.getResponse().getContentAsString(), RankedLink[].class);
	}

	private void makeRedirect(String shortLink) throws Exception {
		mockMvc.perform(get(shortLink));
	}

	private void makeSomeRedirects(String shortLink, int count) throws Exception {
		for (int i = 0; i < count; i++)
			makeRedirect(shortLink);
	}

	private String getIdFromShortLink(String shortLink) {
		return shortLink.split("/")[2];
	}

	@Test
	public void generateShortLinkByGoodOriginal() throws Exception {
		mockMvc.perform(post(uriGenerate)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(new SimpleLink("vk.com"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.link", startsWith("/l/")));
	}

	@Test
	public void generateShortLinkByBadOriginal() throws Exception {
		mockMvc.perform(post(uriGenerate)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(new SimpleLink("varybadURLasflkqwmk"))))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void getStatsLinkByDefault() throws Exception {
		String uriSite = "vk.com";
		SimpleShortLink sShortLink = generateLink(uriSite);
		RankedLink link = getStatsLink(getIdFromShortLink(sShortLink.getLink()));
		RankedLink expectedLink = new RankedLink(sShortLink.getLink(),
				"http://" + uriSite, 1, 0);
		assertEquals(expectedLink, link);
	}

	@Test
	public void getStatsLinkAfterRedirect() throws Exception {
		String uriSite = "pikabu.ru";
		int countRedirects = 10;
		SimpleShortLink sShortLink = generateLink(uriSite);
		makeSomeRedirects(sShortLink.getLink(), countRedirects);
		RankedLink link = getStatsLink(getIdFromShortLink(sShortLink.getLink()));
		RankedLink expectedLink = new RankedLink(sShortLink.getLink(),
				"http://" + uriSite, 1, countRedirects);
		assertEquals(expectedLink, link);
	}

	@Test
	public void checkRedirect() throws Exception{
		String uriSite = "pikabu.ru";
		SimpleShortLink sShortLink = generateLink(uriSite);
		mockMvc.perform(get(sShortLink.getLink()))
				.andExpect(status().isMovedPermanently());
	}

	@Test
	public void getStatsLinksOnPage() throws Exception {
		int numPage = 1;
		int count = 2;
		String uri1 = "vk.com";
		String uri2 = "pikabu.ru";
		int countRedirects2 = 15;
		String uri3 = "https://kontur.ru";
		int countRedirects3 = 10;
		SimpleShortLink sShortLink1 = generateLink(uri1);
		SimpleShortLink sShortLink2 = generateLink(uri2);
		SimpleShortLink sShortLink3 = generateLink(uri3);
		makeSomeRedirects(sShortLink3.getLink(), countRedirects3);
		makeSomeRedirects(sShortLink2.getLink(), countRedirects2);
		RankedLink[] links = getStatsLinks(numPage, count);
		RankedLink[] expectedLinks = new RankedLink[] {
				new RankedLink(sShortLink2.getLink(),
						"http://" + uri2, 1, 15),
				new RankedLink(sShortLink3.getLink(),
						uri3, 2, 10)
		};
		assertTrue(Arrays.equals(links, expectedLinks));
	}

	@Test
	public void getStatsLinksOnPage_countIsBiggerThanCountLinks() throws Exception {
		int numPage = 1;
		int count = 10;
		String uri1 = "vk.com";
		String uri2 = "pikabu.ru";
		int countRedirects2 = 15;
		String uri3 = "https://kontur.ru";
		int countRedirects3 = 10;
		SimpleShortLink sShortLink1 = generateLink(uri1);
		SimpleShortLink sShortLink2 = generateLink(uri2);
		SimpleShortLink sShortLink3 = generateLink(uri3);
		makeSomeRedirects(sShortLink3.getLink(), countRedirects3);
		makeSomeRedirects(sShortLink2.getLink(), countRedirects2);
		RankedLink[] links = getStatsLinks(numPage, count);
		RankedLink[] expectedLinks = new RankedLink[] {
				new RankedLink(sShortLink2.getLink(),
						"http://" + uri2, 1, 15),
				new RankedLink(sShortLink3.getLink(),
						uri3, 2, 10),
				new RankedLink(sShortLink1.getLink(),
						"http://" + uri1, 3, 0)
		};
		assertTrue(Arrays.equals(links, expectedLinks));
	}

	@Test
	public void getStatsLinksOnPage_ExpectEmptyArray() throws Exception {
		int numPage = 4;
		int count = 10;
		String uri1 = "vk.com";
		String uri2 = "pikabu.ru";
		int countRedirects2 = 15;
		String uri3 = "https://kontur.ru";
		int countRedirects3 = 10;
		SimpleShortLink sShortLink1 = generateLink(uri1);
		SimpleShortLink sShortLink2 = generateLink(uri2);
		SimpleShortLink sShortLink3 = generateLink(uri3);
		makeSomeRedirects(sShortLink3.getLink(), countRedirects3);
		makeSomeRedirects(sShortLink2.getLink(), countRedirects2);
		RankedLink[] links = getStatsLinks(numPage, count);
		assertTrue(links.length == 0);
	}
}

