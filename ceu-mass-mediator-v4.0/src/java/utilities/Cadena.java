package utilities;

import CEMS.CEMSFragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alberto Gil de la Fuente
 * @version: 4.0, 20/07/2016.
 */
public class Cadena {

    /**
     * This method extract a list with the occurrences of the pattern in the
     * String chain
     *
     * @param pattern
     * @param chain
     * @return
     */
    static public List<Float> extraerEltos(String pattern, String chain) {
        if (chain.equals("")) {
            return new LinkedList<>();
        }
        //ArrayList because it is acceded by index
        List<Float> masses = new ArrayList<Float>();

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(chain);

        while (m.find()) {
            String mass = m.group();
            masses.add(Float.valueOf(mass));
            // System.out.println("STRING: " + mass + " FLOAT: " + (Float.valueOf(m.group())) + " Double: " + Double.valueOf(mass));
        }

        return masses;
    }

    /**
     * This method extracts the masses (floats) of a chain. It returns a list
     * with the results
     *
     * @param cadena
     * @return
     */
    static public List<Float> extractFloats(String cadena) {
        return extraerEltos("([0-9]+\\.[0-9]+)|[0-9]+", cadena);
    }

    static public List<Float> extractFirstDataSpectrumFloat(String chain) {
        //ArrayList because it is acceded by index
        List<Float> firstDataSpectrum = new ArrayList<>();
        for (String line : chain.split("\\n")) {
            firstDataSpectrum.add(Cadena.extractFirstFloat(line));
        }
        return firstDataSpectrum;
    }

    /**
     *
     * @param chain
     * @return
     */
    static public Float extractFirstFloat(String chain) {
        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
        float ocurrence = 0f;

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(chain);

        if (m.find()) {
            ocurrence = Float.valueOf(m.group());
        }

        return ocurrence;
    }

    /**
     * This function searches a patron in the String chain until the end of the
     * string. Returns an array with the doubles found depending on the patron
     *
     * @param patron
     * @param chain
     * @return
     */
    static public List<Double> extractDoublesWithPatron(String patron, String chain) {
        if (chain.equals("")) {
            return new LinkedList<>();
        } else {
            //ArrayList because it is acceded by index
            List<Double> masses = new ArrayList<>();

            Pattern p = Pattern.compile(patron);
            Matcher m = p.matcher(chain);

            while (m.find()) {
                String mass = m.group();
                Double massDouble = Double.valueOf(mass);
                if (!(Math.abs(massDouble) < 0.00001d)) {
                    masses.add(Double.valueOf(mass));
                }
            }

            return masses;
        }
    }

    /**
     * This method extracts the masses (doubles) of a chain. It returns a list
     * with the results
     *
     * @param chain
     * @return
     */
    static public List<Double> extractDoubles(String chain) {
        chain = chain.replace(",", ".");
        return extractDoublesWithPatron("([0-9]+\\.[0-9]+)|[0-9]+", chain);
    }

    /**
     * Extract the mass of the first peak introduced in the composite Spectrum
     * inputTextArea.
     *
     * @param chain
     * @return
     */
    static public List<Double> extractFirstDataSpectrum(String chain) {
        //ArrayList because it is acceded by index
        List<Double> firstDataSpectrum = new ArrayList<>();
        for (String line : chain.split("\\n")) {
            firstDataSpectrum.add(Cadena.extractFirstDouble(line));
        }
        return firstDataSpectrum;
    }

