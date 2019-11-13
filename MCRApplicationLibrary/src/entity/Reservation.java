/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
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
import javax.persistence.Temporal;
import util.enumeration.OrderTypeEnum;
import util.enumeration.PaidStatusEnum;
import util.enumeration.ResStatusEnum;

/**
 *
 * @author User
 */
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @Enumerated(EnumType.STRING)
    private PaidStatusEnum paymentStatus;
    @Column(nullable = false)
    private Double total;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date pickupDate;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date returnDate;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date pickupTime;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date returnTime;
    @Enumerated(EnumType.STRING)
    private ResStatusEnum resStatus;
    @Enumerated(EnumType.STRING)
    private OrderTypeEnum orderType;
    @Column(nullable = true)
    private double penalty;
    
    @ManyToOne
    private Category carCategory;
    
    @ManyToOne
    private Model carModel;
    
    @OneToOne
    private Car car;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Outlet pickupLocation;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Outlet returnLocation;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Customer customer;
    
    @ManyToOne
    private Partner partner;
    
    @OneToOne(mappedBy = "reservation")
    private DriverDispatchRecord driverDispatchRecord;
    
    public Reservation() {
    }

    public Reservation(PaidStatusEnum paymentStatus, Double total, Date pickupDate, Date returnDate, Date pickupTime, Date returnTime, ResStatusEnum resStatus, OrderTypeEnum orderType, Category carCategory, Model carModel, Car car, Outlet pickupLocation, Outlet returnLocation, Customer customer) {
        this.paymentStatus = paymentStatus;
        this.total = total;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.pickupTime = pickupTime;
        this.returnTime = returnTime;
        this.resStatus = resStatus;
        this.orderType = orderType;
        this.carCategory = carCategory;
        this.carModel = carModel;
        this.car = car;
        this.pickupLocation = pickupLocation;
        this.returnLocation = returnLocation;
        this.customer = customer;
    }

    public Reservation(PaidStatusEnum paymentStatus, Double total, Date pickupDate, Date returnDate, Date pickupTime, Date returnTime, OrderTypeEnum orderType, Customer customer) {
        this.paymentStatus = paymentStatus;
        this.total = total;
        this.pickupDate = pickupDate;
        this.returnDate = returnDate;
        this.pickupTime = pickupTime;
        this.returnTime = returnTime;
        this.orderType = orderType;
        this.customer = customer;
        this.resStatus = ResStatusEnum.ORDERED;
    }

    public double getPenalty() {
        return penalty;
    }

    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }
    
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Partner getPartner() {
        return partner;
    }

    public DriverDispatchRecord getDriverDispatchRecord() {
        return driverDispatchRecord;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public void setDriverDispatchRecord(DriverDispatchRecord driverDispatchRecord) {
        this.driverDispatchRecord = driverDispatchRecord;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Outlet getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Outlet pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Outlet getReturnLocation() {
        return returnLocation;
    }

    public void setReturnLocation(Outlet returnLocation) {
        this.returnLocation = returnLocation;
    }

    
    
    public OrderTypeEnum getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderTypeEnum orderType) {
        this.orderType = orderType;
    }

    public Category getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(Category carCategory) {
        this.carCategory = carCategory;
    }

    public Model getCarModel() {
        return carModel;
    }

    public void setCarModel(Model carModel) {
        this.carModel = carModel;
    }
    
    public PaidStatusEnum getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaidStatusEnum paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Date getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Date returnTime) {
        this.returnTime = returnTime;
    }

    public ResStatusEnum getResStatus() {
        return resStatus;
    }

    public void setResStatus(ResStatusEnum resStatus) {
        this.resStatus = resStatus;
    }
    
    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the reservationId fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }
    
}
