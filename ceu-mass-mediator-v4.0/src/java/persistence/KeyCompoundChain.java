/*
 * KeyCompoundChain.java
 *
 * Created on 06-jun-2018, 22:15:08
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package persistence;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 06-jun-2018
 *
 * @author Alberto Gil de la Fuente
 */
@Embeddable
public class KeyCompoundChain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "chain_id", nullable = false)
    private Integer chainId;

    @Column(name = "compound_id", nullable = false)
    private Integer compoundId;

    public KeyCompoundChain() {

    }

    /**
     * Creates a new instance of KeyCompoundChain
     *
     * @param chainId
     * @param compoundId
     */
    public KeyCompoundChain(final Integer chainId, final Integer compoundId) {
        this.chainId = chainId;
        this.compoundId = compoundId;
    }

    public Integer getChainId() {
        return chainId;
    }

    public void setChainId(Integer chainId) {
        this.chainId = chainId;
    }

    public Integer getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(Integer compoundId) {
        this.compoundId = compoundId;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KeyCompoundChain)) {
            return false;
        }
        KeyCompoundChain castOther = (KeyCompoundChain) other;
        return Objects.equals(chainId, castOther.chainId) && Objects.equals(compoundId, castOther.compoundId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChainId(), getCompoundId());
    }

}
