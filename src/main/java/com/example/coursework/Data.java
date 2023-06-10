package com.example.coursework;

public class Data {
    private int groupNumber;
    private String name;
    private String path;

    public Data(int groupNumber, String name, String path) {
        this.groupNumber = groupNumber;
        this.name = name;
        this.path = path;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(int groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}