package adeo.leroymerlin.cdp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Replace Repository by JpaRepository and add @Repository and move @Transactional(readOnly = true) to EventService
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /*
        No needs to this because it already defined in the JpaRepository
    */
//    void delete(Long eventId);

    /*
        It will not work if there is no criteria after By
    */
//    List<Event> findAll();
}
