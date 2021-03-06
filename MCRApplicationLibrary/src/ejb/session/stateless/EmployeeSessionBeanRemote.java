/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;


public interface EmployeeSessionBeanRemote {

    public long createEmployee(Employee e, long outletId);

    public Employee employeeLogin(String email, String password);
    
    public List<Employee> retrieveEmployees(long outletId);
    
}
