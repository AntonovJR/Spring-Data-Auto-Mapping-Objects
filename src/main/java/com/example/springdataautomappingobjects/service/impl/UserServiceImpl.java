package com.example.springdataautomappingobjects.service.impl;

import com.example.springdataautomappingobjects.model.dto.UserLoginDto;
import com.example.springdataautomappingobjects.model.dto.UserRegisterDto;
import com.example.springdataautomappingobjects.model.entity.Game;
import com.example.springdataautomappingobjects.model.entity.User;
import com.example.springdataautomappingobjects.repository.UserRepository;
import com.example.springdataautomappingobjects.service.GameService;
import com.example.springdataautomappingobjects.service.UserService;
import com.example.springdataautomappingobjects.util.ValidationUtil;
import org.hibernate.annotations.Fetch;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.cdi.Eager;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private User loggedUser;
    private final GameService gameService;


    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, ValidationUtil validationUtil
            , GameService gameService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.gameService = gameService;
    }

    @Override
    public void registerUser(UserRegisterDto userRegisterDto) {
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            System.out.println("Wrong confirm password");
            return;
        }
        Set<ConstraintViolation<UserRegisterDto>> violation =
                validationUtil.violation(userRegisterDto);
        if (!violation.isEmpty()) {
            violation
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);
            return;
        }
        User user = modelMapper.map(userRegisterDto, User.class);

        userRepository.save(user);
        if (user.getId() == 1L) {
            user.setAdmin(true);
        }
        userRepository.save(user);
        System.out.printf("%s was registered%n", user.getFullName());

    }

    @Override
    public void loginUser(UserLoginDto userLoginDto) {
        Set<ConstraintViolation<UserLoginDto>> violation =
                validationUtil.violation(userLoginDto);
        if (!violation.isEmpty()) {
            violation.stream()
                    .map(ConstraintViolation::getMessage)
                    .forEach(System.out::println);
            return;
        }
        User user = userRepository.findAllByEmailAndPassword(userLoginDto.getEmail(),
                userLoginDto.getPassword())
                .orElse(null);
        if (user == null) {
            System.out.println("Incorrect username  / password");
            return;
        }
        loggedUser = user;
        System.out.printf("Successfully logged in %s%n", loggedUser.getFullName());

    }

    @Override
    public void logout() {
        if (this.loggedUser == null) {
            System.out.println("Cannot log out. No user was logged in.");
        } else {
            System.out.printf("User %s successfully logged out%n", this.loggedUser.getFullName());
            this.loggedUser = null;
        }
    }

    @Override
    public void printOwnedGames() {
        if (this.loggedUser == null) {
            System.out.println("No user was logged in.");
        } else {
            Set<Game> gamesList = this.loggedUser.getGamesList();
            if (gamesList.size() == 0) {
                System.out.println("No bought games yet");
            } else {
                gamesList.forEach(game -> System.out.println(game.getTitle()));
            }
        }
    }

    @Override
    public void buyGame(String gameTitle) {
        Game game = this.gameService.getGameByTitle(gameTitle);
        if (this.loggedUser == null) {
            System.out.println("No user was logged in.");
            return;
        }
        if (game == null) {
            System.out.println("There is no such game");
            return;
        }
        for (Game userGame : loggedUser.getGamesList()) {
            if(userGame.getTitle().equals(gameTitle)){
                System.out.printf("User %s already have game %s%n", loggedUser.getFullName(),gameTitle);
                return;
            }

        }
        this.loggedUser.getGamesList().add(this.gameService.getGameByTitle(gameTitle));
        userRepository.save(loggedUser);
        System.out.println("Successfully bought game " + gameTitle);

    }


    @Override
    public boolean userIsAdmin() {
        if (loggedUser != null) {
            return this.loggedUser.isAdmin();
        }
        return false;
    }


}
