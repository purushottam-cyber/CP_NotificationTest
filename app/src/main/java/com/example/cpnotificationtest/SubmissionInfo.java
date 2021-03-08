package com.example.cpnotificationtest;

public class SubmissionInfo {
    private int id;
    private  String verdict;
    private int passedCases;
    private String problemName;

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public SubmissionInfo(int id, String verdict, int passedCases, String problemName) {
        this.id = id;
        this.verdict = verdict;
        this.passedCases = passedCases;
        this.problemName = problemName;
    }
    public SubmissionInfo()
    {
        this(-1,"NOTHING",-1,"Empty");
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVerdict() {
        return verdict;
    }

    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }

    public int getPassedCases() {
        return passedCases;
    }

    public void setPassedCases(int passedCases) {
        this.passedCases = passedCases;
    }
}
