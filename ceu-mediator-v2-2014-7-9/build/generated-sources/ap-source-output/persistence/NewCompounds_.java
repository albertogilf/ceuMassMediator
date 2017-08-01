package persistence;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import persistence.NewCompoundsHMDB;
import persistence.NewCompoundsIdentifiers;
import persistence.NewCompoundsKegg;
import persistence.NewCompoundsLM;
import persistence.NewCompoundsMetlin;
import persistence.NewCompoundsPC;
import persistence.NewLipidsClassification;
import persistence.NewPathways;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-08-01T13:44:15")
@StaticMetamodel(NewCompounds.class)
public class NewCompounds_ { 

    public static volatile SingularAttribute<NewCompounds, NewCompoundsMetlin> ncAgilent;
    public static volatile SingularAttribute<NewCompounds, NewCompoundsKegg> ncKegg;
    public static volatile SingularAttribute<NewCompounds, NewCompoundsLM> ncLM;
    public static volatile SingularAttribute<NewCompounds, Double> mass;
    public static volatile CollectionAttribute<NewCompounds, NewPathways> newPathwaysCollection;
    public static volatile SingularAttribute<NewCompounds, NewLipidsClassification> lipidClass;
    public static volatile SingularAttribute<NewCompounds, Integer> compoundType;
    public static volatile SingularAttribute<NewCompounds, String> casId;
    public static volatile SingularAttribute<NewCompounds, NewCompoundsHMDB> ncHMDB;
    public static volatile SingularAttribute<NewCompounds, String> formulaType;
    public static volatile SingularAttribute<NewCompounds, String> formula;
    public static volatile SingularAttribute<NewCompounds, NewCompoundsPC> ncPC;
    public static volatile SingularAttribute<NewCompounds, Integer> compoundId;
    public static volatile SingularAttribute<NewCompounds, NewCompoundsIdentifiers> ncIdentifier;
    public static volatile SingularAttribute<NewCompounds, String> compoundName;

}