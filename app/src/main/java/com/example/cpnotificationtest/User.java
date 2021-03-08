package com.example.cpnotificationtest;

import java.io.Serializable;

public class User  {
    private String handel;
    private int contribution;
    private String rank;
    private int maxRating;
    private String status;
    private String comment;

    public User(String handel, int contribution, String rank, int maxRating,String comment,String status) {
        this.handel = handel;
        this.contribution = contribution;
        this.rank = rank;
        this.maxRating = maxRating;
        this.comment = comment;
        this.status = status;
    }
    public User()
    {
          this("",0,"none",-1," uinitalized " , "Nothing");
    }

    public String getHandel() {
        return handel;
    }

    public void setHandel(String handel) {
        this.handel = handel;
    }

    public int getContribution() {
        return contribution;
    }

    public void setContribution(int contribution) {
        this.contribution = contribution;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getMaxRating() {
        return maxRating;
    }

    public void setMaxRating(int maxRating) {
        this.maxRating = maxRating;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
