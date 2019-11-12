/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author User
 */
@Stateless
@Local(PartnerSessionBeanLocal.class)
@Remote(PartnerSessionBeanRemote.class)
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createPartner(Partner p) {
        em.persist(p);
        em.flush();
        return p.getPartnerId();
    }
    
    public Partner doPartnerLogin(String email, String password) throws PartnerNotFoundException, InvalidLoginCredentialException{
        Query query = em.createQuery("SELECT p FROM Partner p WHERE p.email = :inEmail")
                .setParameter("inEmail",email);
        Partner partner;
        try{
            partner = (Partner) query.getSingleResult();
        }catch(NoResultException ex){
            throw new PartnerNotFoundException();
        }
        
        if(!partner.getPassword().equals(password)){
            throw new InvalidLoginCredentialException();
        }
        
        return partner;
    }
}
