/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CategorySessionBeanLocal;
import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.ModelSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RateSessionBeanLocal;
import entity.Car;
import entity.Category;
import entity.Employee;
import entity.Model;
import entity.Outlet;
import entity.Partner;
import entity.Rate;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.CarStatusEnum;
import util.enumeration.CategoryNotFoundException;
import util.enumeration.RoleEnum;
import util.exception.InvalidModelException;
import util.exception.OutletNotFoundException;

/**
 *
 * @author User
 */
@Singleton
@LocalBean
@Startup

public class DataInitializerSessionBean {

    @EJB
    private RateSessionBeanLocal rateSessionBeanLocal;
    @EJB
    private CarSessionBeanLocal carSessionBeanLocal;
    @EJB
    private ModelSessionBeanLocal modelSessionBeanLocal;
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
        if(em.find(Model.class, 1l) == null) {
            try {
                initializeModelData();
            } catch (CategoryNotFoundException ex) {
                System.out.println("Category not found");
            }
        }
        if(em.find(Car.class, 1l) == null) {
            try {
                initializeCarData();
            } catch (InvalidModelException ex) {
                System.out.println("Model not found");
            } catch (OutletNotFoundException ex) {
                System.out.println("Outlet not found");
            }
        }
        if(em.find(Rate.class, 1l) == null) {
            try {
                initializeRateData();
            } catch (CategoryNotFoundException ex) {
                System.out.println("Category not found");
            }
        }
        
    }
    
    private void initializeOutletData()
    {
        Outlet o;
        o = new Outlet(new Date(2000,1,1,0, 0, 0), new Date(2000,1,1,23,59,0), "Outlet A", "Ang Mo Kio");
        outletSessionBeanLocal.createOutlet(o);
        o = new Outlet(new Date(2000,1,1,0, 0, 0), new Date(2000,1,1,23,59,0), "Outlet B", "Orchard");
        outletSessionBeanLocal.createOutlet(o);
        o = new Outlet(new Date(2000,1,1,10, 0, 0), new Date(2000,1,1,22,0,0), "Outlet C", "Bugis");
        outletSessionBeanLocal.createOutlet(o);
    }
    
    private void initializeEmployeeData()
    {
        Employee e;
        e = new Employee("Employee A1", "employeeA1@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Employee A2", "employeeA2@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Employee A3", "employeeA3@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Employee A4", "employeeA4@gmail.com", "password", RoleEnum.EMPLOYEE);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        e = new Employee("Employee A5", "employeeA5@gmail.com", "password", RoleEnum.EMPLOYEE);
        employeeSessionBeanLocal.createEmployee(e, 1l);
        
        e = new Employee("Employee B1", "employeeB1@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        e = new Employee("Employee B2", "employeeB2@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        e = new Employee("Employee B3", "employeeB3@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
        employeeSessionBeanLocal.createEmployee(e, 2l);
        
        e = new Employee("Employee C1", "employeeC1@gmail.com", "password", RoleEnum.SALES);
        employeeSessionBeanLocal.createEmployee(e, 3l);
        e = new Employee("Employee C2", "employeeC2@gmail.com", "password", RoleEnum.OPERATION);
        employeeSessionBeanLocal.createEmployee(e, 3l);
        e = new Employee("Employee C3", "employeeC3@gmail.com", "password", RoleEnum.CUSTOMERSERVICE);
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
        c = new Category("Standard Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("Family Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("Luxury Sedan");
        categorySessionBeanLocal.createCategory(c);
        c = new Category("SUV and Minivan");
        categorySessionBeanLocal.createCategory(c);
    }
    
    private void initializeModelData() throws CategoryNotFoundException 
    {
        Model m;
        m = new Model("Corolla", "Toyota");
        modelSessionBeanLocal.createModel(m, 1);
        m = new Model("Civic", "Honda");
        modelSessionBeanLocal.createModel(m, 1);
        m = new Model("Sunny", "Nissan");
        modelSessionBeanLocal.createModel(m, 1);
        m = new Model("E Class", "Mercedes");
        modelSessionBeanLocal.createModel(m, 3);
        m = new Model("5 Series", "BMW");
        modelSessionBeanLocal.createModel(m, 3);
        m = new Model("A6", "Audi");
        modelSessionBeanLocal.createModel(m, 3);
    }
    
    private void initializeCarData() throws InvalidModelException, OutletNotFoundException 
    {
        Car c;
        c = new Car("SS00A1TC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 1, 1);
        c = new Car("SS00A2TC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 1, 1);
        c = new Car("SS00A3TC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 1, 1);
        
        c = new Car("SS00B1HC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 2, 2);
        c = new Car("SS00B2HC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 2, 2);
        c = new Car("SS00B3HC", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 2, 2);
        
        c = new Car("SS00C1NS", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 3, 3);
        c = new Car("SS00C2NS", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 3, 3);
        c = new Car("SS00C3NS", "Black", CarStatusEnum.REPAIR);
        carSessionBeanLocal.createCar(c, 3, 3);
        
        c = new Car("LS00A4ME", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 4, 1);
        c = new Car("LS00B4B5", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 5, 2);
        c = new Car("LS00C4A6", "Black", CarStatusEnum.OUTLET);
        carSessionBeanLocal.createCar(c, 6, 3);
    }
    
    private void initializeRateData() throws CategoryNotFoundException 
    {
        Rate r;
        r = new Rate("Default", 100.0, 100.0, new Date(0L), new Date(2100,10,12));
        rateSessionBeanLocal.createRate(r, 1);
        r = new Rate("Weekend Promo", 80.0, 80.0, new Date(119,11,6,12,0,0), new Date(119,11,8,0,0,0));
        rateSessionBeanLocal.createRate(r, 1);
        r = new Rate("Default", 200.0, 200.0, new Date(0L), new Date(2100,10,12));
        rateSessionBeanLocal.createRate(r, 2);
        r = new Rate("Monday", 310.0, 310.0, new Date(119,11,2,0,0,0), new Date(119,11,2,23,59,0));
        rateSessionBeanLocal.createRate(r, 3);
        r = new Rate("Tuesday", 320.0, 320.0, new Date(119,11,3,0,0,0), new Date(119,11,3,23,59,0));
        rateSessionBeanLocal.createRate(r, 3);
        r = new Rate("Wednesday", 330.0, 330.0, new Date(119,11,4,0,0,0), new Date(119,11,4,23,59,0));
        rateSessionBeanLocal.createRate(r, 3);
        r = new Rate("Weekday Promo", 250.0, 250.0, new Date(119,11,4,12,0,0), new Date(119,11,5,12,0,0));
        rateSessionBeanLocal.createRate(r, 3);
    }
    
}
