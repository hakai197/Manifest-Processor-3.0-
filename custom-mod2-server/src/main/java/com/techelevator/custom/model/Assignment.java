package com.techelevator.custom.model;

public class Assignment {
    private int assignmentId;
    private int employeeId;
    private int trailerId;
    private String doorNumber;
    private String status;
    private String employeeName;
    private String employeeNumber;
    private String trailerNumber;
    private String trailerType;
    private String shipperName;

    public Assignment() {}


    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public int getTrailerId() { return trailerId; }
    public void setTrailerId(int trailerId) { this.trailerId = trailerId; }
    public String getDoorNumber() { return doorNumber; }
    public void setDoorNumber(String doorNumber) { this.doorNumber = doorNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    public String getTrailerNumber() { return trailerNumber; }
    public void setTrailerNumber(String trailerNumber) { this.trailerNumber = trailerNumber; }
    public String getTrailerType() { return trailerType; }
    public void setTrailerType(String trailerType) { this.trailerType = trailerType; }
    public String getShipperName() { return shipperName; }
    public void setShipperName(String shipperName) { this.shipperName = shipperName; }
}