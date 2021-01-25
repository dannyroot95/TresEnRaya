package com.soft.tresenrayamultiplayer.Models;

public class Status {
    int points;
    int stats;

    public Status(){}

    public Status(int points, int stats) {
        this.points = points;
        this.stats = stats;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getStats() {
        return stats;
    }

    public void setStats(int stats) {
        this.stats = stats;
    }
}
