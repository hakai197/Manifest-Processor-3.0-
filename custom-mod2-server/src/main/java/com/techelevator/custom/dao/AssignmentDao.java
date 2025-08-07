package com.techelevator.custom.dao;

import com.techelevator.custom.model.Assignment;
import java.util.List;

public interface AssignmentDao {
    Assignment getAssignmentById(int id);
    List<Assignment> getAllAssignments();
    Assignment createAssignment(Assignment assignment);
    Assignment updateAssignment(Assignment assignment);
    boolean deleteAssignment(int id);
}