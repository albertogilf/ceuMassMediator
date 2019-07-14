package presentation.paginationHelpers;

import java.io.Serializable;

/**
 * Abstract class to paginate the results
 *
 * @author: San Pablo-CEU
 * @version: 3.1, 17/02/2016
 */
public abstract class PaginationHelper implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int pageSize;
    private int page;
    private final int numInputMasses;

    /**
     * Constructor PaginationHelper
     *
     * @param pageSize Size of the page
     * @param numInputMasses
     */
    public PaginationHelper(int pageSize, int numInputMasses) {
        this.pageSize = pageSize;
        this.numInputMasses = numInputMasses;
    }

    /**
     * @return the first item of the page
     */
    public int getPageFirstItem() {
        return getPage() * this.pageSize;
    }

    /**
     * @return the last item of the page
     */
    public int getPageLastItem() {
        int i = getPageFirstItem() + this.pageSize - 1;
        int count = this.numInputMasses - 1;
        if (i > count) {
            i = count;
        }
        if (i < 0) {
            i = 0;
        }
        return i;
    }

    /**
     * @return a boolean which shows if there is more pages in pagination
     */
    public boolean isHasNextPage() {
        return (getPage() + 1) * this.pageSize + 1 <= this.numInputMasses;
    }

    /**
     * changes to the next page
     */
    public void nextPage() {
        if (isHasNextPage()) {
            setPage(getPage() + 1);
        }
    }

    /**
     * @return a boolean which shows if there is previous pages in pagination
     */
    public boolean isHasPreviousPage() {
        return getPage() > 0;
    }

    /**
     * changes to the previous page
     */
    public void previousPage() {
        if (isHasPreviousPage()) {
            setPage(getPage() - 1);
        }
    }

    /**
     * @return the size of the pages
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return the actual page
     */
    public int getPage() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public void setPage(int page) {
        this.page = page;
    }
}
