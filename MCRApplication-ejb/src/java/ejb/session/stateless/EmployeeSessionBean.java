/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import entity.Outlet;
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
@Local(EmployeeSessionBeanLocal.class)
@Remote(EmployeeSessionBeanRemote.class)
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    @Override
    public long createEmployee(Employee e, long outletId) {
        em.persist(e);
        Outlet o = em.find(Outlet.class, outletId);
        o.getEmployees().add(e);
        e.setOutlet(o);
        em.flush();
        
        return e.getEmployeeId();
    }

    @Override
    public Employee employeeLogin(String email, String password) {
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.email LIKE :empEmail")
                .setParameter("empMail", email);
        Employee e =  (Employee) query.getSingleResult();
        
        if(e==null) return null;
        
        if(e.getPassword()!= password) return null;
        else return e;
    }
    
    public List<Employee> retrieveEmployees(long outletId){
        Query query = em.createQuery("SELECT e FROM Employee e WHERE e.outlet.outletId = :inOutlet")
                .setParameter("inOutlet", outletId);
        
        return query.getResultList();
    }
    
}
