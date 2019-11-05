/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DriverDispatchRecord;
import java.time.LocalDate;
import java.util.List;


public interface TransitDriverDispatchRecordSessionBeanLocal {

    public long createDispatchRecord(DriverDispatchRecord d, long resId, long outletId);

    public void assignDriver(long employeeId, long dispatchId);

    public void updateDispatchRecordAsCompleted(long dispatchId);

    public List<DriverDispatchRecord> retrieveDispatchRecords(long outletId, LocalDate date);
    
}
