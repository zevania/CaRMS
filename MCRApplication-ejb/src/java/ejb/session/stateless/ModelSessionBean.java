/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Car;
import entity.Category;
import entity.Model;
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
@Local(ModelSessionBeanLocal.class)
@Remote(ModelSessionBeanRemote.class)
public class ModelSessionBean implements ModelSessionBeanRemote, ModelSessionBeanLocal {

    @PersistenceContext(unitName = "MCRApplication-ejbPU")
    private EntityManager em;
    
    @Override
    public long createModel(Model m, long categoryId) {
        Category c = em.find(Category.class, categoryId);
        
        em.persist(m);
        m.setCategory(c);
        c.getModel().add(m);
        em.flush();
        return m.getModelId();
    }
    
    @Override
    public List<Model> retrieveModels() {
        Query query = em.createQuery("SELECT m FROM Model m WHERE m.active IS TRUE");
        
        return query.getResultList();
    }
    
    @Override
    public void updateModel(Model m, Long categoryId) {
        Model oldModel = em.find(Model.class, m.getModelId());
        em.merge(m);
        Category newCategory = em.find(Category.class, categoryId);
        if(m.getCategory().getCategoryId() != categoryId) {
            m.setCategory(newCategory);
        }
        em.flush();
    }
    
    @Override
    public void deleteModel(Long modelId) {
        Model model = em.find(Model.class, modelId);
        if(model.isActive()) {
            model.setActive(false);
        } else {
            if(model.getCar().size() == 0) {
                model.getCategory().getModel().remove(model);
                model.setCategory(null);
                em.remove(model);
            }
        }
        em.flush();
    }
    
}
