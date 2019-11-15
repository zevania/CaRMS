/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import util.enumeration.CarStatusEnum;

/**
 *
 * @author User
 */
@Entity
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;
    
    // don't need unique because plateNumber might be reused for different cars
    // after a certain period of time
    @Column(nullable = false)
    private String plateNumber;
    @Column(nullable = false)
    private String colour;
    @Enumerated(EnumType.STRING)
    private CarStatusEnum status;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private boolean active;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Model model;
    
    @OneToOne(mappedBy = "car")
    private Reservation reservation;
    
    @ManyToOne
    private Outlet outlet;

    public Car() {
    }

    public Car(String plateNumber, String colour, CarStatusEnum status, String location, Model model) {
        this.plateNumber = plateNumber;
        this.colour = colour;
        this.status = status;
        this.location = location;
        this.model = model;
        this.active = true;
    }

    public Car(String plateNumber, String colour, CarStatusEnum status, boolean active) {
        this.plateNumber = plateNumber;
        this.colour = colour;
        this.status = status;
        this.active = active;
    }    
    
    public Car(String plateNumber, String colour, CarStatusEnum status) {
        this.plateNumber = plateNumber;
        this.colour = colour;
        this.status = status;
        this.active = true;
    }
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
    }
    
    

    public Reservation getReservation() {
        return reservation;
    }

    public Outlet getOutlet() {
        return outlet;
    }
    
    
    
    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public CarStatusEnum getStatus() {
        return status;
    }

    public void setStatus(CarStatusEnum status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    
    
    public Long getCarId() {
        return carId;
    }


    @Override
    public int hashCode() {
        int hash = 0;
        hash += (carId != null ? carId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the carId fields are not set
        if (!(object instanceof Car)) {
            return false;
        }
        Car other = (Car) object;
        if ((this.carId == null && other.carId != null) || (this.carId != null && !this.carId.equals(other.carId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Car[ id=" + carId + " ]";
    }
    
}
