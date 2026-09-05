package com.goffeebean.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Roast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String origin;

    @Enumerated(EnumType.STRING)
    private RoastLevel roastLevel;

    private BigDecimal price;

    private String tastingNotes;

    public Roast() {
    }

    public Roast(Long id, String name, String origin, RoastLevel roastLevel, BigDecimal price, String tastingNotes) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.roastLevel = roastLevel;
        this.price = price;
        this.tastingNotes = tastingNotes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public RoastLevel getRoastLevel() {
        return roastLevel;
    }

    public void setRoastLevel(RoastLevel roastLevel) {
        this.roastLevel = roastLevel;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTastingNotes() {
        return tastingNotes;
    }

    public void setTastingNotes(String tastingNotes) {
        this.tastingNotes = tastingNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Roast roast = (Roast) o;
        return Objects.equals(id, roast.id) && Objects.equals(name, roast.name)
                && Objects.equals(origin, roast.origin) && roastLevel == roast.roastLevel
                && Objects.equals(price, roast.price) && Objects.equals(tastingNotes, roast.tastingNotes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, origin, roastLevel, price, tastingNotes);
    }
}
