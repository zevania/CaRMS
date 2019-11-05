/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import java.util.List;


public interface CarSessionBeanRemote {

    public long createCar(Car car, long modelId) throws Exception;
    public List<Car> retrieveCars();

    public void updateCar(Car c, long outletId, long modelId);

    public void deleteCar(long carId);
    
}
