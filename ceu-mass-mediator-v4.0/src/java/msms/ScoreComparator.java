/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.util.Comparator;

/**
 * Comparator of the application
 *
 * @author Maria Postigo. San Pablo-CEU
 * @version: 4.0, 18/04/2018
 */
public class ScoreComparator implements Comparator <MSMSCompound>{

 @Override
 public int compare(MSMSCompound a, MSMSCompound b) {
       
       if(a.getScore()<b.getScore()){return 1;}
       else if(a.getScore()>b.getScore()){return -1;}
       else {return 0;}
        
    }
  

    
}
