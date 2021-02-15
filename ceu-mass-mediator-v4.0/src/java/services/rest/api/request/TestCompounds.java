/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services.rest.api.request;

import java.util.*;

/**
 *
 * @author Sergio Saugar García <sergio.saugargarcia@ceu.es>
 */
public class TestCompounds {

    public static PathWayExt map00260 = new PathWayExt("Glycine and Serine Metabolism", "http://www.genome.jp/kegg-bin/show_pathway?map00260", "map00260");
    public static PathWayExt map00250 = new PathWayExt("Glutamate Metabolism", "http://www.genome.jp/kegg-bin/show_pathway?map00250", "map00250");
    public static PathWayExt map00330 = new PathWayExt("Arginine and Proline Metabolism", "http://www.genome.jp/kegg-bin/show_pathway?map00330", "map00330");
    public static PathWayExt map00472 = new PathWayExt("D-Arginine and D-Ornithine Metabolism", "http://www.genome.jp/kegg-bin/show_pathway?map00472", "map00472");
    public static PathWayExt map01100 = new PathWayExt("Metabolic pathways", "http://www.genome.jp/kegg-bin/show_pathway?map01100", "map01100");
    public static PathWayExt map01110 = new PathWayExt("Biosynthesis of secondary metabolites", "http://www.genome.jp/kegg-bin/show_pathway?map01110", "map01110");
    public static PathWayExt map01060 = new PathWayExt("Biosynthesis of plant secondary metabolites", "http://www.genome.jp/kegg-bin/show_pathway?map01060", "map01060");
    public static PathWayExt map01064 = new PathWayExt("Biosynthesis of alkaloids derived from ornithine, lysine and nicotinic acid", "http://www.genome.jp/kegg-bin/show_pathway?map01064", "map01064");
    public static PathWayExt map01130 = new PathWayExt("Biosynthesis of antibiotics", "http://www.genome.jp/kegg-bin/show_pathway?map01130", "map01130");
    public static PathWayExt map05230 = new PathWayExt("Central carbon metabolism in cancer", "http://www.genome.jp/kegg-bin/show_pathway?map05230", "map05230");
    public static PathWayExt map00220 = new PathWayExt("Arginine biosynthesis", "http://www.genome.jp/kegg-bin/show_pathway?map00220", "map00220");
    public static PathWayExt map00261 = new PathWayExt("Monobactam biosynthesis", "http://www.genome.jp/kegg-bin/show_pathway?map00261", "map00261");
    public static PathWayExt map00970 = new PathWayExt("Aminoacyl-tRNA biosynthesis", "http://www.genome.jp/kegg-bin/show_pathway?map00970", "map00970");
    public static PathWayExt map01230 = new PathWayExt("Biosynthesis of amino acids", "http://www.genome.jp/kegg-bin/show_pathway?map01230", "map01230");
    public static PathWayExt map02010 = new PathWayExt("ABC transporters", "http://www.genome.jp/kegg-bin/show_pathway?map02010", "map02010");
    public static PathWayExt map04974 = new PathWayExt("Protein digestion and absorption", "http://www.genome.jp/kegg-bin/show_pathway?map04974", "map04974");
    public static PathWayExt map05014 = new PathWayExt("Amyotrophic lateral sclerosis (ALS)", "http://www.genome.jp/kegg-bin/show_pathway?map05014", "map05014");
    public static PathWayExt map05142 = new PathWayExt("Chagas disease (American trypanosomiasis)", "http://www.genome.jp/kegg-bin/show_pathway?map05142", "map05142");
    public static PathWayExt map05146 = new PathWayExt("Amoebiasis", "http://www.genome.jp/kegg-bin/show_pathway?map05146", "map05146");
    public static PathWayExt map00331 = new PathWayExt("Clavulanic acid biosynthesis", "http://www.genome.jp/kegg-bin/show_pathway?map00331", "map00331");
    public static PathWayExt map05132 = new PathWayExt("Salmonella infection", "http://www.genome.jp/kegg-bin/show_pathway?map05132", "map05132");

    public static PDCompound larginina = new PDCompound(
            139243,
            174.1109,
            1.1756,
            "L-Arginine",
            "C6H14N4O2",
            Adducts.PMpH,
            174.1117,
            4,
            0.0,
            0.0,
            0.0,
            0.0,
            "74-79-3",
            "C00062",
            "http://www.genome.jp/dbget-bin/www_bget?cpd:C00062",
            "HMDB0000517",
            "http://www.hmdb.ca/metabolites/HMDB0000517",
            "",
            "",
            "13",
            "https://metlin.scripps.edu/metabo_info.php?molid=13",
            "6322",
            "https://pubchem.ncbi.nlm.nih.gov/compound/6322",
            "ODKSFYDXXFIFQN-BYPYZUCNSA-N",
            new LinkedList<PathWayExt>(
                    Arrays.asList(
                            map00260, map00250, map00330, map00472, map01100, map01110, map01060,
                            map01064, map01130, map05230, map00220, map00261, map00970, map01230,
                            map02010, map04974, map05014, map05142, map05146, map00331, map05132
                    )
            )
    );

    public static PDCompound darginina = new PDCompound(
            105918,
            174.1109,
            1.1756,
            "D-Arginine",
            "C6H14N4O2",
            Adducts.PMpH,
            174.1117,
            4,
            0.0,
            0.0,
            0.0,
            0.0,
            "157-06-2",
            "C00792",
            "http://www.genome.jp/dbget-bin/www_bget?cpd:C00792",
            "HMDB0003416",
            "http://www.hmdb.ca/metabolites/HMDB0003416",
            "",
            "",
            "6924",
            "https://metlin.scripps.edu/metabo_info.php?molid=6924",
            "71070",
            "https://pubchem.ncbi.nlm.nih.gov/compound/71070",
            "ODKSFYDXXFIFQN-SCSAIBSYSA-N",
            new LinkedList<PathWayExt>(
                    Arrays.asList(
                            map00472, map01100
                    )
            )
    );

    public static PDCompound oxoarginina = new PDCompound(
            139640,
            155.0689,
            0.6093,
            "2-Oxoarginine",
            "C6H11N3O3",
            Adducts.PMpHmH2O,
            173.08,
            4,
            0.0,
            0.0,
            0.0,
            0.0,
            "3715-10-4",
            "C03771",
            "http://www.genome.jp/dbget-bin/www_bget?cpd:C03771",
            "HMDB0004225",
            "http://www.hmdb.ca/metabolites/HMDB0004225",
            "",
            "",
            "7030",
            "https://metlin.scripps.edu/metabo_info.php?molid=7030",
            "558",
            "https://pubchem.ncbi.nlm.nih.gov/compound/558",
            "ARBHXJXXVVHMET-UHFFFAOYSA-N",
            new LinkedList<PathWayExt>(
                    Arrays.asList(
                            map00472, map01100, map00330
                    )
            )
    );
}
