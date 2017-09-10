package com.google.firebase.codelab.friendlychat;

/**
 * Created by Vishal Anand on 11-04-2017.
 */

public class ScoreHolder {
    int score;
    int total;

    public ScoreHolder(int score, int total) {
        this.score = score;
        this.total = total;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ScoreHolder() {
    }
}
