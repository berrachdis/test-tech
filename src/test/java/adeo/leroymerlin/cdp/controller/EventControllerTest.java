package adeo.leroymerlin.cdp.controller;

import adeo.leroymerlin.cdp.domain.entity.Event;
import adeo.leroymerlin.cdp.repository.EventRepository;
import adeo.leroymerlin.cdp.util.CommonConstantUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
public class EventControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mvc;

    @Autowired
    private EventRepository eventRepository;

    @Test
    public void findAll() throws Exception {
        final List<Event> events = this.eventRepository.findAll();
        final String bodyAsString = this.mvc.perform(get(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final List<Event> actualResult = objectMapper.readValue(bodyAsString, objectMapper.getTypeFactory().constructCollectionType(List.class, Event.class));
        Assertions.assertThat(actualResult.size()).isEqualTo(events.size());
    }

    @Test
    public void deleteKnownEventById() throws Exception {
        final List<Event> events = this.eventRepository.findAll();
        final Long id = 1005L;
        this.mvc.perform(delete(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH + "/" + CommonConstantUtil.EVENT_DELETE_BY_ID_PATH, String.valueOf(id)))
                .andExpect(status().isNoContent());
        final Event event = this.eventRepository.findOne(id);
        Assertions.assertThat(event).isNull();
    }

    @Test
    public void deleteUnknownEventById() throws Exception {
        final Long id = 100L;
        this.mvc.perform(delete(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH + "/" + CommonConstantUtil.EVENT_DELETE_BY_ID_PATH, String.valueOf(id))
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateKnownEvent() throws Exception {
        final Long id = 1001L;
        final String title = "new Title";
        final Event event = this.eventRepository.findOne(id);
        event.setTitle(title);
        this.mvc.perform(put(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH + "/" + CommonConstantUtil.EVENT_UPDATE_BY_ID_PATH, String.valueOf(id))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNoContent());
        final Event actualResult = this.eventRepository.findOne(id);
        Assertions.assertThat(actualResult.getTitle()).isEqualTo(title);
    }

    @Test
    public void updateUnknownEvent() throws Exception {
        final Long id = -1002L;
        final Event event = new Event();
        event.setTitle("test");
        event.setBands(Collections.EMPTY_SET);
        this.mvc.perform(put(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH + "/" + CommonConstantUtil.EVENT_UPDATE_BY_ID_PATH, String.valueOf(id))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void filterknownMember() throws Exception {
        final String query = "wa";
        final String expectedResponseBody = "[\n" +
                "    {\n" +
                "        \"id\": 1000,\n" +
                "        \"title\": \"GrasPop Metal Meeting [1]\",\n" +
                "        \"imgUrl\": \"img/1000.jpeg\",\n" +
                "        \"bands\": [\n" +
                "            {\n" +
                "                \"name\": \"Metallica [1]\",\n" +
                "                \"members\": [\n" +
                "                    {\n" +
                "                        \"name\": \"Queen Anika Walsh\"\n" +
                "                    }\n" +
                "                ]\n" +
                "            }\n" +
                "        ],\n" +
                "        \"nbStars\": null,\n" +
                "        \"comment\": null\n" +
                "    }\n" +
                "]";

        final String bodyAsString = this.mvc.perform(get(CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH + "/" + CommonConstantUtil.EVENT_FILTER_BY_QUERY_API_PATH, query)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        final List<Event> actualResult = objectMapper.readValue(bodyAsString, objectMapper.getTypeFactory().constructCollectionType(List.class, Event.class));
        Assertions.assertThat(actualResult.get(0).getTitle()).isEqualTo("GrasPop Metal Meeting [1]");
        Assertions.assertThat(actualResult.get(0).getBands().iterator().next().getMembers().iterator().next().getName().toLowerCase()).contains(query.toLowerCase());
    }
}