package com.example.springdataautomappingobjects.service.impl;

import com.example.springdataautomappingobjects.model.dto.GameAddDto;
import com.example.springdataautomappingobjects.model.entity.Game;
import com.example.springdataautomappingobjects.repository.GameRepository;
import com.example.springdataautomappingobjects.service.GameService;
import com.example.springdataautomappingobjects.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;



    public GameServiceImpl(GameRepository gameRepository, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;

    }

    @Override
    public void addGame(GameAddDto gameAddDto) {

        Set<ConstraintViolation<GameAddDto>> violation = validationUtil.violation(gameAddDto);
        if (!violation.isEmpty()) {
            violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);
            return;
        }
        if(gameRepository.getGameByTitle(gameAddDto.getTitle())!=null){
            System.out.printf("Game %s is already added%n",gameAddDto.getTitle());
            return;
        }
        Game game = modelMapper.map(gameAddDto, Game.class);
        gameRepository.save(game);
        System.out.printf("Added game %s%n", game.getTitle());
    }

    @Override
    public void editGame(long id, String[] commands) {

        String[] tokens = Arrays.stream(commands).skip(1).toArray(String[]::new);
        Game gameById = gameRepository.findById(id).orElse(null);
        if (gameById == null) {
            System.out.println("There is no such game");
            return;
        }
        for (String token : tokens) {
            String[] editedFields = token.split("=");
            switch (editedFields[0]) {
                case "title" -> gameById.setTitle(editedFields[1]);
                case "trailer" -> gameById.setTrailer(editedFields[1]);
                case "imageThumbnail" -> gameById.setImageThumbnail(editedFields[1]);
                case "size" -> gameById.setSize(Float.parseFloat(editedFields[1]));
                case "price" -> gameById.setPrice(new BigDecimal(editedFields[1]));
                case "description" -> gameById.setDescription(editedFields[1]);
                case "releaseDate" -> gameById.setReleaseDate(LocalDate.parse(editedFields[1],
                        DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            }

        }
        //String title, BigDecimal price, Double size, String trailer, String thumbnailUrl,
        //                      String description, LocalDate releaseDate
        GameAddDto gameAddDto = new GameAddDto(gameById.getTitle(),gameById.getPrice(),
                gameById.getSize(),gameById.getTrailer(),gameById.getImageThumbnail(),gameById.getDescription(),
                gameById.getReleaseDate());

        Set<ConstraintViolation<GameAddDto>> violation = validationUtil.violation(gameAddDto);
        if (!violation.isEmpty()) {
            violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);
            return;
        }
        gameRepository.save(gameById);
        System.out.println("Successfully edit game "+ gameById.getTitle());

    }

    @Override
    public void printAllGames() {
    gameRepository.findAll().forEach(game -> System.out.printf("%s %.2f%n",game.getTitle(),game.getPrice()));
    }

    @Override
    public void gameDetailsByTitle(String title) {
        Game gameByTitle = gameRepository.getGameByTitle(title);
        System.out.printf("Title: %s%n" +
                "Price: %.2f%n" +
                "Description: %s%n" +
                "Release date: %s%n", gameByTitle.getTitle(),gameByTitle.getPrice(),gameByTitle.getDescription(),
                gameByTitle.getReleaseDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    @Override
    public Game getGameByTitle(String title) {
        return gameRepository.getGameByTitle(title);
    }


    @Override
    public void deleteGame(long id) {
        Game gameById = gameRepository.findById(id).orElse(null);
        if (gameById == null) {
            System.out.println("There is no such game");
            return;
        }
        System.out.println("Successfully delete game "+ gameById.getTitle());
        try {
            gameRepository.delete(gameById);
        }catch (Exception e){
            System.out.println("This game is still in use");
        }

    }


}
