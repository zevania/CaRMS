/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.DriverDispatchRecord;
import entity.Employee;
import entity.Outlet;
import entity.Reservation;
import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.DispatchStatusEnum;
import util.exception.DDRCompletedException;
import util.exception.DDRNotFoundException;
import util.exception.EmployeeNotFoundException;

/**
 *
 * @author User
 */
@Stateless
@Local(TransitDriverDispatchRecordSessionBeanLocal.class)
@Remote(TransitDriverDispatchRecordSessionBeanRemote.class)
public class TransitDriverDispatchRecordSessionBean implements TransitDriverDispatchRecordSessionBeanRemote, TransitDriverDispatchRecordSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    @Override
    public long createDispatchRecord(DriverDispatchRecord d, long resId, long outletId) {
        em.persist(d);
        Reservation r = em.find(Reservation.class,resId);
        Outlet o = em.find(Outlet.class, outletId);
        
        r.setDriverDispatchRecord(d);
        d.setReservation(r);
        
        o.getDispatchRecords().add(d);
        d.setOutlet(o);
        
        em.flush();
        return d.getDispatchId();
    }

    @Override
    public void assignDriver(long employeeId, long dispatchId) throws EmployeeNotFoundException, DDRNotFoundException {
        Employee e = em.find(Employee.class, employeeId);
        DriverDispatchRecord ddr = em.find(DriverDispatchRecord.class, dispatchId);
        
        if(e==null) throw new EmployeeNotFoundException();
        if(ddr==null) throw new DDRNotFoundException();
        
        e.getDispatchRecords().add(ddr);
        ddr.setEmployee(e);
        em.flush();
        
    }

    @Override
    public void updateDispatchRecordAsCompleted(long dispatchId) throws DDRNotFoundException, DDRCompletedException {
        DriverDispatchRecord ddr = em.find(DriverDispatchRecord.class, dispatchId);
        
        if(ddr==null) throw new DDRNotFoundException();
        if(ddr.getDispatchStatus()==DispatchStatusEnum.COMPLETED) throw new DDRCompletedException();
        
        Car c = ddr.getReservation().getCar();
        c.setOutlet(ddr.getReservation().getPickupLocation());
        c.setLocation(ddr.getReservation().getPickupLocation().getName());
        ddr.setDispatchStatus(DispatchStatusEnum.COMPLETED);
        
        em.flush();
    }

    @Override
    public List<DriverDispatchRecord> retrieveDispatchRecords(long outletId, Date date) {
        Outlet o = em.find(Outlet.class, outletId);
        
        Query query = em.createQuery("SELECT ddr FROM DriverDispatchRecord ddr WHERE ddr.outlet.outletId = :theOutlet AND ddr.dispatchDate = :theDate")
                    .setParameter("theDate", date)
                    .setParameter("theOutlet",outletId);
        List<DriverDispatchRecord> theList = query.getResultList();
        return theList;
             
                
    }
    
    
    
}
