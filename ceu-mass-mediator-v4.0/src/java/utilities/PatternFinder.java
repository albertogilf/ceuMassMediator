package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author USUARIO
 * @version: 4.0, 20/07/2016. Modified by Alberto Gil de la Fuente
 */
public class PatternFinder {

    /**
     * Function searchWithReplacement
     *
     * @param content String of content
     * @param pattern pattern to search
     * @param stringToRemove stringToremove in content
     * @return a String with content which reaches the pattern
     */
    public String searchWithReplacement(String content, String pattern, String stringToRemove) {
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);

        Matcher m = p.matcher(content);
        
        StringBuilder sb = new StringBuilder();
        String response = "";

        while (m.find()) {
            sb.append(m.group().replaceAll(stringToRemove, ""));
            sb.append("\n");
        }
        response = sb.toString();
        return response;
    }

    /**
     * Function searchWithoutReplacement
     *
     * @param content String of content
     * @param pattern pattern to search
     * @return a String with content which reaches the pattern
     */
    public String searchWithoutReplacement(String content, String pattern) {
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);

        Matcher m = p.matcher(content);

        StringBuilder sb = new StringBuilder();
        String response = "";

        while (m.find()) {
            sb.append(m.group());
            sb.append("\n");
        }
        response = sb.toString();
        return response;
    }

    /**
     * Function searchList
     *
     * @param content String of content
     * @param pattern pattern to search
     * @param stringToRemove stringToremove in content
     * @return arraylist with content which reaches the pattern
     */
    public ArrayList<String> searchList(String content, String pattern, String stringToRemove) {
        Pattern p = Pattern.compile(pattern, Pattern.DOTALL);

        Matcher m = p.matcher(content);

        ArrayList<String> respuesta = new ArrayList<>();

        while (m.find()) {
            respuesta.add(m.group().replaceAll(stringToRemove, ""));

        }

        return respuesta;
    }

    /**
     * Function searchInDifferentCalls
     *
     * @param content StringBuilder of content
     * @param pattern pattern to search
     * @param stringToRemove stringToremove in content
     * @param result StringBuilder to save the result
     * @return arraylist with content which reaches the pattern
     */
    // What does this function do exactly?
    // Why delete at the beginning all the results?
    // I think it is only to reuse the stringBuilder
    public boolean searchInDifferentCalls(StringBuilder content, String pattern, String stringToRemove, StringBuilder result) {
        result.delete(0, result.length());
        Pattern patternContextoDato = Pattern.compile(pattern, Pattern.DOTALL);
        Matcher matcherContextoDato = patternContextoDato.matcher(content);
        int start = 0;
        boolean found = matcherContextoDato.find(start);
        if (found) {
            //start = matcherContextoDato.end();
            
            String localResult = matcherContextoDato.group().replaceAll(stringToRemove, "");

            localResult = localResult.replaceAll("\"", "''");
            localResult = localResult.replaceAll("\n", "");
            result.append(localResult);
        }

        return found;
    }

    public double searchDouble(String content) {
        double result = 0.0d;
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+))");
        Matcher m = p.matcher(content);
        while (m.find()) {
            result = Double.parseDouble(m.group(1));
            System.out.println(result);
        }

        return result;
    }
    
     public static List<Double> readDoublesFromFile(String PATH) {
        List<Double> listDoubles = new LinkedList<Double>();

        Scanner scan;
        File file = new File(PATH);
        try {
            scan = new Scanner(file);

            while (scan.hasNextDouble()) {
                Double number = scan.nextDouble();
                //System.out.println(number);
                listDoubles.add(number);
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        return listDoubles;
    }

}
