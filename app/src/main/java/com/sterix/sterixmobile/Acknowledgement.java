package com.sterix.sterixmobile;

/**
 * Created by Ivan Escamos on 8/7/2017.
 */

public class Acknowledgement {

    private String term;
    private boolean accepted;


    public Acknowledgement(){}

    public Acknowledgement(String term, boolean accepted){

        this.term =term;
        this.accepted = accepted;
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public boolean isAccepted() {
        return this.accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
