/*
 * CEMSAnnotationAdapter.java
 *
 * Created on 30-dic-2019, 19:24:04
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package CEMS;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 30-dic-2019
 *
 * @author Alberto Gil de la Fuente
 */
public interface CEMSAnnotationAdapter {

    public Integer getErrorMT();
    public Double getExp_mz();
    public Double getExp_MT();
    public Double getExp_RMT();
    public Double getExp_effMob();
    public Integer getErrorMZ();
    public Integer getErrorRMT();
    public Integer getErrorEffMob();
    public String getAdduct();

}
