package com.example.studentgrievieance.model;

public class Category {
    private String name,icon,color,brief;
    private int id;
    public Category(String name,String icon,String color,String brief,int id)
    {
        this.name=name;
        this.brief=brief;
        this.color=color;
        this.id=id;
        this.icon=icon;
    }
        public int getId()
    {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }
}
