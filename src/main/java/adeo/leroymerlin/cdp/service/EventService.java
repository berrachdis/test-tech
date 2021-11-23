package adeo.leroymerlin.cdp.service;

import adeo.leroymerlin.cdp.domain.entity.Band;
import adeo.leroymerlin.cdp.domain.entity.Event;
import adeo.leroymerlin.cdp.domain.entity.Member;
import adeo.leroymerlin.cdp.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class EventService {
    private static final Logger LOG = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    // replace findAllBy() by findAll() to get all entities

    /**
     * Method used to retrieve all events from database
     * @return a {@literal List} of {@literal Event}
     */
    @Transactional(readOnly = true)
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    /**
     * Method used to delete an existing event by the given {@code id}
     * @param id represent the identifier of the event to delete
     * @return {@code true} if the given {@code id} exist, otherwise {@code false}
     */
    @Transactional
    public Integer delete(Long id) {
        return eventRepository.deleteById(id);
    }

    /**
     * Method used to update an existing event
     * @param id id represent the identifier of the event to delete
     * @param eventToUpdate instance of {@literal Event} to update
     * @return {@code true} if the given {@code id} exist, otherwise {@code false}
     */
    public boolean update(Long id, Event eventToUpdate) {
        final Event event = this.eventRepository.findOne(id);
        if (!ObjectUtils.isEmpty(event)) {
            event.setNbStars(eventToUpdate.getNbStars());
            event.setComment(eventToUpdate.getComment());
            this.eventRepository.save(event);
            return true;
        } else {
            LOG.warn(String.format("No event with id %s exists!", id));
            return false;
        }
    }

    /**
     * Method used to filter events by the given {@code query}
     * @param query the filter to apply on event's members
     * @return {@literal List<Event>}
     */
    @Transactional(readOnly = true)
    public List<Event> getFilteredEvents(String query) {
        final List<Event> events = this.eventRepository.findAll();
        this.filterEventsByQuery(events, query);
        return events;
    }

    private void filterEventsByQuery(List<Event> events, String query) {
        if (!CollectionUtils.isEmpty(events)) {
            final Iterator<Event> eventIterator = events.iterator();

            while (eventIterator.hasNext()) {
                final Event currentEvent = eventIterator.next();
                final Set<Band> bands = this.filterBandsByQuery(currentEvent, query);
                if (!CollectionUtils.isEmpty(bands)) {
                    currentEvent.setBands(bands);
                    currentEvent.setTitle(concatTitleCount(currentEvent.getTitle(), bands.size()));
                } else {
                    eventIterator.remove();
                }
            }
        }
    }

    private Set<Band> filterBandsByQuery(Event event, String query) {
        if (!CollectionUtils.isEmpty(event.getBands())) {
            final Set<Band> bandsToReturn = new HashSet<>();
            for (Band currentBand : event.getBands()) {
                this.filterMembersByQuery(currentBand, query);
                if (!CollectionUtils.isEmpty(currentBand.getMembers())) {
                    bandsToReturn.add(mapToBand(currentBand.getName(), currentBand.getMembers()));
                }
            }
            return bandsToReturn;
        }
        return null;
    }

    private void filterMembersByQuery(Band band, String query) {
        if (!CollectionUtils.isEmpty(band.getMembers())) {
            final Iterator<Member> memberIterator = band.getMembers().iterator();
            while (memberIterator.hasNext()) {
                final Member currentMember = memberIterator.next();
                if (StringUtils.isEmpty(currentMember.getName()) || !currentMember.getName().toLowerCase().contains(query.toLowerCase())) {
                    memberIterator.remove();
                }
            }
        }
    }

    /**
     * Method used to create a new instance of {@literal Band} by the given {@code name} and {@members}
     * @param name the band's name
     * @param members the band's members
     * @return a new instance of {@literal Band}
     */
    private Band mapToBand(String name, Set<Member> members) {
        final Band bandToReturn = new Band();
        bandToReturn.setName(concatTitleCount(name, members.size()));
        bandToReturn.setMembers(members);
        return bandToReturn;
    }

    /**
     * Method used to concat the title with the size of child list
     * @param name
     * @param size
     * @return a {@literal String} which contains the concatenation of the {@code name} and {@code size}
     */
    private String concatTitleCount(String name, int size) {
        return name + " [" + size + "]";
    }
}
