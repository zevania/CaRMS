/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author User
 */
@Entity
public class Outlet implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long outletId;
    
    @Column(nullable = false)
    private Date openHrs;
    @Column(nullable = false)
    private Date closeHrs;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String address;
    
    @OneToMany(mappedBy = "outlet")
    private List<Car> cars = new ArrayList<>();
    
    @OneToMany(mappedBy = "pickupLocation")
    private List<Reservation> pickReservation = new ArrayList<>();
    
    @OneToMany(mappedBy = "returnLocation")
    private List<Reservation> returnReservation = new ArrayList<>();
    
    @OneToMany(mappedBy = "outlet")
    private List<Employee> employees = new ArrayList<>();
    
    @OneToMany(mappedBy = "outlet")
    private List<DriverDispatchRecord> dispatchRecords = new ArrayList<>();

    public Outlet() {
    }

    public Outlet(Date openHrs, Date closeHrs, String name, String address) {
        this.openHrs = openHrs;
        this.closeHrs = closeHrs;
        this.name = name;
        this.address = address;
    }

    public List<Car> getCars() {
        return cars;
    }

    public List<Reservation> getPickReservation() {
        return pickReservation;
    }

    public List<Reservation> getReturnReservation() {
        return returnReservation;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<DriverDispatchRecord> getDispatchRecords() {
        return dispatchRecords;
    }
    
    public Date getOpenHrs() {
        return openHrs;
    }

    public void setOpenHrs(Date openHrs) {
        this.openHrs = openHrs;
    }

    public Date getCloseHrs() {
        return closeHrs;
    }

    public void setCloseHrs(Date closeHrs) {
        this.closeHrs = closeHrs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    
    

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (outletId != null ? outletId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the outletId fields are not set
        if (!(object instanceof Outlet)) {
            return false;
        }
        Outlet other = (Outlet) object;
        if ((this.outletId == null && other.outletId != null) || (this.outletId != null && !this.outletId.equals(other.outletId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Outlet[ id=" + outletId + " ]";
    }
    
}
