package com.techelevator.custom.dao;

import com.techelevator.custom.exception.DaoException;
import com.techelevator.custom.model.Assignment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAssignmentDao implements AssignmentDao {
    private static final String BASE_SELECT =
            "SELECT a.assignment_id, a.employee_id, a.trailer_id, a.door_number, a.status, " +
                    "u.name AS employee_name, u.employee_number, " +
                    "t.trailer_number, t.trailer_type " +
                    "FROM assignments a " +
                    "JOIN unloader u ON a.employee_id = u.employee_id " +
                    "JOIN trailer t ON a.trailer_id = t.trailer_id";

    private final JdbcTemplate jdbcTemplate;

    public JdbcAssignmentDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Assignment> getAllAssignments() {
        List<Assignment> assignments = new ArrayList<>();
        SqlRowSet results = jdbcTemplate.queryForRowSet(BASE_SELECT + " ORDER BY a.assignment_id");
        while (results.next()) {
            assignments.add(mapRowToAssignment(results));
        }
        return assignments;
    }

    @Override
    public Assignment getAssignmentById(int id) {
        String sql = BASE_SELECT + " WHERE a.assignment_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return mapRowToAssignment(results);
        }
        return null;
    }

    @Override
    @Transactional
    public Assignment createAssignment(Assignment assignment) {
        String sql = "INSERT INTO assignments (employee_id, trailer_id, door_number, status) " +
                "VALUES (?, ?, ?, ?) RETURNING assignment_id";
        Integer newId = jdbcTemplate.queryForObject(sql, Integer.class,
                assignment.getEmployeeId(),
                assignment.getTrailerId(),
                assignment.getDoorNumber(),
                assignment.getStatus());

        if (newId == null) {
            throw new DaoException("Failed to create assignment - no ID returned");
        }
        return getAssignmentById(newId);
    }

    @Override
    @Transactional
    public Assignment updateAssignment(Assignment assignment) {
        String sql = "UPDATE assignments SET employee_id = ?, trailer_id = ?, " +
                "door_number = ?, status = ? WHERE assignment_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                assignment.getEmployeeId(),
                assignment.getTrailerId(),
                assignment.getDoorNumber(),
                assignment.getStatus(),
                assignment.getAssignmentId());

        if (rowsUpdated == 0) {
            throw new DaoException("No assignment found with ID " + assignment.getAssignmentId());
        }
        return getAssignmentById(assignment.getAssignmentId());
    }

    @Override
    @Transactional
    public boolean deleteAssignment(int id) {
        String sql = "DELETE FROM assignments WHERE assignment_id = ?";
        int rowsDeleted = jdbcTemplate.update(sql, id);
        return rowsDeleted > 0;
    }

    private Assignment mapRowToAssignment(SqlRowSet rs) {
        Assignment assignment = new Assignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setEmployeeId(rs.getInt("employee_id"));
        assignment.setTrailerId(rs.getInt("trailer_id"));
        assignment.setDoorNumber(rs.getString("door_number"));
        assignment.setStatus(rs.getString("status"));
        assignment.setEmployeeName(rs.getString("employee_name"));
        assignment.setEmployeeNumber(rs.getString("employee_number"));
        assignment.setTrailerNumber(rs.getString("trailer_number"));
        assignment.setTrailerType(rs.getString("trailer_type"));
        // Shipper name removed since we're not joining with shipper table
        return assignment;
    }
}