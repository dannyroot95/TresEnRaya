package com.soft.tresenrayamultiplayer.Models;

public class Users {


    String id;
    String name;
    String lastName;
    String nick;
    String email;
    int stats;
    int points;
    String photo;

    public Users(){}

    public Users(String id, String name, String lastName, String nick, String email, int stats, int points, String photo) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.nick = nick;
        this.email = email;
        this.stats = stats;
        this.points = points;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStats() {
        return stats;
    }

    public void setStats(int stats) {
        this.stats = stats;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
