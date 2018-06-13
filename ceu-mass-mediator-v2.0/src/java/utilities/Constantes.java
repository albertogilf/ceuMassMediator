package utilities;

/**
 *
 * @author USUARIO
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente
 */
public interface Constantes {

    public static final double PROTON_WEIGHT = 1.0073d;
    public static double RT_WINDOW = 0.05D;
    public static double ADDUCT_AUTOMATIC_DETECTION_WINDOW = 0.05D;
    public static double TOLERANCE_SAME_MASS_WITHIN_FEATUREGROUPEDBYRT = 0.00001; 
    public static final String TOLERANCE_INICITAL_VALUE = "10";
    public static final String TOLERANCE_MODE_INICITAL_VALUE = "ppm";
    public static final String WEB_COMPUESTO_KEGG = "http://www.genome.jp/dbget-bin/www_bget?cpd:";
    public static final String WEB_COMPUESTO_METLIN = "https://metlin.scripps.edu/metabo_info.php?molid=";
    public static final String WEB_COMPUESTO_LM = "http://www.lipidmaps.org/data/LMSDRecord.php?LMID=";
    public static final String WEB_PATHWAY_KEGG = "http://www.genome.jp/kegg-bin/show_pathway?";
    public static final String WEB_COMPUESTO_HMDB = "http://www.hmdb.ca/metabolites/";
    public static final String WEB_COMPUESTO_PUBCHEMICHAL = "https://pubchem.ncbi.nlm.nih.gov/compound/";
    public static final String WEB_KEGG = "http://www.genome.jp/kegg/";
    public static final String WEB_METLIN = "https://metlin.scripps.edu";
    public static final String WEB_LIPID_MAPS = "http://www.lipidmaps.org";
    public static final String WEB_HMDB = "http://www.hmdb.ca";
    public static final String WEB_PUBCHEMICHAL = "https://pubchem.ncbi.nlm.nih.gov";
    public static final String WEB_COMPOUND_MINE_START = "http://minedatabase.mcs.anl.gov/#/acompound";
    public static final String WEB_COMPOUND_MINE_SUFFIX = "/overview";
    public static final String WEB_MINE = "http://minedatabase.mcs.anl.gov";
    public static final int ITEMS_PER_PAGE = 6500;
    public static final int ITEMS_PER_PAGE_IN_EXCEL = 6500;
    public static final float MIN_RETENTION_TIME_SCORE = 0.05F;
    public static final float MIN_SCORE_FOR_EUCLIDEAN_DISTANCE = 0.000001F;
    

    public static final String FILE_FOR_ANALYTICS_PATH = "/home/alberto/PHD/statistics_mediator/";

    public static final String NAME_FOR_RECALCULATED = "neutral";

    // Columns to manage excel files
    public static final String EXPERIMENTAL_MASS_HEADER = "Experimental mass";
    public static final String RT_HEADER = "Retention Time";
    public static final String CAS_HEADER = "CAS";
    public static final String COMPOUND_ID_HEADER = "Identifier";
    public static final String PPM_INCREMENT_HEADER = "PPM Error";
    public static final String IONIZATION_SCORE_HEADER = "Score 1"; // Ionization Score
    //Deleted temporaly
    //public static final String PRECEDENCE_SCORE_HEADER = "Score 2"; // Precedence Score
    public static final String ADDUCT_RELATION_SCORE_HEADER = "Score 2"; // ADDUCT RELATION Score
    public static final String RETENTION_TIME_SCORE_HEADER = "Score 3"; // Retention Time Score
    public static final String FINAL_SCORE_HEADER = "Final Score"; // FINAL Score

    public static final String MOL_WEIGHT_HEADER = "Molecular Weight";
    public static final String NAME_HEADER = "Name";
    public static final String FORMULA_HEADER = "Formula";
    public static final String ADDUCT_HEADER = "Adduct";
    public static final String KEGG_HEADER = "Kegg";
    public static final String HMDB_HEADER = "HMDB";
    public static final String LIPIDMAPS_HEADER = "LipidMaps";
    public static final String METLIN_HEADER = "Metlin";
    public static final String PUBHCEMICAL_HEADER = "PubChem";
    public static final String INCHIKEY_HEADER = "InChIKey";
    public static final String SMILES_HEADER = "SMILES";
    public static final String PATHWAYS_HEADER = "Pathways";

