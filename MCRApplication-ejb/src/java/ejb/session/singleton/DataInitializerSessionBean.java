/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import entity.Category;
import entity.Employee;
import entity.Outlet;
import entity.Partner;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.RoleEnum;

/**
 *
 * @author User
 */
@Singleton
@LocalBean
@Startup

public class DataInitializerSessionBean {

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBeanLocal;
    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;
    @EJB
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    @EJB
    private CategorySessionBeanLocal categorySessionBeanLocal;
    
    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    public DataInitializerSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        if(em.find(Outlet.class, 1l) == null) {
            initializeOutletData();
        }
        if(em.find(Employee.class, 1l) == null) {
            initializeEmployeeData();
        }
        if(em.find(Partner.class, 1l) == null) {
            initializePartnerData();
        }
        if(em.find(Category.class, 1l) == null) {
            initializeCategoryData();
        }
        
    }
    
    private void initializeOutletData()
    {
        Outlet o;
        o = new Outlet(new Date(0,0,0,9, 0, 0), new Date(0,0,0,21,0,0), "Outlet 1", "Ang Mo Kio");
        outletSessionBeanLocal.createOutlet(o);
        o = new Outlet(new Date(0,0,0,9, 0, 0), new Date(0,0,0,21,0,0), "Outlet 2", "Orchard");
        outletSessionBeanLocal.createOutlet(o);
        o = new Outlet(new Date(0,0,0,9, 0, 0), new Date(0,0,0,21,0,0), "Outlet 3", "Bugis");
        outletSessionBeanLocal.createOutlet(o);
    }
    
    private void initializeEmployeeData()
    {
        Employee e;
        e = new Employee("Sales Manager 1", "sales1@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Operations Manager 1", "operations1@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Customer Service Executive 1", "custservice1@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        
        e = new Employee("Sales Manager 2", "sales2@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        e = new Employee("Operations Manager 2", "operations2@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        e = new Employee("Customer Service Executive 2", "custservice2@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        
        e = new Employee("Sales Manager 3", "sales3@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 3l);
        e = new Employee("Operations Manager 3", "operations3@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 3l);
        e = new Employee("Customer Service Executive 3", "custservice3@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
        employeeSessionBeanLocal.createEmployee(e, 3l);
    }
    
    private void initializePartnerData() 
    {
        Partner p;
        p = new Partner("Holiday.com", "holiday@gmail.com", "password");
        partnerSessionBeanLocal.createPartner(p);
    }
    
    private void initializeCategoryData() 
    {
        Category c;
        c = new Category("Luxury Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("Family Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("Standard Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("SUV/Minivan");
        categorySessionBeanLocal.createCategory(c);
    }
    
}
