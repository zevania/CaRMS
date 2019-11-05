/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Rate;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author User
 */
@Stateless
@Local(RateSessionBeanLocal.class)
@Remote(RateSessionBeanRemote.class)
public class RateSessionBean implements RateSessionBeanRemote, RateSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    
    @Override
    public long createRate(Rate r, long categoryId) {
        
        em.persist(r);
        Category category = em.find(Category.class, categoryId);
        r.setCategory(category);
        category.getRate().add(r);
        em.flush();
        
        return r.getRateId();
    }

    @Override
    public List<Rate> retrieveRates() {
        Query query = em.createQuery("SELECT r FROM Rate r");
        
        return query.getResultList();
    }

    @Override
    public void updateRate(Rate r) {
        em.merge(r);
    }

    @Override
    public void deleteRate(Long rateId) {
        Rate r = em.find(Rate.class, rateId);
        Category category = r.getCategory();
        category.getRate().remove(r);
        em.remove(r);
        em.flush();
        
    }
     
}