    static public List<Double> getListOfDoubles(String input, int numInputMasses) {
        List<Double> retAux;
        if (!input.equals("")) {
            retAux = Cadena.extractDoubles(input);
            // If there is no time for all queryParentIonMasses, fill with 0
            for (int i = retAux.size(); i < numInputMasses; i++) {
                retAux.add(0d);
            }
        } else {
            retAux = new ArrayList<>();
            for (int i = 0; i < numInputMasses; i++) {
                retAux.add(0d);
            }
        }
        return retAux;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Double extractFirstDouble(String chain) {
        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
        Double ocurrence = 0d;

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(chain);

        if (m.find()) {
            ocurrence = Double.valueOf(m.group());
        }

        return ocurrence;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Double extractSecondDouble(String chain) {
        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
        Double ocurrence = 0d;

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(chain);

        if (m.find()) {
            if (m.find()) {
                ocurrence = Double.valueOf(m.group());
            }
        }

        return ocurrence;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Integer extractSecondNumberToInteger(String chain) {
        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
        Integer ocurrence = 0;

        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(chain);

        if (m.find()) {
            if (m.find()) {
                ocurrence = (int) (Float.parseFloat(m.group()));
            }
        }

        return ocurrence;
    }

    /**
     * Generate a Set with the key inputMass_RetentionTime in order to detect
     * the significative compounds. It process the inputtextArea written by the
     * user
     *
     * @param inputMasses
     * @param inputRetentionTimes
     * @return
     */
    static private Set<String> generateKeysSignificativeCompound(String inputMasses, String inputRetentionTimes) {
        if (inputMasses.equals("")) {
            return new HashSet<>();
        } else {
            String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";
            //ArrayList because it is acceded by index
            Set<String> keys = new HashSet<>();

            Pattern p = Pattern.compile(pattern);
            Matcher masses = p.matcher(inputMasses);
            Matcher retentionTimes = p.matcher(inputRetentionTimes);
            String mass;
            String retentionTime;
            String key;
            while (masses.find()) {
                mass = masses.group();
                if (retentionTimes.find()) {
                    retentionTime = retentionTimes.group();
                } else {
                    retentionTime = "0";
                }
                key = generateKeyOfCompound(mass, retentionTime);
                keys.add(key);
            }
            return keys;
        }
    }

    static private String generateKeyOfCompound(String inputMass, String retentionTime) {
        String separator = "_";
        String keyOfCompound = inputMass + separator + retentionTime;
        return keyOfCompound;
    }

    /**
     * Generate a Set with the key inputMass_RetentionTime in order to detect
     * the significative compounds. It process the inputtextArea written by the
     * user
     *
     * @param inputMasses
     * @param inputRetentionTimes
     * @return
     */
    static public Set<String> generateSetOfSignificativeCompounds(String inputMasses, String inputRetentionTimes) {
        Set<String> keys;
        inputMasses = inputMasses.replace(",", ".");
        inputRetentionTimes = inputRetentionTimes.replace(",", ".");

        return generateKeysSignificativeCompound(inputMasses, inputRetentionTimes);
    }
    
    /**
     * Extrac the experimental fragments (CEMSFragment) from a string input with 
 the format (mz,intensity),(mz,intensity),(mz,intenstiy)
     *
     * @param input
     * @param ionSourceVoltage
     * @return the list of the product ions
     */
    static public Set<CEMSFragment> extractExperimentalFragments(String input, Integer ionSourceVoltage) {
        Set<CEMSFragment> experimentalFragments = new TreeSet<>();
        // Two lines. Ugly way for detecting all the cases
        // (\([0-9]+\.[0-9]+\,\s*[0-9]+\.[0-9]+\))|(\([0-9]+\,\s*[0-9]+\.[0-9]+\))
        // |(\([0-9]+\.[0-9]+\,\s*[0-9]+\))|(\([0-9]+\,\s*[0-9]+\))
        // Smart way.
        // (\([0-9]+\.?[0-9]*\,\s*[0-9]+\.?[0-9]*\))
        String peakPattern = "(\\([0-9]+\\.?[0-9]*\\,\\s*[0-9]+\\.?[0-9]*\\))";
        String peak;
        Double mzValue;
        Double mzIntensity;

        Pattern p = Pattern.compile(peakPattern);

        Matcher m = p.matcher(input);

        while (m.find()) {
            peak = m.group();
            mzValue = extractFirstDouble(peak);
            mzIntensity = extractSecondDouble(peak);
            // System.out.println("GROUP: " + peak + " Value: " + peakValue + " INTENSITY: " + peakIntensity);
            CEMSFragment experimentalFragment = new CEMSFragment(null, null, null, ionSourceVoltage,
            mzValue, mzIntensity, null, null, null);
            experimentalFragments.add(experimentalFragment);
        }
        return experimentalFragments;
    }

    /**
     * Extract the complete compositeSpectrum introduced in the composite
     * Spectrum inputTextArea.
     *
     * @param chain
     * @return
     */
    static public List<Map<Double, Double>> extractDataSpectrum(String chain) {
        //ArrayList because it is acceded by index
        List<Map<Double, Double>> dataSpectrum = new ArrayList<>();
        for (String line : chain.split("\\n")) {
            dataSpectrum.add(Cadena.extractOneSpectrum(line));
        }
        return dataSpectrum;
    }

    /**
     * Extract the first Double of the chain
     *
     * @param chain
     * @return
     */
    static public Map<Double, Double> extractOneSpectrum(String chain) {
        Map<Double, Double> peaks;
        peaks = new TreeMap<>();
        // Two lines. Ugly way for detecting all the cases
        // (\([0-9]+\.[0-9]+\,\s*[0-9]+\.[0-9]+\))|(\([0-9]+\,\s*[0-9]+\.[0-9]+\))
        // |(\([0-9]+\.[0-9]+\,\s*[0-9]+\))|(\([0-9]+\,\s*[0-9]+\))
        // Smart way.
        // (\([0-9]+\.?[0-9]*\,\s*[0-9]+\.?[0-9]*\))
        String peakPattern = "(\\([0-9]+\\.?[0-9]*\\,\\s*[0-9]+\\.?[0-9]*\\))";
        String peak;
        Double peakValue;
        Double peakIntensity;

        Pattern p = Pattern.compile(peakPattern);

        Matcher m = p.matcher(chain);

        while (m.find()) {
            peak = m.group();
            peakValue = extractFirstDouble(peak);
            peakIntensity = extractSecondDouble(peak);
            // System.out.println("GROUP: " + peak + " Value: " + peakValue + " INTENSITY: " + peakIntensity);
            peaks.put(peakValue, peakIntensity);
        }
        return peaks;
    }

    /**
     * Get a list of CEMS experimental fragments sets from 
     *
     * @param input
     * @param numInputMasses
     * @param ionSourceVoltage
     * @return
     */
    public static List<Set<CEMSFragment>> getListOfExperimentalProductIons(String input, Integer numInputMasses, Integer ionSourceVoltage) {
        List<Set<CEMSFragment>> allExperimentalFragments = new ArrayList<>();
        String[] lines = input.split("\n");
        Integer linesCount = 0;
        for (String line : lines) {
            Set<CEMSFragment> experimentalFragments = Cadena.extractExperimentalFragments(input, ionSourceVoltage);
            // If there is no time for all the mzs, fill with 0
            allExperimentalFragments.add(experimentalFragments);
            linesCount++;
        }
        while(linesCount < numInputMasses)
        {
            allExperimentalFragments.add(new TreeSet<>());
            linesCount++;
        }
        return allExperimentalFragments;
    }

    /**
     * Get a list of all the spectra from Data
     *
     * @param input
     * @param numInputMasses
     * @return
     */
    public static List<Map<Double, Double>> getListOfCompositeSpectra(String input, int numInputMasses) {
        List<Map<Double, Double>> spectrumAux;
        if (!input.equals("")) {
            spectrumAux = Cadena.extractDataSpectrum(input);
            // If there is no time for all the mzs, fill with 0
            for (int i = spectrumAux.size(); i < numInputMasses; i++) {
                spectrumAux.add(new TreeMap<>());
            }
        } else {
            spectrumAux = new ArrayList<>();
            // If there is no time for all mzs, fill with 0
            for (int i = 0; i < numInputMasses; i++) {
                spectrumAux.add(new TreeMap<>());
            }
        }
        return spectrumAux;
    }

    /**
     * Method wich returns a list of boolean that indicates if the mass is in
     * the list of significative compounds or not. (True or false value)
     *
     * @param inputMasses
     * @param inputRetentionTimes
     * @param numInputMasses
     * @param keysSignificativeCompounds
     * @return
     */
    static public List<Boolean> fillIsSignificativeCompound(String inputMasses,
            String inputRetentionTimes,
            int numInputMasses,
            Set<String> keysSignificativeCompounds) {

        inputMasses = inputMasses.replace(",", ".");
        inputRetentionTimes = inputRetentionTimes.replace(",", ".");

        List<Boolean> isSignifativeCompoundAux = new ArrayList<>(numInputMasses);

        String pattern = "([0-9]+\\.[0-9]+)|[0-9]+";

        Pattern p = Pattern.compile(pattern);
        Matcher masses = p.matcher(inputMasses);
        Matcher retentionTimes = p.matcher(inputRetentionTimes);
        String mass;
        String retentionTime;
        String key;
        int i = 0;
        while (masses.find()) {
            mass = masses.group();
            if (retentionTimes.find()) {
                retentionTime = retentionTimes.group();
            } else {
                retentionTime = "0";
            }
            key = generateKeyOfCompound(mass, retentionTime);
            if (keysSignificativeCompounds.contains(key)) {
                isSignifativeCompoundAux.add(i, Boolean.TRUE);
            } else {
                isSignifativeCompoundAux.add(i, Boolean.FALSE);
            }
            i++;
        }
        return isSignifativeCompoundAux;
    }

}
