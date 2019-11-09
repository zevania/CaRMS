/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Model;
import java.util.List;


public interface ModelSessionBeanLocal {

    public long createModel(Model m, long categoryId);

    public List<Model> retrieveModels();

    public void updateModel(Model m, Long categoryId);

    public void deleteModel(Long modelId);
    
}
