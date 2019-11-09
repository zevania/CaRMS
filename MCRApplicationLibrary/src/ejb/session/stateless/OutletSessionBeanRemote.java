/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Outlet;
import java.util.List;


public interface OutletSessionBeanRemote {
    
    public long createOutlet(Outlet o);
    
    public List<Outlet> retrieveAllOutlets();
    
}
