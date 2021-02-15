package services.rest.api.response;

/**
 *
 * @author Sergio Saugar <sergio.saugargarcia@ceu.es>
 */
public class ClassyfireNodeView extends View {


    private static final String title = "Classyfire Node";

    private final String node_name;
    private final String node_id;
    private final String kingdom;
    private final String super_class;
    private final String main_class;
    private final String sub_class;
    private final String direct_parent;
    private final String level_7;
    private final String level_8;
    private final String level_9;
    private final String level_10;
    private final String level_11;



    public ClassyfireNodeView(String node_name, String node_id, String kingdom, String super_class,
                              String main_class, String sub_class,
                              String direct_parent, String level_7,
                              String level_8, String level_9, String level_10,
                              String level_11,
                              String created,
                              String last_updated) {
        super(created, last_updated);
        this.node_name = node_name;
        this.node_id = obtainNumberId(node_id);
        this.kingdom = obtainNumberId(kingdom);
        this.super_class = obtainNumberId(super_class);
        this.main_class = obtainNumberId(main_class);
        this.sub_class = obtainNumberId(sub_class);
        this.direct_parent = obtainNumberId(direct_parent);
        this.level_7 = obtainNumberId(level_7);
        this.level_8 = obtainNumberId(level_8);
        this.level_9 = obtainNumberId(level_9);
        this.level_10 = obtainNumberId(level_10);
        this.level_11 = obtainNumberId(level_11);
    }



    public String getNode_name() {
        return node_name;
    }



    public String getNode_id() {
        return node_id;
    }



    public String getNode_id_web() {
        return node_id;
    }



    public String getKingdom() {
        return kingdom;
    }



    public String getSuper_class() {
        return super_class;
    }



    public String getMain_class() {
        return main_class;
    }



    public String getSub_class() {
        return sub_class;
    }



    public String getDirect_parent() {
        return direct_parent;
    }



    public String getLevel_7() {
        return level_7;
    }



    public String getLevel_8() {
        return level_8;
    }



    public String getLevel_9() {
        return level_9;
    }



    public String getLevel_10() {
        return level_10;
    }



    public String getLevel_11() {
        return level_11;
    }



    public static String obtainNumberId(String cad) {
        return (cad != null && !cad.isEmpty() && cad.length() > 11) ? cad.substring(10) : cad;
    }



    @Override
    public String getTitle() {
        return this.title;
    }


}

