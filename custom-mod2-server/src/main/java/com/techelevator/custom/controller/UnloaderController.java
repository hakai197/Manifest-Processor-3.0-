package com.techelevator.custom.controller;

import com.techelevator.custom.dao.UnloaderDao;
import com.techelevator.custom.exception.DaoException;
import com.techelevator.custom.model.Unloader;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(path = "api/unloaders")

public class UnloaderController {
    private final UnloaderDao unloaderDao;

    public UnloaderController(UnloaderDao unloaderDao) {
        this.unloaderDao = unloaderDao;
    }


    @GetMapping
    public List<Unloader> getAllUnloaders() {
        try {
            return unloaderDao.getAllUnloaders();
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find unloaders: " + e.getMessage());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/name/{name}")
    public Unloader getUnloaderByName(@PathVariable String name) {
        try {
            // You'll need to add this method to your UnloaderDao interface and implement it
            Unloader unloader = unloaderDao.getUnloaderByName(name);
            if (unloader == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unloader not found with name: " + name);
            }
            return unloader;
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving unloader: " + e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Unloader> createUnloader(@Valid @RequestBody Unloader unloader) {
        return new ResponseEntity<>(unloaderDao.createUnloader(unloader), HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public Unloader updateUnloader(@PathVariable int id, @RequestBody Unloader unloader) {
        try {
            unloader.setEmployeeId(id);
            return unloaderDao.updateUnloader(id, unloader);
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating unloader: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUnloader(@PathVariable int id) {
        try {
            int rowsAffected = unloaderDao.deleteUnloaderById(id);
            if (rowsAffected == 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unloader not found");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting unloader: " + e.getMessage());
        }
    }
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/search")
    public List<Unloader> searchUnloaders(@RequestParam String query) {
        try {
            List<Unloader> results = unloaderDao.searchUnloaders(query);
            return results; // Returns empty list if no results
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error searching unloaders");
        }
    }
}