    // Strings to titles in Excel Files
    public static final String PATHWAYEXCELFILETITLE = "LIST OF PATHWAYS";
    public static final String COMPOUNDSEXCELFILETITLE = "LIST OF COMPOUNDS";

    //New constants for the paths of the server
    //public static final String UPLOADPATH ="C:\\Users\\ceu\\Desktop\\alberto\\CMM\\mediatorLoaderPathways"; 
    public static final String UPLOADPATH = "/var/mediatorloader";

    /**
     * Unique Sample mass
     */
    public static final String ONEDEMOMASS = "757.5667";

    /**
     * Unique sample retention time
     */
    public static final String ONERETENTIONTIME = "27.755224";

    /**
     * Unique sample composite Spectrum
     */
    public static final String ONECOMPOSITESPECTRUM = "(758.574, 2504091.8)(759.57526, 1266287.5)(760.57806, 351016.47)(761.57874, 68498.03)(762.5804, 12906.35)(780.5511, 45726.906)(781.5546, 21230.219)";

    /**
     * String for the sample of CompositeSpectrum
     */
    public static final String NEWDEMOSPECTRUM
            = "(400.3432, 307034.88)(401.34576, 73205.016)(402.3504, 15871.166)(403.35446, 2379.5325)(404.3498, 525.92053)"
            + "\n(422.32336, 1562.7301)(423.3237, 564.0795)(424.33255, 292.2923)"
            + "\n(631.4875, 367.90726)(632.4899, 261.73)(316.24945, 569921.25)(317.2518, 100396.53)(318.25354, 13153.248)(319.2558, 1834.3552)(320.25305, 241.56665)"
            + "\n(338.2299, 1832.6085)(339.2322, 468.8131)"
            + "\n(561.4858, 236.35)(141.1306, 297.12)(281.24765, 8532.774)(263.23685, 2734.8223)(264.24228, 616.6557)(265.2474, 97.63)(303.2296, 3154.6785)(304.2393, 718.22534)(305.23438, 272.0783)"
            + "\n(575.422, 339.64)(288.2174, 204084.69)(289.22003, 32914.242)(290.22226, 5089.2285)(291.2198, 723.9824)(292.2142, 86.07)(310.19937, 1059.77)(311.20145, 400.03336)"
            + "\n(991.674, 336133.72)(992.67676, 173852.98)(993.6781, 51292.195)(994.6797, 11841.43)(995.6813, 2720.4565)(996.6789, 674.16394)(496.3427, 2282350.8)(497.34558, 627602.9)(498.3469, 105371.484)(499.34842, 15081.279)(500.35052, 2490.545)(501.3458, 489.87)"
            + "\n(518.32263, 116812.14)(519.3252, 30485.719)(520.3284, 5558.731)(521.32385, 1256.6665)"
            + "\n(548.37054, 8616.517)(549.3739, 2817.4402)(550.3746, 667.2553)(570.35345, 917.5628)(571.35535, 354.61224)"
            + "\n(572.3718, 3261.4624)(573.3752, 1148.7113)(574.3768, 314.52)(594.35284, 400.20178)(595.3476, 362.69)"
            + "\n(570.3551, 15104.281)(571.3582, 5156.9814)(572.36224, 1203.8275)(573.3646, 101.370476)(592.33673, 1409.5024)(593.3393, 543.91895)(594.3383, 262.465)"
            + "\n(568.3401, 69828.43)(569.34283, 22957.86)(570.3444, 4912.129)"
            + "\n(590.32104, 5850.7417)(591.3069, 2871.11)"
            + "\n(482.324, 20545.27)(483.3272, 5389.9365)(484.33014, 1090.0233)(485.33273, 79.926674)(504.30594, 2025.5525)(505.30887, 635.57446)(506.3216, 258.78)"
            + "\n(955.57416, 613.7586)(956.57416, 387.9443)(478.29312, 53651.203)(479.29605, 13785.803)(480.29916, 2631.2334)(481.30112, 399.37863)"
            + "\n(500.27457, 3699.7795)(501.2779, 1045.2344)"
            + "\n(502.29303, 39800.742)(503.29602, 10899.144)(504.29947, 2186.163)(505.3003, 353.84317)(524.27563, 2873.6033)(525.2791, 906.12933)"
            + "\n(526.2927, 24282.523)(527.2958, 7487.513)(528.2991, 1569.15)(529.2973, 196.42332)"
            + "\n(548.27484, 1864.1719)(549.27313, 673.96075)(550.2758, 280.30704)"
            + "\n(512.33417, 5651.2104)(513.3372, 1544.2075)(514.3397, 386.18942)"
            + "\n(534.31616, 759.21454)(535.31836, 317.3078)"
            + "\n(540.3651, 1427.6666)(541.3666, 506.6308)(562.3447, 281.01)"
            + "\n(357.29926, 2166.04)(358.30072, 656.5368)(359.30063, 256.55334)(339.28897, 5054.322)(340.29132, 1220.8387)(341.28363, 439.1794)(379.2817, 11951.163)(380.28772, 2878.347)(381.28943, 626.92395)"
            + "\n(130.15865, 77549.195)(131.1616, 7348.628)(132.16498, 510.1139)"
            + "\n(563.54956, 708.24585)(564.55304, 334.00888)(282.2793, 24868.418)(283.28024, 5362.9136)(284.28632, 752.07227)(304.26303, 3486.2092)(305.2565, 1693.85)"
            + "\n(565.51715, 246.79333)(283.2637, 13169.584)(284.2691, 2777.094)(285.2758, 305.08862)"
            + "\n(265.25244, 2643.8992)(266.2552, 546.2861)(305.24606, 811.56195)(306.2479, 286.611)"
            + "\n(257.24796, 26266.43)(258.25128, 4836.586)(259.25348, 573.6886)(239.23682, 3090.2358)(240.23941, 594.7761)"
            + "\n(256.26474, 7365.2866)(257.26456, 1518.788)(258.26477, 316.8008)(278.24487, 1094.8077)(279.24127, 358.36502)"
            + "\n(649.5228, 1114.813)(650.52545, 503.7906)(651.5194, 298.524)(671.5072, 1295.3906)(672.5123, 650.952)(673.5101, 305.3548)"
            + "\n(647.5117, 3996.186)(648.5149, 1659.7957)(649.5138, 461.5271)(669.4957, 901.72766)(670.49976, 465.99585)"
            + "\n(337.2635, 249.91)(673.52704, 7908.1562)(674.53015, 3471.177)(675.5341, 901.9608)(676.5344, 177.278)"
            + "\n(695.50885, 1805.3715)(696.5148, 841.8891)(697.5164, 366.23264)"
            + "\n(426.35757, 10978.777)(427.36075, 3123.628)(428.36786, 853.9964)(429.37033, 121.670006)"
            + "\n(268.10373, 7663.9497)(269.10892, 1096.865)(270.11124, 339.83444)(290.0872, 283.51)"
            + "\n(184.09554, 124794.64)(185.09825, 9755.658)(186.10042, 1137.4908)(166.08492, 147531.22)(167.08737, 11846.145)(168.0917, 1502.9445)"
            + "\n(175.11816, 3152.7393)(176.11705, 458.54694)"
            + "\n(585.27026, 42384.46)(586.27325, 15461.8545)(587.27606, 3448.339)(588.27905, 542.0178)(607.2517, 1003.9856)(608.25476, 424.0903)"
            + "\n(195.08762, 71672.484)(196.09015, 7332.2144)(197.0978, 0.0)"
            + "\n(345.19922, 466.09)(162.11313, 157559.67)(163.11618, 13668.992)(164.12001, 3486.9958)(165.12671, 264.767)(144.102, 148704.73)(145.1047, 11795.48)(184.0943, 672.43)"
            + "\n(747.404, 539.6654)(748.4078, 408.8633)(363.21667, 25937.32)(364.22025, 6043.364)(365.222, 1204.7391)(366.2258, 166.76999)(385.19785, 2467.2812)(386.20236, 686.54364)(387.203, 266.985)"
            + "\n(114.06652, 17417.838)(115.070625, 1084.2437)(116.07057, 18180.924)(117.07328, 1213.5526)(136.0501, 7457.47)(137.0577, 724.81)(138.0552, 3627.86)(139.057, 32.69)"
            + "\n(156.07611, 9551.281)(157.08028, 1046.9487)"
            + "\n(331.1681, 282.99)(166.08543, 169862.95)(167.08788, 18255.64)(168.08939, 2065.1548)(188.0698, 99807.625)(189.07253, 15988.848)(190.07431, 2063.414)(191.07504, 302.325)"
            + "\n(431.3844, 7593.5786)(432.38687, 2237.7622)(433.38278, 589.1297)(434.3849, 65.02)";

