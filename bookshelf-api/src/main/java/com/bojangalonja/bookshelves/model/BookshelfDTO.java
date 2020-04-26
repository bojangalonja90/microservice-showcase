package com.bojangalonja.bookshelves.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookshelfDTO {

    private String id;

    private String userId;

    private String title;

    private String description;

    private List<String> items = new ArrayList<>();

    private Date created;

    private Date updated;

    private int volumesCount;

    public BookshelfDTO() {
    }

    public BookshelfDTO(String id, String userId, String title, String description, List<String> items, Date created, Date updated, int volumesCount) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.description = description;
        this.items = items;
        this.created = created;
        this.updated = updated;
        this.volumesCount = volumesCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public int getVolumesCount() {
        return volumesCount;
    }

    public void setVolumesCount(int volumesCount) {
        this.volumesCount = volumesCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
