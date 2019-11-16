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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 *
 * @author User
 */
@Entity
public class Model implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long modelId;
    
    @Column(nullable = false, length = 20)
    private String modelName;
    
    @Column(nullable = false, length = 20)
    private String make;
    
    @Column(nullable = false)
    private boolean active;
    
    @Transient
    private Double currFee;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Category category;
    
    @OneToMany(mappedBy = "model")
    private List<Car> car = new ArrayList<>();

    public Model() {
    }

    public Model(String modelName, String make) {
        this.modelName = modelName;
        this.make = make;
        this.active = true;
    }

    public Double getCurrFee() {
        return currFee;
    }

    public void setCurrFee(Double currFee) {
        this.currFee = currFee;
    }

    
    
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    
    
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Car> getCar() {
        return car;
    }
    
    
    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }
    
    
    
    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (modelId != null ? modelId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the modelId fields are not set
        if (!(object instanceof Model)) {
            return false;
        }
        Model other = (Model) object;
        if ((this.modelId == null && other.modelId != null) || (this.modelId != null && !this.modelId.equals(other.modelId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Model[ id=" + modelId + " ]";
    }
    
}
