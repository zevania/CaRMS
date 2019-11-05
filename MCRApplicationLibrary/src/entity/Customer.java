/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import util.enumeration.CustomerTypeEnum;

/**
 *
 * @author User
 */
@Entity
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long custId;
    
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long ccNum;
    @Column(nullable = false)
    private String email;
    @Enumerated(EnumType.STRING)
    private CustomerTypeEnum customerType;
    
    
    @OneToMany(mappedBy = "customer")
    private List<Reservation> reservations = new ArrayList<>();
    
    public Customer() {
    }

    public Customer(String name, Long ccNum, String email, CustomerTypeEnum customerType) {
        this.name = name;
        this.ccNum = ccNum;
        this.email = email;
        this.customerType = customerType;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public void setCcNum(Long ccNum) {
        this.ccNum = ccNum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCustomerType(CustomerTypeEnum customerType) {
        this.customerType = customerType;
    }

    public String getName() {
        return name;
    }

    public Long getCcNum() {
        return ccNum;
    }

    public String getEmail() {
        return email;
    }

    public CustomerTypeEnum getCustomerType() {
        return customerType;
    }
    
    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (custId != null ? custId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the custId fields are not set
        if (!(object instanceof Customer)) {
            return false;
        }
        Customer other = (Customer) object;
        if ((this.custId == null && other.custId != null) || (this.custId != null && !this.custId.equals(other.custId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Customer[ id=" + custId + " ]";
    }
    
}
