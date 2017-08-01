package presentation;

import java.io.Serializable;
import javax.persistence.EntityManager;

  /**
   * Abstract class to work with entityManager
   * @author: Alberto Gil de la Fuente. San Pablo-CEU
   * @version: 3.1, 17/02/2016
   */
public abstract class AbstractFacade<T> implements Serializable{
    private Class<T> entityClass;

   /** 
     * constructor of abstract Class AbstractFacade
     * @param entityClass 
     * @return No return value
     * @exception exceptions No exceptions thrown
     */
    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
   /** 
     * Gets the EntityManager
     * @return EntityManager of the class
     * @exception exceptions No exceptions thrown
     */
    protected abstract EntityManager getEntityManager();
    
}
