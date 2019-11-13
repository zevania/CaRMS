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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author User
 */
@Entity
public class Rate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rateId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double rate;
    @Column(nullable = false)
    private Double peakRate;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date startPeriod;
    @Column(nullable = false)
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endPeriod;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;

    public Rate() {
    }

    public Rate(String name, Double rate, Date startPeriod, Date endPeriod, Category category) {
        this.name = name;
        this.rate = rate;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
        this.category = category;
    }

    public Rate(String name, Double rate, Double peakRate, Date startPeriod, Date endPeriod) {
        this.name = name;
        this.rate = rate;
        this.peakRate = peakRate;
        this.startPeriod = startPeriod;
        this.endPeriod = endPeriod;
    }

    public Double getPeakRate() {
        return peakRate;
    }

    public void setPeakRate(Double peakRate) {
        this.peakRate = peakRate;
    }
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Date getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Date startPeriod) {
        this.startPeriod = startPeriod;
    }

    public Date getEndPeriod() {
        return endPeriod;
    }

    public void setEndPeriod(Date endPeriod) {
        this.endPeriod = endPeriod;
    }
    
    
    
    public Long getRateId() {
        return rateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (rateId != null ? rateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the rateId fields are not set
        if (!(object instanceof Rate)) {
            return false;
        }
        Rate other = (Rate) object;
        if ((this.rateId == null && other.rateId != null) || (this.rateId != null && !this.rateId.equals(other.rateId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Rate[ id=" + rateId + " ]";
    }
    
}
