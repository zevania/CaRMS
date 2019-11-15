/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Rate;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
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
        List<Rate> rates = query.getResultList();
//        for(Rate r: rates){
//            System.out.println("start "+r.getStartPeriod());
//            System.out.println("end "+r.getEndPeriod());
//            System.out.println();
//        }
        
        return rates;
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
    public double retrieveTotalByCategory(long catId, Date startDate, Date endDate) throws CategoryNotFoundException, RateNotFoundException{
        double total = 0;
        
        Category category = em.find(Category.class, catId);
        if(category == null) throw new CategoryNotFoundException();
        
        Date temp = startDate;
        Date onedaybefore = startDate;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(temp);
        calendar.add(Calendar.DATE,1);
        temp = calendar.getTime();
        
        Query query;
        
        while(!temp.after(endDate)){
            if(temp.getDay()==5 || temp.getDay()==6 || temp.getDay()==0){
                query = em.createQuery("SELECT r FROM Rate r WHERE r.category.categoryId = :inCat AND r.startPeriod <= :inStart AND r.endPeriod >= :inStart AND r.startPeriod<= :inEnd AND r.endPeriod >= :inEnd ORDER BY r.peakRate ASC")
                        .setParameter("inCat", catId)
                        .setParameter("inStart", temp)
                        .setParameter("inEnd", onedaybefore);
                
                List<Rate> peakrates = query.getResultList();
                if(peakrates.size()==0) throw new RateNotFoundException();
                
                int idx = 0;
                boolean found = false;
                
                //to check for boundary case, make sure the rate is valid for the reservation endTime also
                //if the reservation time is later than the rate ending period
                //means we cannot use
                for(int i = 0; i < peakrates.size();i++){
                    Date toCheck = peakrates.get(i).getEndPeriod();
                    if(!toCheck.before(temp)){
                        if(toCheck.getDate()==endDate.getDate() && toCheck.getMonth()==endDate.getMonth() && toCheck.getYear()==endDate.getYear()){
                            if(toCheck.after(endDate)) continue;
                        }
                        idx = i;
                        found = true;
                        break;
                    }
                }
                if(!found) throw new RateNotFoundException();
                total += peakrates.get(idx).getPeakRate();
            } else {
                query = em.createQuery("SELECT r FROM Rate r WHERE r.category.categoryId = :inCat AND r.startPeriod <= :inStart AND r.endPeriod >= :inEnd AND r.startPeriod<= :inStart AND r.endPeriod >= :inEnd ORDER BY r.rate ASC")
                        .setParameter("inCat", catId)
                        .setParameter("inStart", temp)
                        .setParameter("inEnd", onedaybefore);
                
                List<Rate> rates = query.getResultList();
                if(rates.size()==0) throw new RateNotFoundException();

                int idx = 0;
                boolean found = false;
                
                //to check for boundary case, make sure the rate is valid for the reservation endTime also
                //if the reservation time is later than the rate ending period
                //means we cannot use
                for(int i = 0; i < rates.size();i++){
                    Date toCheck = rates.get(i).getEndPeriod();
                    if(!toCheck.before(temp)){
                        if(toCheck.getDate()==endDate.getDate() && toCheck.getMonth()==endDate.getMonth() && toCheck.getYear()==endDate.getYear()){
                            if(toCheck.after(endDate)) continue;
                        }    
                        idx = i;
                        found = true;
                        break;
                    }
                }
                if(!found) throw new RateNotFoundException();
                total += rates.get(idx).getRate();
            }
            calendar.add(Calendar.DATE,1);
            onedaybefore = temp;
            temp = calendar.getTime();  
        }
        
        if(temp.before(endDate)) throw new RateNotFoundException();
        
        
        return total;
    }
}
