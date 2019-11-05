/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Rate;
import java.util.List;


public interface RateSessionBeanRemote {

    public long createRate(Rate r, long categoryId);

    public List retrieveRates();

    public void updateRate(Rate r);

    public void deleteRate(Long rateId);
    
}
