
package com.techelevator.custom.controller;

import com.techelevator.custom.dao.AssignmentDao;
import com.techelevator.custom.model.Assignment;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentDao assignmentDao;

    public AssignmentController(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public List<Assignment> getAllAssignments() {
        return assignmentDao.getAllAssignments();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Assignment getAssignmentById(@PathVariable int id) {
        Assignment assignment = assignmentDao.getAssignmentById(id);
        if (assignment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
        return assignment;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Assignment createAssignment(@RequestBody Assignment assignment) {
        return assignmentDao.createAssignment(assignment);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public Assignment updateAssignment(@PathVariable int id,  @RequestBody Assignment assignment) {
        assignment.setAssignmentId(id);
        return assignmentDao.updateAssignment(assignment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAssignment(@PathVariable int id) {
        if (!assignmentDao.deleteAssignment(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignment not found");
        }
    }
}