    /**
     * String for the sample of sample masses
     */
    public static final String NEWDEMOMASSES
            = "399.3367"
            + "\n421.31686"
            + "\n315.2424"
            + "\n337.2234"
            + "\n280.2402"
            + "\n287.2104"
            + "\n495.3352"
            + "\n517.3172"
            + "\n547.3635"
            + "\n571.3644"
            + "\n569.3479"
            + "\n567.3328"
            + "\n589.3148"
            + "\n481.3167"
            + "\n477.2858"
            + "\n499.2672"
            + "\n501.286"
            + "\n525.2853"
            + "\n547.2667"
            + "\n511.3265"
            + "\n533.3079"
            + "\n539.3571"
            + "\n356.2923"
            + "\n129.1515"
            + "\n281.2722"
            + "\n282.2563"
            + "\n264.2458"
            + "\n256.2406"
            + "\n255.2571"
            + "\n648.5165"
            + "\n646.5048"
            + "\n672.5205"
            + "\n694.5019"
            + "\n425.3504"
            + "\n267.0966"
            + "\n183.0882"
            + "\n174.1109"
            + "\n584.263"
            + "\n194.0804"
            + "\n161.1059"
            + "\n362.2094"
            + "\n113.059"
            + "\n155.0689"
            + "\n165.078"
            + "\n430.3773";

