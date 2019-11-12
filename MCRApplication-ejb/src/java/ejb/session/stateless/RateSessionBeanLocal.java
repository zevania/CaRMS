/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Rate;
import java.time.LocalDate;
import java.util.List;
import util.enumeration.CategoryNotFoundException;
import util.exception.RateNotFoundException;

public interface RateSessionBeanLocal {

    public long createRate(Rate r, long categoryId) throws CategoryNotFoundException;

    public List retrieveRates();

    public void updateRate(Rate r);

    public void deleteRate(Long rateId) throws RateNotFoundException;

    public Rate retrieveRateById(long rateId) throws RateNotFoundException;

    public double retrieveTotalByCategory(long catId, LocalDate startDate, LocalDate endDate) throws CategoryNotFoundException, RateNotFoundException;
    
}
