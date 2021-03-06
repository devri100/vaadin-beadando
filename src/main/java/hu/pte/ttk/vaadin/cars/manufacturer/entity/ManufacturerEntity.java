package hu.pte.ttk.vaadin.cars.manufacturer.entity;

import hu.pte.ttk.vaadin.cars.core.entity.CoreEntity;

import javax.persistence.*;

@Table(name = "manufacturer")
@Entity
public class ManufacturerEntity extends CoreEntity {

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
