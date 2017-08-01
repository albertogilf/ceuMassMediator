package persistence;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import persistence.NewCompounds;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-08-01T13:44:15")
@StaticMetamodel(NewPathways.class)
public class NewPathways_ { 

    public static volatile CollectionAttribute<NewPathways, NewCompounds> newCompoundsCollection;
    public static volatile SingularAttribute<NewPathways, Integer> pathwayId;
    public static volatile SingularAttribute<NewPathways, String> pathwayMap;
    public static volatile SingularAttribute<NewPathways, String> pathwayName;

}