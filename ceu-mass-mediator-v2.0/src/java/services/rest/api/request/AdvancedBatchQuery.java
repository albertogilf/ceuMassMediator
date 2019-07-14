package services.rest.api.request;

import services.rest.api.request.ModifiersType;
import services.rest.api.request.ChemAlphabet;
import services.rest.api.request.Spectrum;
import services.rest.api.request.Database;
import services.rest.api.request.Adducts;
import services.rest.api.request.MetabolitesType;
import services.rest.api.request.MassesMode;
import services.rest.api.request.IonMode;
import services.rest.api.request.ToleranceMode;
import com.google.gson.Gson;
import java.util.*;

/**
 *
 * @author Rodrigo Garcia Carmona <r.garcia.carmona@gmail.com>
 */
public class AdvancedBatchQuery {

    private List<Double> masses;
    private List<Double> retention_times;
    private List<List<Spectrum>> composite_spectra;
    private List<Double> all_masses;
    private List<Double> all_retention_times;
    private List<List<Spectrum>> all_composite_spectra;
    private double tolerance;
    private ToleranceMode tolerance_mode;
    private ChemAlphabet chemical_alphabet;
    private boolean deuterium;
    private ModifiersType modifiers_type;
    private List<Database> databases;
    private MetabolitesType metabolites_type;
    private MassesMode masses_mode;
    private IonMode ion_mode;
    private List<Adducts> adducts;

    public AdvancedBatchQuery(List<Double> masses, List<Double> retention_times,
                              List<List<Spectrum>> composite_spectra,
                              List<Double> all_masses,
                              List<Double> all_retention_times,
                              List<List<Spectrum>> all_composite_spectra,
                              double tolerance, ToleranceMode tolerance_mode,
                              ChemAlphabet chemical_alphabet,
                              boolean deuterium, ModifiersType modifiers_type,
                              List<Database> databases,
                              MetabolitesType metabolites_type,
                              MassesMode masses_mode, IonMode ion_mode,
                              List<Adducts> adducts) {
        this(masses, retention_times, all_masses, all_retention_times,
             tolerance, tolerance_mode, chemical_alphabet, deuterium, modifiers_type,
             metabolites_type, masses_mode, ion_mode);
        this.composite_spectra = composite_spectra;
        this.all_composite_spectra = all_composite_spectra;
        this.databases = databases;
        this.adducts = adducts;
    }

    public AdvancedBatchQuery(List<Double> masses, List<Double> retention_times,
                              List<Double> all_masses,
                              List<Double> all_retention_times,
                              double tolerance, ToleranceMode tolerance_mode,
                              ChemAlphabet chemical_alphabet, boolean deuterium,
                              ModifiersType modifiers_type,
                              MetabolitesType metabolites_type,
                              MassesMode masses_mode, IonMode ion_mode) {
        this.masses = masses;
        this.retention_times = retention_times;
        this.composite_spectra = new ArrayList();
        this.all_masses = all_masses;
        this.all_retention_times = all_retention_times;
        this.all_composite_spectra = new ArrayList();
        this.tolerance = tolerance;
        this.tolerance_mode = tolerance_mode;
        this.chemical_alphabet = chemical_alphabet;
        this.deuterium = deuterium;
        this.modifiers_type = modifiers_type;
        this.databases = new ArrayList();
        this.metabolites_type = metabolites_type;
        this.masses_mode = masses_mode;
        this.ion_mode = ion_mode;
        this.adducts = new ArrayList();
    }

    public List<Double> getMasses() {
        return masses;
    }

    public List<Double> getRetention_times() {
        return retention_times;
    }

    public List<List<Spectrum>> getComposite_spectra() {
        return composite_spectra;
    }

    public List<Double> getAll_masses() {
        return all_masses;
    }

    public List<Double> getAll_retention_times() {
        return all_retention_times;
    }

    public List<List<Spectrum>> getAll_composite_spectra() {
        return all_composite_spectra;
    }

