package com.example.todolist;

public class Task {
    private String content;
    private String date;
    private  boolean hiPriority;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isHiPriority() {
        return hiPriority;
    }

    public void setHiPriority(boolean hiPriority) {
        this.hiPriority = hiPriority;
    }

    public Task(String content, String date, boolean hiPriority) {
        this.content = content;
        this.date = date;
        this.hiPriority = hiPriority;
    }
}