    /**
     * String for the sample of retention time
     */
    public static final String NEWDEMORETENTIONTIME
            = "18.842525"
            + "\n18.842525"
            + "\n8.144917"
            + "\n8.144917"
            + "\n28.269503"
            + "\n4.021555"
            + "\n19.46886"
            + "\n19.46886"
            + "\n21.503885"
            + "\n20.90083"
            + "\n18.852442"
            + "\n17.863642"
            + "\n17.863642"
            + "\n17.41639"
            + "\n16.68847"
            + "\n16.68847"
            + "\n17.76522"
            + "\n17.698446"
            + "\n17.698446"
            + "\n16.254662"
            + "\n16.254662"
            + "\n20.272083"
            + "\n24.690693"
            + "\n1.9077333"
            + "\n27.716194"
            + "\n31.261553"
            + "\n31.261553"
            + "\n30.816559"
            + "\n26.94"
            + "\n31.910034"
            + "\n30.488884"
            + "\n32.697334"
            + "\n32.697334"
            + "\n19.546305"
            + "\n0.80008334"
            + "\n0.70422226"
            + "\n1.1756389"
            + "\n7.2901664"
            + "\n1.0755279"
            + "\n0.6985555"
            + "\n4.0684447"
            + "\n0.7552286"
            + "\n0.6092778"
            + "\n1.0322223"
            + "\n30.569942";

    /**
     * DEMO NAME
     */
    public static final String DEMONAME = "choline";

    /**
     * DEMO NAME
     */
    public static final String DEMOFORMULA = "C5H14NO";

    /**
     * DEMO PEAKS FOR THE MSMS SEARCH
     */
    public static final String DEMOPEAKS
            = "40.948 0.174\n"
            + "56.022 0.424\n"
            + "84.37 53.488\n"
            + "101.50 8.285\n"
            + "102.401 0.775\n"
            + "129.670 100.000\n"
            + "146.966 20.070";
    /**
     * DEMO PARENT ION MASS FOR THE MSMS SEARCH
     */
    public static final String DEMOPARENTIONMASS="147.0";
    /**
     * DEMO PARENT ION MASS TOLERANCE FOR THE MSMS SEARCH
     */
    public static final String DEMOPARENTIONMASSTOLERANCE="0.1";
    /**
     * DEMO MZ TOLERANCE FOR THE MSMS SEARCH
     */
    public static final String DEMOMZTOLERANCE="0.5";
}
