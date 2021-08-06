package com.example.springdataautomappingobjects.service;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.entity.Game;


public interface GameService {
    void addGame(GameAddDto gameAddDto);

    void deleteGame(long gameId);

    void editGame(long parseLong, String[] commands);

    void printAllGames();

    void gameDetailsByTitle(String title);

    Game getGameByTitle(String title);
}
