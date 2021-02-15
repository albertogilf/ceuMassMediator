/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCMS_FEATURE;

import java.util.Comparator;

/**
 *
 * @author María Postigo. San Pablo CEU
 */
public class EMComparator implements Comparator <Feature>{
     @Override
    public int compare(Feature f1, Feature f2) {
        if (f1.getEM()<f2.getEM()){return 1;}
        else if (f1.getEM()>f2.getEM()){return -1;}
        else {return 0;}
    }
}
