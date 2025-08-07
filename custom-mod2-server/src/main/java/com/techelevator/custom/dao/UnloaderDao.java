package com.techelevator.custom.dao;

import com.techelevator.custom.model.Unloader;

import java.util.List;

public interface UnloaderDao {
    Unloader getUnloaderById(int id);
    Unloader createUnloader(Unloader unloader);
    Unloader updateUnloader(int id, Unloader unloader);
    int deleteUnloaderById(int id);
    List<Unloader> getAllUnloaders();

    List<Unloader> searchUnloaders(String query);

    List<Unloader> getAvailableUnloaders();

    Unloader getUnloaderByName(String name);
}
