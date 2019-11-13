/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.OutletNotFoundException;

@Stateless
@Local(OutletSessionBeanLocal.class)
@Remote(OutletSessionBeanRemote.class)
public class OutletSessionBean implements OutletSessionBeanRemote, OutletSessionBeanLocal {
    
    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    @Override
    public long createOutlet(Outlet o) {
        em.persist(o);
        em.flush();
        return o.getOutletId();
    }
    
    @Override
    public List<Outlet> retrieveAllOutlets() {
        Query query = em.createQuery("SELECT o FROM Outlet o");
        return query.getResultList();
    }
    
    @Override
    public Outlet retrieveOutletById(Long outletId) throws OutletNotFoundException {
        try {
            return em.find(Outlet.class, outletId);
        }
        catch(NoResultException ex) {
            throw new OutletNotFoundException("Outlet not found!");
        }
    }
}
