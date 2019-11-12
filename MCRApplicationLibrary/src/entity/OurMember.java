/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author User
 */
@Entity
public class OurMember implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ourMemberId;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    public OurMember() {
    }

    public OurMember(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    public Long getOurMemberId() {
        return ourMemberId;
    }

    public void setOurMemberId(Long ourMemberId) {
        this.ourMemberId = ourMemberId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ourMemberId != null ? ourMemberId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ourMemberId fields are not set
        if (!(object instanceof OurMember)) {
            return false;
        }
        OurMember other = (OurMember) object;
        if ((this.ourMemberId == null && other.ourMemberId != null) || (this.ourMemberId != null && !this.ourMemberId.equals(other.ourMemberId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OurMember[ id=" + ourMemberId + " ]";
    }
    
}
