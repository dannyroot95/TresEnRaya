package com.soft.tresenrayamultiplayer.Models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Playing {

    private String playerOneId;
    private String playerTwoId;
    private List<Integer> selectedRow;
    private boolean turnPlayerOne;
    private String winnerId;
    private Date created;
    private String lostPlay;

    public Playing(){}

    public Playing(String playerOneId) {
        this.playerOneId = playerOneId;
        this.playerTwoId = "";
        this.selectedRow = new ArrayList<>();
        for (int i = 0 ; i<9; i++){
            this.selectedRow.add(new Integer(0));
        }
        this.turnPlayerOne = true;
        this.created = new Date();
        this.winnerId = "";
        this.lostPlay = "";
    }

    public String getPlayerOneId() {
        return playerOneId;
    }

    public void setPlayerOneId(String playerOneId) {
        this.playerOneId = playerOneId;
    }

    public String getPlayerTwoId() {
        return playerTwoId;
    }

    public void setPlayerTwoId(String playerTwoId) {
        this.playerTwoId = playerTwoId;
    }

    public List<Integer> getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(List<Integer> selectedRow) {
        this.selectedRow = selectedRow;
    }

    public boolean isTurnPlayerOne() {
        return turnPlayerOne;
    }

    public void setTurnPlayerOne(boolean turnPlayerOne) {
        this.turnPlayerOne = turnPlayerOne;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLostPlay() {
        return lostPlay;
    }

    public void setLostPlay(String lostPlay) {
        this.lostPlay = lostPlay;
    }
}
