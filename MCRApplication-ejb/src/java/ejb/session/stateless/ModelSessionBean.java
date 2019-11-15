/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Category;
import entity.Model;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import util.enumeration.CategoryNotFoundException;
import util.exception.ModelNotFoundException;

/**
 *
 * @author User
 */
@Stateless
@Local(ModelSessionBeanLocal.class)
@Remote(ModelSessionBeanRemote.class)
public class ModelSessionBean implements ModelSessionBeanRemote, ModelSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    @Override
    public long createModel(Model m, long categoryId) throws CategoryNotFoundException {
        Category c = em.find(Category.class, categoryId);
        
        if(c==null) throw new CategoryNotFoundException();
        
        m.setCategory(c);
        c.getModel().add(m);
        try {
            em.persist(m);
            em.flush();
            return m.getModelId();
        } catch (PersistenceException ex) {
            System.out.println("Model name too long!");
            return -1;
        }
    }
    
    @Override
    public List<Model> retrieveModels() {
        Query query = em.createQuery("SELECT m FROM Model m WHERE m.active = TRUE ORDER BY m.category.categoryId ASC, m.make ASC, m.modelName ASC");
        
        return query.getResultList();
    }
    
    @Override
    public void updateModel(Model m, Long categoryId) throws CategoryNotFoundException {
        //Model oldModel = em.find(Model.class, m.getModelId());
        Category newCategory = em.find(Category.class, categoryId);
        
        if(newCategory == null) throw new CategoryNotFoundException();
        
        if(m.getCategory().getCategoryId() != categoryId) {
            m.setCategory(newCategory);
        }
        em.merge(m);
        em.flush();
    }
    
    @Override
    public void deleteModel(Long modelId) throws ModelNotFoundException {
        Model model = em.find(Model.class, modelId);
        
        if(model == null) throw new ModelNotFoundException();
        
        if(model.isActive()) {
            model.setActive(false);
        } 
        em.flush();
    }
    
    @Override
    public Model retrieveModelById(long modelId) {
        Model m = em.find(Model.class, modelId);
       
        return m;
    }
    
}
