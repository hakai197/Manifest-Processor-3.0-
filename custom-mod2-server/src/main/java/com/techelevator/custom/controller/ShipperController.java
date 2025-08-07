package com.techelevator.custom.controller;

import com.techelevator.custom.dao.ShipperDao;
import com.techelevator.custom.exception.DaoException;
import com.techelevator.custom.model.Shipper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/shippers")
public class ShipperController {
    private final ShipperDao shipperDao;

    public ShipperController(ShipperDao shipperDao) {
        this.shipperDao = shipperDao;
    }

    @GetMapping
    public List<Shipper> getAllShippers() {
        try {
            return shipperDao.getShippers();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find shippers: " + e.getMessage());
        }
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Shipper getShipperByName(@PathVariable String name) {
        try {
            Shipper shipper = shipperDao.getByName(name);
            if (shipper == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipper not found");
            }
            return shipper;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving shipper: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Shipper createShipper(@Valid @RequestBody Shipper shipper) {
        try {
            return shipperDao.createShipper(shipper);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating shipper: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Shipper updateShipper(@PathVariable int id, @Valid@RequestBody Shipper shipper) {
        try {
            shipper.setShipperId(id);
            return shipperDao.updateShipper(shipper);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating shipper: " + e.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteShipper(@PathVariable int id) {
        try {
            int rowsAffected = shipperDao.deleteShipperById(id);
            if (rowsAffected == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipper not found");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting shipper: " + e.getMessage());
        }
    }
    @GetMapping("/search")
    public List<Shipper> searchShippers(@RequestParam String query) {
        try {
            List<Shipper> shippers = new ArrayList<>();
            Shipper shipper = shipperDao.getByName(query);
            if (shipper != null) {
                shippers.add(shipper);
            }
            return shippers;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error searching shippers: " + e.getMessage());
        }
    }
}