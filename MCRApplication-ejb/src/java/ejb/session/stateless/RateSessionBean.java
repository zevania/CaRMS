/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Rate;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.CategoryNotFoundException;
import util.exception.RateNotFoundException;

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
    public long createRate(Rate r, long categoryId) throws CategoryNotFoundException {
        
        Category category = em.find(Category.class, categoryId);
        
        if(category == null) throw new CategoryNotFoundException();
        
        r.setCategory(category);
        category.getRate().add(r);
        em.persist(r);
        em.flush();
        
        return r.getRateId();
    }

    @Override
    public List<Rate> retrieveRates() {
        Query query = em.createQuery("SELECT r FROM Rate r ORDER BY r.category.categoryId ASC, r.startPeriod ASC, r.endPeriod ASC");
        
        return query.getResultList();
    }

    @Override
    public void updateRate(Rate r) {
        em.merge(r);
    }

    @Override
    public void deleteRate(Long rateId) throws RateNotFoundException {
        Rate r = em.find(Rate.class, rateId);
        if(r == null) throw new RateNotFoundException();
        
        Category category = em.find(Category.class, r.getCategory().getCategoryId());
        category.getRate().remove(r);
        em.remove(r);
        em.flush();
        
    }
    
    @Override
    public Rate retrieveRateById(long rateId) throws RateNotFoundException{
        Rate r = em.find(Rate.class, rateId);
        
        if(r==null) throw new RateNotFoundException();
        
        return r;
    }
     
    @Override
    public double retrieveTotalByCategory(long catId, LocalDate startDate, LocalDate endDate) throws CategoryNotFoundException, RateNotFoundException{
        double total = 0;
        
        Category category = em.find(Category.class, catId);
        if(category == null) throw new CategoryNotFoundException();
        
        LocalDate temp = startDate;
        
        Query query;
        
        while(!temp.isAfter(endDate)){
            if(temp.getDayOfWeek()==DayOfWeek.FRIDAY || temp.getDayOfWeek()==DayOfWeek.SATURDAY || temp.getDayOfWeek() == DayOfWeek.SUNDAY){
                query = em.createQuery("SELECT r FROM Rate r WHERE r.category.categoryId = :inCat AND r.startPeriod <= :inStart AND r.endPeriod >= :inEnd ORDER BY r.peakRate ASC")
                        .setParameter("inCat", catId)
                        .setParameter("inStart", temp)
                        .setParameter("inEnd", temp);
                
                List<Rate> peakrates = query.getResultList();
                if(peakrates.size()==0) throw new RateNotFoundException();
                total += peakrates.get(0).getPeakRate();
            } else {
                query = em.createQuery("SELECT r FROM Rate r WHERE r.category.categoryId = :inCat AND r.startPeriod <= :inStart AND r.endPeriod >= :inEnd ORDER BY r.rate ASC")
                        .setParameter("inCat", catId)
                        .setParameter("inStart", temp)
                        .setParameter("inEnd", temp);
                List<Rate> rates = query.getResultList();
                if(rates.size()==0) throw new RateNotFoundException();
                total+= rates.get(0).getRate();
            }
            
        }
        return total;
    }
}
