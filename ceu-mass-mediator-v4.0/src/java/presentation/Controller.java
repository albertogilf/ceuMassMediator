package presentation;

import java.util.ArrayList;
import java.util.List;
import presentation.paginationHelpers.PaginationHelper;

  /**
   * Abstract class to work with controllers
   * @author: San Pablo-CEU
   * @version: 3.1, 17/02/2016
 * @param <T>
   */
public interface Controller<T> {
    List<T> getItems();
    
    List<T> getItemsGrouped();
    
    List<T> getItemsGroupedWithoutSignificative();
    
    
    
}
