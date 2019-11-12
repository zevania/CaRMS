/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Model;
import java.util.List;
import util.enumeration.CategoryNotFoundException;
import util.exception.ModelNotFoundException;


public interface ModelSessionBeanLocal {

    public long createModel(Model m, long categoryId) throws CategoryNotFoundException;

    public List<Model> retrieveModels();

    public void updateModel(Model m, Long categoryId) throws CategoryNotFoundException;

    public void deleteModel(Long modelId) throws ModelNotFoundException;

    public Model retrieveModelById(long modelId);
    
}
