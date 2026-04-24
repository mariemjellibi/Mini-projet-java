package Model;

import java.io.Serializable;

public class Quiz implements Serializable {
    private int id;
    private String title;
    private String createdBy;

    public Quiz() {}

    public Quiz(int id, String title, String createdBy) {
        this.id = id;
        this.title = title;
        this.createdBy = createdBy;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}