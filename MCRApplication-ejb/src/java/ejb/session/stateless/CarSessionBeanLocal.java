/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import java.util.List;
import util.exception.CarNotFoundException;
import util.exception.InvalidModelException;
import util.exception.OutletNotFoundException;


public interface CarSessionBeanLocal {

    public long createCar(Car car, long modelId) throws InvalidModelException;
    
    public List<Car> retrieveCars();

    public void updateCar(Car c, long outletId, long modelId) throws InvalidModelException, OutletNotFoundException;

    public void deleteCar(long carId) throws CarNotFoundException;
    
}