    public double getTolerance() {
        return tolerance;
    }

    public ToleranceMode getTolerance_mode() {
        return tolerance_mode;
    }

    public ChemAlphabet getChemical_alphabet() {
        return chemical_alphabet;
    }

    public boolean getDeuterium() {
        return deuterium;
    }

    public ModifiersType getModifiers_type() {
        return modifiers_type;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public MetabolitesType getMetabolites_type() {
        return metabolites_type;
    }

    public MassesMode getMasses_mode() {
        return masses_mode;
    }

    public IonMode getIon_mode() {
        return ion_mode;
    }

    public List<Adducts> getAdducts() {
        return adducts;
    }

    public void addSpectra(List<Spectrum> spectra) {
        this.composite_spectra.add(spectra);
    }

    public void addSpectraAll(List<Spectrum> spectra) {
        this.all_composite_spectra.add(spectra);
    }

    public void addDatabase(Database database) {
        this.databases.add(database);
    }

    public void addAdduct(Adducts pa) {
        this.adducts.add(pa);
    }

    /**
     * Shows a JSON message for invoking the advance batch service using the
     * predefined demo values.
     *
     * @param args No params needed
     */
    public static void main(String[] args) {

        // DEMO DATA - BATCH ADVANCED SEARCH V1
        Double[] massesArray = {
            399.3367, 421.31686, 315.2424, 337.2234, 280.2402,
            287.2104, 495.3352, 517.3172, 547.3635, 571.3644,
            569.3479, 567.3328, 589.3148, 481.3167, 477.2858,
            499.2672, 501.286, 525.2853, 547.2667, 511.3265,
            533.3079, 539.3571, 356.2923, 129.1515, 281.2722,
            282.2563, 264.2458, 256.2406, 255.2571, 648.5165,
            646.5048, 672.5205, 694.5019, 425.3504, 267.0966,
            183.0882, 174.1109, 584.263, 194.0804, 161.1059,
            362.2094, 113.059, 155.0689, 165.078, 430.3773
        };

        Double[] retention_timesArray = {
            18.842525, 18.842525, 8.144917, 8.144917, 28.269503,
            4.021555, 19.46886, 19.46886, 21.503885, 20.90083,
            18.852442, 17.863642, 17.863642, 17.41639, 16.68847,
            16.68847, 17.76522, 17.698446, 17.698446, 16.254662,
            16.254662, 20.272083, 24.690693, 1.9077333, 27.716194,
            31.261553, 31.261553, 30.816559, 26.94, 31.910034,
            30.488884, 32.697334, 32.697334, 19.546305, 0.80008334,
            0.70422226, 1.1756389, 7.2901664, 1.0755279, 0.6985555,
            4.0684447, 0.7552286, 0.6092778, 1.0322223, 30.569942
        };

        List<Double> masses = Arrays.asList(massesArray);
        List<Double> retention_times = Arrays.asList(retention_timesArray);
        List<Double> all_masses = new ArrayList<Double>();
        List<Double> all_retention_times = new ArrayList<Double>();

        AdvancedBatchQuery demoData = new AdvancedBatchQuery(
                masses,
                retention_times,
                all_masses,
                all_retention_times,
                10.0,
                ToleranceMode.PPM,
                ChemAlphabet.CHNOPS,
                false,
                ModifiersType.NONE,
                MetabolitesType.AWPEPTIDES,
                MassesMode.NEUTRAL,
                IonMode.POSITIVE);
        Spectrum[] array1 = {
            new Spectrum(400.3432, 307034.88),
            new Spectrum(401.34576, 73205.016),
            new Spectrum(402.3504, 15871.166),
            new Spectrum(403.35446, 2379.5325),
            new Spectrum(404.3498, 525.92053)};
        List<Spectrum> spec1 = Arrays.asList(array1);
        Spectrum[] array2 = {
            new Spectrum(422.32336, 1562.7301),
            new Spectrum(423.3237, 564.0795),
            new Spectrum(424.33255, 292.2923)};
        List<Spectrum> spec2 = Arrays.asList(array2);
        Spectrum[] array3 = {
            new Spectrum(631.4875, 367.90726),
            new Spectrum(632.4899, 261.73),
            new Spectrum(316.24945, 569921.25),
            new Spectrum(317.2518, 100396.53),
            new Spectrum(318.25354, 13153.248),
            new Spectrum(319.2558, 1834.3552),
            new Spectrum(320.25305, 241.56665)};
        List<Spectrum> spec3 = Arrays.asList(array3);
        Spectrum[] array4 = {
            new Spectrum(338.2299, 1832.6085),
            new Spectrum(339.2322, 468.8131)};
        List<Spectrum> spec4 = Arrays.asList(array4);
        Spectrum[] array5 = {
            new Spectrum(561.4858, 236.35),
            new Spectrum(141.1306, 297.12),
            new Spectrum(281.24765, 8532.774),
            new Spectrum(263.23685, 2734.8223),
            new Spectrum(264.24228, 616.6557),
            new Spectrum(265.2474, 97.63),
            new Spectrum(303.2296, 3154.6785),
            new Spectrum(304.2393, 718.22534),
            new Spectrum(305.23438, 272.0783)};
        List<Spectrum> spec5 = Arrays.asList(array5);
        Spectrum[] array6 = {
            new Spectrum(575.422, 339.64),
            new Spectrum(288.2174, 204084.69),
            new Spectrum(289.22003, 32914.242),
            new Spectrum(290.22226, 5089.2285),
            new Spectrum(291.2198, 723.9824),
            new Spectrum(292.2142, 86.07),
            new Spectrum(310.19937, 1059.77),
            new Spectrum(311.20145, 400.03336)};
        List<Spectrum> spec6 = Arrays.asList(array6);
        Spectrum[] array7 = {
            new Spectrum(991.674, 336133.72),
            new Spectrum(992.67676, 173852.98),
            new Spectrum(993.6781, 51292.195),
            new Spectrum(994.6797, 11841.43),
            new Spectrum(995.6813, 2720.4565),
            new Spectrum(996.6789, 674.16394),
            new Spectrum(496.3427, 2282350.8),
            new Spectrum(497.34558, 627602.9),
            new Spectrum(498.3469, 105371.484),
            new Spectrum(499.34842, 15081.279),
            new Spectrum(500.35052, 2490.545),
            new Spectrum(501.3458, 489.87)};
        List<Spectrum> spec7 = Arrays.asList(array7);
        Spectrum[] array8 = {
            new Spectrum(518.32263, 116812.14),
            new Spectrum(519.3252, 30485.719),
            new Spectrum(520.3284, 5558.731),
            new Spectrum(521.32385, 1256.6665)};
        List<Spectrum> spec8 = Arrays.asList(array8);
        Spectrum[] array9 = {
            new Spectrum(548.37054, 8616.517),
            new Spectrum(549.3739, 2817.4402),
            new Spectrum(550.3746, 667.2553),
            new Spectrum(570.35345, 917.5628),
            new Spectrum(571.35535, 354.61224)};
        List<Spectrum> spec9 = Arrays.asList(array9);
        Spectrum[] array10 = {
            new Spectrum(572.3718, 3261.4624),
            new Spectrum(573.3752, 1148.7113),
            new Spectrum(574.3768, 314.52),
            new Spectrum(594.35284, 400.20178),
            new Spectrum(595.3476, 362.69)};
        List<Spectrum> spec10 = Arrays.asList(array10);
        Spectrum[] array11 = {
            new Spectrum(570.3551, 15104.281),
            new Spectrum(571.3582, 5156.9814),
            new Spectrum(572.36224, 1203.8275),
            new Spectrum(573.3646, 101.370476),
            new Spectrum(592.33673, 1409.5024),
            new Spectrum(593.3393, 543.91895),
            new Spectrum(594.3383, 262.465)};
        List<Spectrum> spec11 = Arrays.asList(array11);
        Spectrum[] array12 = {
            new Spectrum(568.3401, 69828.43),
            new Spectrum(569.34283, 22957.86),
            new Spectrum(570.3444, 4912.129)};
        List<Spectrum> spec12 = Arrays.asList(array12);
        Spectrum[] array13 = {
            new Spectrum(590.32104, 5850.7417),
            new Spectrum(591.3069, 2871.11)};
        List<Spectrum> spec13 = Arrays.asList(array13);
        Spectrum[] array14 = {
            new Spectrum(482.324, 20545.27),
            new Spectrum(483.3272, 5389.9365),
            new Spectrum(484.33014, 1090.0233),
            new Spectrum(485.33273, 79.926674),
            new Spectrum(504.30594, 2025.5525),
            new Spectrum(505.30887, 635.57446),
            new Spectrum(506.3216, 258.78)};
        List<Spectrum> spec14 = Arrays.asList(array14);
        Spectrum[] array15 = {
            new Spectrum(955.57416, 613.7586),
            new Spectrum(956.57416, 387.9443),
            new Spectrum(478.29312, 53651.203),
            new Spectrum(479.29605, 13785.803),
            new Spectrum(480.29916, 2631.2334),
            new Spectrum(481.30112, 399.37863)};
        List<Spectrum> spec15 = Arrays.asList(array15);
        Spectrum[] array16 = {
            new Spectrum(500.27457, 3699.7795),
            new Spectrum(501.2779, 1045.2344)};
        List<Spectrum> spec16 = Arrays.asList(array16);
        Spectrum[] array17 = {
            new Spectrum(502.29303, 39800.742),
            new Spectrum(503.29602, 10899.144),
            new Spectrum(504.29947, 2186.163),
            new Spectrum(505.3003, 353.84317),
            new Spectrum(524.27563, 2873.6033),
            new Spectrum(525.2791, 906.12933)};
        List<Spectrum> spec17 = Arrays.asList(array17);
        Spectrum[] array18 = {
            new Spectrum(526.2927, 24282.523),
            new Spectrum(527.2958, 7487.513),
            new Spectrum(528.2991, 1569.15),
            new Spectrum(529.2973, 196.42332)};
        List<Spectrum> spec18 = Arrays.asList(array18);
        Spectrum[] array19 = {
            new Spectrum(548.27484, 1864.1719),
            new Spectrum(549.27313, 673.96075),
            new Spectrum(550.2758, 280.30704)};
        List<Spectrum> spec19 = Arrays.asList(array19);
        Spectrum[] array20 = {
            new Spectrum(512.33417, 5651.2104),
            new Spectrum(513.3372, 1544.2075),
            new Spectrum(514.3397, 386.18942)};
        List<Spectrum> spec20 = Arrays.asList(array20);
        Spectrum[] array21 = {
            new Spectrum(534.31616, 759.21454),
            new Spectrum(535.31836, 317.3078)};
        List<Spectrum> spec21 = Arrays.asList(array21);
        Spectrum[] array22 = {
            new Spectrum(540.3651, 1427.6666),
            new Spectrum(541.3666, 506.6308),
            new Spectrum(562.3447, 281.01)};
        List<Spectrum> spec22 = Arrays.asList(array22);
        Spectrum[] array23 = {
            new Spectrum(357.29926, 2166.04),
            new Spectrum(358.30072, 656.5368),
            new Spectrum(359.30063, 256.55334),
            new Spectrum(339.28897, 5054.322),
            new Spectrum(340.29132, 1220.8387),
            new Spectrum(341.28363, 439.1794),
            new Spectrum(379.2817, 11951.163),
            new Spectrum(380.28772, 2878.347),
            new Spectrum(381.28943, 626.92395)};
        List<Spectrum> spec23 = Arrays.asList(array23);
        Spectrum[] array24 = {
            new Spectrum(130.15865, 77549.195),
            new Spectrum(131.1616, 7348.628),
            new Spectrum(132.16498, 510.1139)};
        List<Spectrum> spec24 = Arrays.asList(array24);
        Spectrum[] array25 = {
            new Spectrum(563.54956, 708.24585),
            new Spectrum(564.55304, 334.00888),
            new Spectrum(282.2793, 24868.418),
            new Spectrum(283.28024, 5362.9136),
            new Spectrum(284.28632, 752.07227),
            new Spectrum(304.26303, 3486.2092),
            new Spectrum(305.2565, 1693.85)};
        List<Spectrum> spec25 = Arrays.asList(array25);
        Spectrum[] array26 = {
            new Spectrum(565.51715, 246.79333),
            new Spectrum(283.2637, 13169.584),
            new Spectrum(284.2691, 2777.094),
            new Spectrum(285.2758, 305.08862)};
        List<Spectrum> spec26 = Arrays.asList(array26);
        Spectrum[] array27 = {
            new Spectrum(265.25244, 2643.8992),
            new Spectrum(266.2552, 546.2861),
            new Spectrum(305.24606, 811.56195),
            new Spectrum(306.2479, 286.611)};
        List<Spectrum> spec27 = Arrays.asList(array27);
        Spectrum[] array28 = {
            new Spectrum(257.24796, 26266.43),
            new Spectrum(258.25128, 4836.586),
            new Spectrum(259.25348, 573.6886),
            new Spectrum(239.23682, 3090.2358),
            new Spectrum(240.23941, 594.7761)};
        List<Spectrum> spec28 = Arrays.asList(array28);
        Spectrum[] array29 = {
            new Spectrum(256.26474, 7365.2866),
            new Spectrum(257.26456, 1518.788),
            new Spectrum(258.26477, 316.8008),
            new Spectrum(278.24487, 1094.8077),
            new Spectrum(279.24127, 358.36502)};
        List<Spectrum> spec29 = Arrays.asList(array29);
        Spectrum[] array30 = {
            new Spectrum(649.5228, 1114.813),
            new Spectrum(650.52545, 503.7906),
            new Spectrum(651.5194, 298.524),
            new Spectrum(671.5072, 1295.3906),
            new Spectrum(672.5123, 650.952),
            new Spectrum(673.5101, 305.3548)};
        List<Spectrum> spec30 = Arrays.asList(array30);
        Spectrum[] array31 = {
            new Spectrum(647.5117, 3996.186),
            new Spectrum(648.5149, 1659.7957),
            new Spectrum(649.5138, 461.5271),
            new Spectrum(669.4957, 901.72766),
            new Spectrum(670.49976, 465.99585)};
        List<Spectrum> spec31 = Arrays.asList(array31);
        Spectrum[] array32 = {
            new Spectrum(337.2635, 249.91),
            new Spectrum(673.52704, 7908.1562),
            new Spectrum(674.53015, 3471.177),
            new Spectrum(675.5341, 901.9608),
            new Spectrum(676.5344, 177.278)};
        List<Spectrum> spec32 = Arrays.asList(array32);
        Spectrum[] array33 = {
            new Spectrum(695.50885, 1805.3715),
            new Spectrum(696.5148, 841.8891),
            new Spectrum(697.5164, 366.23264)};
        List<Spectrum> spec33 = Arrays.asList(array33);
        Spectrum[] array34 = {
            new Spectrum(426.35757, 10978.777),
            new Spectrum(427.36075, 3123.628),
            new Spectrum(428.36786, 853.9964),
            new Spectrum(429.37033, 121.670006)};
        List<Spectrum> spec34 = Arrays.asList(array34);
        Spectrum[] array35 = {
            new Spectrum(268.10373, 7663.9497),
            new Spectrum(269.10892, 1096.865),
            new Spectrum(270.11124, 339.83444),
            new Spectrum(290.0872, 283.51)};
        List<Spectrum> spec35 = Arrays.asList(array35);
        Spectrum[] array36 = {
            new Spectrum(184.09554, 124794.64),
            new Spectrum(185.09825, 9755.658),
            new Spectrum(186.10042, 1137.4908),
            new Spectrum(166.08492, 147531.22),
            new Spectrum(167.08737, 11846.145),
            new Spectrum(168.0917, 1502.9445)};
        List<Spectrum> spec36 = Arrays.asList(array36);
        Spectrum[] array37 = {
            new Spectrum(175.11816, 3152.7393),
            new Spectrum(176.11705, 458.54694)};
        List<Spectrum> spec37 = Arrays.asList(array37);
        Spectrum[] array38 = {
            new Spectrum(585.27026, 42384.46),
            new Spectrum(586.27325, 15461.8545),
            new Spectrum(587.27606, 3448.339),
            new Spectrum(588.27905, 542.0178),
            new Spectrum(607.2517, 1003.9856),
            new Spectrum(608.25476, 424.0903)};
        List<Spectrum> spec38 = Arrays.asList(array38);
        Spectrum[] array39 = {
            new Spectrum(195.08762, 71672.484),
            new Spectrum(196.09015, 7332.2144),
            new Spectrum(197.0978, 0.0)};
        List<Spectrum> spec39 = Arrays.asList(array39);
        Spectrum[] array40 = {
            new Spectrum(345.19922, 466.09),
            new Spectrum(162.11313, 157559.67),
            new Spectrum(163.11618, 13668.992),
            new Spectrum(164.12001, 3486.9958),
            new Spectrum(165.12671, 264.767),
            new Spectrum(144.102, 148704.73),
            new Spectrum(145.1047, 11795.48),
            new Spectrum(184.0943, 672.43)};
        List<Spectrum> spec40 = Arrays.asList(array40);
        Spectrum[] array41 = {
            new Spectrum(747.404, 539.6654),
            new Spectrum(748.4078, 408.8633),
            new Spectrum(363.21667, 25937.32),
            new Spectrum(364.22025, 6043.364),
            new Spectrum(365.222, 1204.7391),
            new Spectrum(366.2258, 166.76999),
            new Spectrum(385.19785, 2467.2812),
            new Spectrum(386.20236, 686.54364),
            new Spectrum(387.203, 266.985)};
        List<Spectrum> spec41 = Arrays.asList(array41);
        Spectrum[] array42 = {
            new Spectrum(114.06652, 17417.838),
            new Spectrum(115.070625, 1084.2437),
            new Spectrum(116.07057, 18180.924),
            new Spectrum(117.07328, 1213.5526),
            new Spectrum(136.0501, 7457.47),
            new Spectrum(137.0577, 724.81),
            new Spectrum(138.0552, 3627.86),
            new Spectrum(139.057, 32.69)};
        List<Spectrum> spec42 = Arrays.asList(array42);
        Spectrum[] array43 = {
            new Spectrum(156.07611, 9551.281),
            new Spectrum(157.08028, 1046.9487)};
        List<Spectrum> spec43 = Arrays.asList(array43);
        Spectrum[] array44 = {
            new Spectrum(331.1681, 282.99),
            new Spectrum(166.08543, 169862.95),
            new Spectrum(167.08788, 18255.64),
            new Spectrum(168.08939, 2065.1548),
            new Spectrum(188.0698, 99807.625),
            new Spectrum(189.07253, 15988.848),
            new Spectrum(190.07431, 2063.414),
            new Spectrum(191.07504, 302.325)};
        List<Spectrum> spec44 = Arrays.asList(array44);
        Spectrum[] array45 = {
            new Spectrum(431.3844, 7593.5786),
            new Spectrum(432.38687, 2237.7622),
            new Spectrum(433.38278, 589.1297),
            new Spectrum(434.3849, 65.02)};
        List<Spectrum> spec45 = Arrays.asList(array45);

        demoData.addSpectra(spec1);
        demoData.addSpectra(spec2);
        demoData.addSpectra(spec3);
        demoData.addSpectra(spec4);
        demoData.addSpectra(spec5);
        demoData.addSpectra(spec6);
        demoData.addSpectra(spec7);
        demoData.addSpectra(spec8);
        demoData.addSpectra(spec9);
        demoData.addSpectra(spec10);
        demoData.addSpectra(spec11);
        demoData.addSpectra(spec12);
        demoData.addSpectra(spec13);
        demoData.addSpectra(spec14);
        demoData.addSpectra(spec15);
        demoData.addSpectra(spec16);
        demoData.addSpectra(spec17);
        demoData.addSpectra(spec18);
        demoData.addSpectra(spec19);
        demoData.addSpectra(spec20);
        demoData.addSpectra(spec21);
        demoData.addSpectra(spec22);
        demoData.addSpectra(spec23);
        demoData.addSpectra(spec24);
        demoData.addSpectra(spec25);
        demoData.addSpectra(spec26);
        demoData.addSpectra(spec27);
        demoData.addSpectra(spec28);
        demoData.addSpectra(spec29);
        demoData.addSpectra(spec30);
        demoData.addSpectra(spec31);
        demoData.addSpectra(spec32);
        demoData.addSpectra(spec33);
        demoData.addSpectra(spec34);
        demoData.addSpectra(spec35);
        demoData.addSpectra(spec36);
        demoData.addSpectra(spec37);
        demoData.addSpectra(spec38);
        demoData.addSpectra(spec39);
        demoData.addSpectra(spec40);
        demoData.addSpectra(spec41);
        demoData.addSpectra(spec42);
        demoData.addSpectra(spec43);
        demoData.addSpectra(spec44);
        demoData.addSpectra(spec45);
        demoData.addDatabase(Database.ALLWMINE);
        demoData.addAdduct(Adducts.PMpH);
        demoData.addAdduct(Adducts.PMp2H);
        demoData.addAdduct(Adducts.PMpNA);
        demoData.addAdduct(Adducts.PMpK);
        demoData.addAdduct(Adducts.PMpNH4);
        demoData.addAdduct(Adducts.PMpHmH2O);

        Gson gson = new Gson();

        String result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Batch Advanced Search");
        System.out.println(result);

        AdvancedSearchQuery received = gson.fromJson(result, AdvancedSearchQuery.class);

        System.out.println("Resultado: " + gson.toJson(received).equals(result));

        System.out.println("El tipo es: " + received.getAdducts().getClass().getTypeName());
        for (Adducts pa : received.getAdducts()) {
            System.out.println(pa + " " + pa.getClass().getTypeName());
        }

        // DEMO DATA - BATCH ADVANCED SEARCH - V2 - LCMS
        System.out.println("Query - DEMO DATA - LCMS Batch Advanced Search");

        final Double[] massesArray2 = {192.0743, 301.1798, 146.4819, 90.0219, 187.0};

        final Double[] retention_timesArray2 = {18.842525, 8.425, 18.842525, 18.842525, 8.425};

        masses = Arrays.asList(massesArray2);
        retention_times = Arrays.asList(retention_timesArray2);
        all_masses = new ArrayList<Double>();
        all_retention_times = new ArrayList<Double>();

        demoData = new AdvancedBatchQuery(
                masses,
                retention_times,
                all_masses,
                all_retention_times,
                10.0,
                ToleranceMode.PPM,
                ChemAlphabet.CHNOPS,
                false,
                ModifiersType.NONE,
                MetabolitesType.AWPEPTIDES,
                MassesMode.MZ,
                IonMode.POSITIVE);

        demoData.addDatabase(Database.ALLWMINE);
        demoData.addAdduct(Adducts.PMpH);
        demoData.addAdduct(Adducts.PMpNA);

        result = gson.toJson(demoData);

        System.out.println("Query - DEMO DATA - Batch Advanced Search v2 - LCMS");
        System.out.println(result);

        received = gson.fromJson(result, AdvancedSearchQuery.class);
        System.out.println("Resultado: " + gson.toJson(received).equals(result));
    }
}
