package com.techelevator.custom.dao;

import com.techelevator.custom.model.Trailer;

import java.util.List;

public interface TrailerDao {
    Trailer getTrailerById(int id);
    List<Trailer> getTrailers();
    List<Trailer> getTrailersByShipperId(int shipperId);
    Trailer createTrailer(Trailer trailer);
    Trailer updateTrailer(Trailer trailer);
    List<Trailer> getAvailableTrailers();
    void unassignTrailer(String trailerNumber);
    void assignUnloaderToTrailer(String trailerNumber, int employeeId);
    boolean deleteTrailer(int id);
    Trailer getTrailerByNumber(String trailerNumber);

}