package com.google.firebase.codelab.friendlychat;


/**
 * Created by Vishal Anand on 18-03-2017.
 */

public class CompRequest {
    String myName, opponentName, opponentEmail, myEmail;
    int isAccepted;

    public CompRequest(String myName, String opponentName, String opponentEmail, String myEmail,
                       int isAccepted) {
        this.myName = myName;
        this.opponentName = opponentName;
        this.opponentEmail = opponentEmail;
        this.myEmail = myEmail;
        this.isAccepted = isAccepted;
    }

    public String getMyName() {
        return myName;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public int isAccepted() {
        return isAccepted;
    }

    public void setAccepted(int accepted) {
        isAccepted = accepted;
    }

    public String getMyEmail() {
        return myEmail;
    }

    public void setMyEmail(String myEmail) {
        this.myEmail = myEmail;
    }

    public String getOpponentEmail() {
        return opponentEmail;
    }

    public void setOpponentEmail(String opponentEmail) {
        this.opponentEmail = opponentEmail;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public CompRequest() {
    }
}
