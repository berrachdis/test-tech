package adeo.leroymerlin.cdp.controller;

import adeo.leroymerlin.cdp.domain.entity.Event;
import adeo.leroymerlin.cdp.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// IMPORTANT : Avoid import * from package org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// it's preferable to specify the type of MediaType if all api produces the same type
@RequestMapping(value = "/api/events", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping("search/{query}")
    public ResponseEntity<? extends Object> findEvents(@PathVariable String query) {
        final List<Event> filteredEvents = eventService.getFilteredEvents(query);
        if (CollectionUtils.isEmpty(filteredEvents)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(filteredEvents);
    }

    // Replace @RequestMapping with method DELETE by @DeleteMapping
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        final Integer isDeleted = this.eventService.delete(id);
        if (isDeleted == 1) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Replace @RequestMapping with method DELETE by @PutMapping
    @PutMapping("{id}")
    public ResponseEntity<Void> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        final boolean isUpdated = this.eventService.update(id, event);
        if (isUpdated) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
