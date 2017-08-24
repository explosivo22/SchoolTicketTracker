package com.ocboe.tech.schooltickettracker;

/**
 * Created by Brad on 8/23/2017.
 */

public class Inventory {
    private String Tag;
    private String Type;
    private String Brand;
    private String Model;
    private String Serial;

    public Inventory(String Tag, String Type, String Brand, String Model, String Serial){
        this.Tag = Tag;
        this.Type = Type;
        this.Brand = Brand;
        this.Model = Model;
        this.Serial = Serial;
    }

    public String getTag() { return Tag; }

    public void setTag(String Tag) { this.Tag = Tag; }

    public String getType() { return Type; }

    public void setType(String Type) { this.Type = Type; }

    public String getBrand() { return Brand; }

    public void setBrand(String Brand) { this.Brand = Brand; }

    public String getModel() { return Model; }

    public void setModel(String Model) { this.Model = Model; }

    public String getSerial() { return Serial; }

    public void setSerial(String Serial) { this.Serial = Serial; }
}
