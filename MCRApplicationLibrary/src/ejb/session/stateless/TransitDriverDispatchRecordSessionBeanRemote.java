/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DriverDispatchRecord;
import java.util.Date;
import java.util.List;
import util.exception.DDRCompletedException;
import util.exception.DDRNotFoundException;
import util.exception.EmployeeNotFoundException;


public interface TransitDriverDispatchRecordSessionBeanRemote {

    public long createDispatchRecord(DriverDispatchRecord d, long resId, long outletId);

    public void assignDriver(long employeeId, long dispatchId) throws EmployeeNotFoundException, DDRNotFoundException;

    public void updateDispatchRecordAsCompleted(long dispatchId)throws DDRNotFoundException, DDRCompletedException;

    public List<DriverDispatchRecord> retrieveDispatchRecords(long outletId, Date date);
    
}
