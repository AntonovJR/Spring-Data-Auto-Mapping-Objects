package com.example.springdataautomappingobjects.model.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User extends BaseEntity {
    private String  email;
    private String password;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "is_admin")
    private boolean isAdmin;
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Game> gamesList;

    public User() {
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(Set<Game> gamesList) {
        this.gamesList = gamesList;
    }
}
