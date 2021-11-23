package adeo.leroymerlin.cdp.controller.v1;

import adeo.leroymerlin.cdp.domain.entity.Event;
import adeo.leroymerlin.cdp.service.EventService;
import adeo.leroymerlin.cdp.util.CommonConstantUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// IMPORTANT : Avoid import * from package org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// it's preferable to specify the type of MediaType if all api produces the same type
@RequestMapping(value = CommonConstantUtil.EVENT_BASE_ENDPOINT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Replace @RequestMapping with method GET by @GetMapping
    @GetMapping
    public List<Event> findEvents() {
        return eventService.getEvents();
    }

    // Replace @RequestMapping with method GET by @GetMapping
    @GetMapping(CommonConstantUtil.EVENT_FILTER_BY_QUERY_API_PATH)
    public ResponseEntity<? extends Object> findEvents(@PathVariable String query) {
        final List<Event> filteredEvents = eventService.getFilteredEvents(query);
        if (CollectionUtils.isEmpty(filteredEvents)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredEvents);
    }

    // Replace @RequestMapping with method DELETE by @DeleteMapping
    @DeleteMapping(CommonConstantUtil.EVENT_DELETE_BY_ID_PATH)
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        final Integer isDeleted = this.eventService.delete(id);
        if (isDeleted == 1) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Replace @RequestMapping with method UPDATE by @PutMapping
    @PatchMapping(CommonConstantUtil.EVENT_UPDATE_BY_ID_PATH)
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        final boolean isUpdated = this.eventService.update(id, event);
        if (isUpdated) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
