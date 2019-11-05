/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Model;
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
@Local(CarSessionBeanLocal.class)
@Remote(CarSessionBeanRemote.class)
public class CarSessionBean implements CarSessionBeanRemote, CarSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    
    @Override
    public long createCar(Car car, long modelId) throws Exception {
        Model model = em.find(Model.class, modelId);
        
        if(!model.isActive()) throw new Exception();
        em.persist(car);
        
        car.setModel(model);
        model.getCar().add(car);
        em.flush();
        
        return car.getCarId();
    }

    @Override
    public List<Car> retrieveCars(){
        Query query = em.createQuery("SELECT c FROM Car c WHERE c.active IS TRUE");
        
        return query.getResultList();
    }

    @Override
    public void updateCar(Car c, long outletId, long modelId) {
        em.merge(c);
        em.flush();
        Model model = em.find(Model.class, modelId);
        long carId = c.getCarId();
        Car car = em.find(Car.class, carId);
        if(outletId != c.getOutlet().getOutletId()){
            Outlet old = em.find(Outlet.class,c.getOutlet().getOutletId());
            old.getCars().remove(c);
            Outlet newOutlet = em.find(Outlet.class,outletId);
            newOutlet.getCars().add(car);
            car.setOutlet(newOutlet);
        }
        
        if(modelId!=c.getModel().getModelId()){
            Model oldModel = em.find(Model.class, c.getModel().getModelId());
            oldModel.getCar().remove(c);
            model.getCar().add(car);
            car.setModel(model);
        }
    }

    @Override
    public void deleteCar(long carId) {
        Car c = em.find(Car.class, carId);
        c.setActive(false);
        em.flush();
    }
    
    
    
    
}
