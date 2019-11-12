/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OurMember;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.MemberEmailExistException;

/**
 *
 * @author User
 */
@Stateless
@Local(MemberSessionBeanLocal.class)
@Remote(MemberSessionBeanRemote.class)
public class MemberSessionBean implements MemberSessionBeanRemote, MemberSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createMember(OurMember m) throws MemberEmailExistException {
        Query query = em.createQuery("SELECT m FROM OurMember m WHERE m.email = :inEmail");
        query.setParameter("inEmail", m.getEmail());
        
        OurMember member;
            
        try{
            member = (OurMember) query.getSingleResult();
        }catch(NoResultException ex){
            em.persist(m);
            em.flush();
            return m.getOurMemberId();
        }
            
            throw new MemberEmailExistException("Email already exist!");
        
    }
    
    @Override
    public OurMember memberLogin(String email, String password) throws InvalidLoginCredentialException  {
        Query query = em.createQuery("SELECT m FROM OurMember m WHERE m.email = :inEmail");
        query.setParameter("inEmail", email);
        OurMember m;
        
        try {
            m = (OurMember)query.getSingleResult();
            if(m.getPassword().equals(password)) {
                return m;
            } else {
                throw new InvalidLoginCredentialException("Invalid password!");
            }
        } catch (NoResultException ex) {
            throw new InvalidLoginCredentialException("Member not found!");
        }
        
        
    }
    
}
