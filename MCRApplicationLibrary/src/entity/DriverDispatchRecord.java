/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
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
import util.enumeration.DispatchStatusEnum;

/**
 *
 * @author User
 */
@Entity
public class DriverDispatchRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispatchId;
    
    @Enumerated(EnumType.STRING)
    private DispatchStatusEnum dispatchStatus;
    @Column(nullable = false)
    private Date dispatchDate;
    @Column(nullable = false)
    private Time dispatchTime;
    @Column(nullable = false)
    private String fromOutlet;
    @Column(length = 50)
    private String outletName;
    
    
    
    @OneToOne
    @JoinColumn(nullable = false)
    private Reservation reservation;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Outlet outlet;
    
    @ManyToOne
    private Employee employee;

    public DriverDispatchRecord() {
    }



    public DriverDispatchRecord(DispatchStatusEnum dispatchStatus, Date dispatchDate, Time dispatchTime, String fromOutlet) {
        this.dispatchStatus = dispatchStatus;
        this.dispatchDate = dispatchDate;
        this.dispatchTime = dispatchTime;
        this.fromOutlet = fromOutlet;
    }

    public String getFromOutlet() {
        return fromOutlet;
    }

    public void setFromOutlet(String fromOutlet) {
        this.fromOutlet = fromOutlet;
    }

    
    public DispatchStatusEnum getDispatchStatus() {
        return dispatchStatus;
    }

    public void setDispatchStatus(DispatchStatusEnum dispatchStatus) {
        this.dispatchStatus = dispatchStatus;
    }

    public Date getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(Date dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public Time getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(Time dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Outlet getOutlet() {
        return outlet;
    }

    public void setOutlet(Outlet outlet) {
        this.outlet = outlet;
        this.outletName = outlet.getName();
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public Long getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(Long dispatchId) {
        this.dispatchId = dispatchId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dispatchId != null ? dispatchId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the dispatchId fields are not set
        if (!(object instanceof DriverDispatchRecord)) {
            return false;
        }
        DriverDispatchRecord other = (DriverDispatchRecord) object;
        if ((this.dispatchId == null && other.dispatchId != null) || (this.dispatchId != null && !this.dispatchId.equals(other.dispatchId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.DriverDispatchRecord[ id=" + dispatchId + " ]";
    }
    
}
