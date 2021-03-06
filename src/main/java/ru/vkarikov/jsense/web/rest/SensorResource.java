package ru.vkarikov.jsense.web.rest;

import com.codahale.metrics.annotation.Timed;
import ru.vkarikov.jsense.domain.Sensor;
import ru.vkarikov.jsense.service.SensorService;
import ru.vkarikov.jsense.web.rest.errors.BadRequestAlertException;
import ru.vkarikov.jsense.web.rest.util.HeaderUtil;
import ru.vkarikov.jsense.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Sensor.
 */
@RestController
@RequestMapping("/api")
public class SensorResource {

    private final Logger log = LoggerFactory.getLogger(SensorResource.class);

    private static final String ENTITY_NAME = "sensor";

    private final SensorService sensorService;

    public SensorResource(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    /**
     * POST  /sensors : Create a new sensor.
     *
     * @param sensor the sensor to create
     * @return the ResponseEntity with status 201 (Created) and with body the new sensor, or with status 400 (Bad Request) if the sensor has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/sensors")
    @Timed
    public ResponseEntity<Sensor> createSensor(@RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to save Sensor : {}", sensor);
        if (sensor.getId() != null) {
            throw new BadRequestAlertException("A new sensor cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Sensor result = sensorService.save(sensor);
        return ResponseEntity.created(new URI("/api/sensors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sensors : Updates an existing sensor.
     *
     * @param sensor the sensor to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated sensor,
     * or with status 400 (Bad Request) if the sensor is not valid,
     * or with status 500 (Internal Server Error) if the sensor couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/sensors")
    @Timed
    public ResponseEntity<Sensor> updateSensor(@RequestBody Sensor sensor) throws URISyntaxException {
        log.debug("REST request to update Sensor : {}", sensor);
        if (sensor.getId() == null) {
            return createSensor(sensor);
        }
        Sensor result = sensorService.save(sensor);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, sensor.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sensors : get all the sensors.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of sensors in body
     */
    @GetMapping("/sensors")
    @Timed
    public ResponseEntity<List<Sensor>> getAllSensors(Pageable pageable) {
        log.debug("REST request to get a page of Sensors");
        Page<Sensor> page = sensorService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sensors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sensors/:id : get the "id" sensor.
     *
     * @param id the id of the sensor to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the sensor, or with status 404 (Not Found)
     */
    @GetMapping("/sensors/{id}")
    @Timed
    public ResponseEntity<Sensor> getSensor(@PathVariable Long id) {
        log.debug("REST request to get Sensor : {}", id);
        Sensor sensor = sensorService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(sensor));
    }

    /**
     * DELETE  /sensors/:id : delete the "id" sensor.
     *
     * @param id the id of the sensor to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/sensors/{id}")
    @Timed
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        log.debug("REST request to delete Sensor : {}", id);
        sensorService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/sensors?query=:query : search for the sensor corresponding
     * to the query.
     *
     * @param query the query of the sensor search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/sensors")
    @Timed
    public ResponseEntity<List<Sensor>> searchSensors(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Sensors for query {}", query);
        Page<Sensor> page = sensorService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/sensors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
