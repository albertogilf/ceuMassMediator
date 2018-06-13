/*
 * CompoundChain.java
 *
 * Created on 06-jun-2018, 22:06:01
 *
 * Copyright(c) 2018 Ceu Mass Mediator All Rights Reserved.
 * This software is the proprietary information of Alberto Gil de la Fuente.
 *
 */
package persistence;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * {Insert class description here}
 *
 * @version $Revision: 1.1.1.1 $
 * @since Build {insert version here} 06-jun-2018
 *
 * @author Alberto Gil de la Fuente
 */
@Entity(name = "CompoundChain")
@Table(name = "compound_chain")
@XmlRootElement
public class CompoundChain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of compoundChain
     */
    public CompoundChain() {

    }
    @EmbeddedId
    private KeyCompoundChain keyCompoundChain;

    public KeyCompoundChain getKeyCompoundChain() {
        return keyCompoundChain;
    }

    public void setKeyCompoundChain(KeyCompoundChain keyCompoundChain) {
        this.keyCompoundChain = keyCompoundChain;
    }

    /*
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "chain_id")
    private int chainId;
    
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "compound_id")
    private int compoundId;

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public int getCompoundId() {
        return compoundId;
    }

    public void setCompoundId(int compoundId) {
        this.compoundId = compoundId;
    }
     */
 /*
    @Id
    @AttributeOverrides({
        @AttributeOverride(name = "bk_name", column = @Column(name = "bk_name"))
        ,@AttributeOverride(name = "author_name", column = @Column(name = "author_name"))
    }
    )
     */
}
