/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

/**
 *
 * @author User
 */
@Stateless
@Local(CategorySessionBeanLocal.class)
@Remote(CategorySessionBeanRemote.class)
public class CategorySessionBean implements CategorySessionBeanRemote, CategorySessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;

    @Override
    public long createCategory(Category c) {
        try 
        {
            em.persist(c);
            em.flush();
            return c.getCategoryId();
        }
        catch (PersistenceException ex) 
        {
            return -1;
        }
    }

    public List<Category> retrieveAllCategories(){
        Query query = em.createQuery("SELECT c FROM Category c");
        List<Category> categories = query.getResultList();
        for(Category c: categories){
            c.getModel().size();
        }
        return categories;
        
    }
    
}
