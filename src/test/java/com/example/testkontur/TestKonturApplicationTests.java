package com.example.testkontur;

import com.example.testkontur.Entity.Link;
import com.example.testkontur.Entity.RankedLink;
import com.example.testkontur.Entity.SimpleLink;
import com.example.testkontur.Entity.SimpleShortLink;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TestKonturApplication.class)
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
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
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
//		String uri = "/generate?original=safqweqweq";
//		mockMvc.perform(post(uri).accept(MediaType.APPLICATION_JSON_VALUE))
		mockMvc.perform(post(uriGenerate)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(new SimpleLink("varybadURLasflkqwmk"))))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void statsLinkByDefault() throws Exception {
		String uriStats = "/stats/";
		String uriSite = "vk.com";
		MvcResult answGenerate = mockMvc.perform(post(uriGenerate)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(mapToJson(new SimpleLink(uriSite))))
				.andReturn();
		SimpleShortLink sShortLink = mapFromJson(answGenerate.getResponse()
				.getContentAsString(), SimpleShortLink.class);
		String urlGettingStats = uriStats + sShortLink.getId();
		MvcResult answStats = mockMvc.perform(get(urlGettingStats))
				//.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
		RankedLink link = mapFromJson(answStats.getResponse().getContentAsString(), RankedLink.class);
		assertEquals(link.getCount(), 0);
		assertEquals(link.getRank(), 1);
		assertEquals(link.getOriginal(), "http://" + uriSite);
		assertEquals(link.getLink(), sShortLink.getLink());
	}

	@Test
	public void statstatsLinkAfterRedirect() throws Exception {
		String uriStats = "/stats/";
		MvcResult answGenerate = mockMvc.perform(post(uriGenerate)
				.content(mapToJson(Collections.singletonMap("original", "vk.com"))))
				.andReturn();
		String shortLink = mapFromJson(answGenerate.getResponse().getContentAsString(), String.class);
		mockMvc.perform(get("/l/" + shortLink));
		mockMvc.perform(get("/l/" + shortLink));
		MvcResult answStats = mockMvc.perform(get(uriStats + shortLink)
				.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andReturn();
		RankedLink link = mapFromJson(answStats.getResponse().getContentAsString(), RankedLink.class);
		assertEquals(link.getCount(), 2);
		assertEquals(link.getRank(), 1);
		assertEquals(link.getOriginal(), "http://vk.com");
		assertEquals(link.getLink(), shortLink);
	}




}

