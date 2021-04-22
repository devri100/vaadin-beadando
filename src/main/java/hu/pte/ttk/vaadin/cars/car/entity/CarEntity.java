package hu.pte.ttk.vaadin.cars.car.entity;

import hu.pte.ttk.vaadin.cars.manufacturer.entity.ManufacturerEntity;
import hu.pte.ttk.vaadin.cars.core.entity.CoreEntity;

import javax.persistence.*;

@Table(name = "car")
@Entity
public class CarEntity extends CoreEntity {

    @ManyToOne
    @JoinColumn(name="manufacturer_id")
    private ManufacturerEntity manufacturer;

    @Column(name = "type")
    private String type;

    @Column(name = "number_of_doors")
    private Integer numberOfDoors;

    @Column(name = "year")
    private Integer year;

    public ManufacturerEntity getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(ManufacturerEntity manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNumberOfDoors() {
        return numberOfDoors == null ? 0 : numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public int getYear() {
        return year == null ? 0 : year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
