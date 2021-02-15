/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package msms;

import java.util.List;
import facades.*;


/**
 *
 * @author María 606888798
 */
public class TestAlgorithms {
    
    public static void main(String[] args) {
        List <Msms> msms= MsmsReader.loadMsmsToTest();
        int size= msms.size();
        MSMSFacade msmsFacade= new MSMSFacade();
        int top1=0, top5=0, full=0;
        int counter=0;
       
        
        for (Msms m: msms)
        {
            List<MSMSCompound> listCandidates
                = msmsFacade.getMsmsCandidates(m, 0.1);
        //System.out.println("Number of msms candidates: " + listCandidates.size());
        List<MSMSCompound> candidatesScored
                = msmsFacade.scoreMatchedPeaks(m, listCandidates, 0.5, "Da");
        List <MSMSCompound> result= msmsFacade.getTopNMatches(candidatesScored, candidatesScored.size());
        m.setCompounds(result);
        }
        
        for(Msms m: msms)
        {
            System.out.println("REAL COMPOUND: "+m.getName());
            List <MSMSCompound> compounds= m.getCompounds();
            MSMSCompound compound=null;
            String name= "";
            
            counter=0;
            for (MSMSCompound c: compounds)
            {
                if(counter>=1){break;}
                if(c.getCompound_name().equalsIgnoreCase(m.getName())){top1=top1+1;break;}
                counter++;
            }
            
            counter=0;
            for (MSMSCompound c: compounds)
            {
                if(counter>=5){break;}
                if(c.getCompound_name().equalsIgnoreCase(m.getName())){top5=top5+1;break;}
                counter++;
            }
            for (MSMSCompound c: compounds)
            {
                System.out.println("Matched compound: "+c.getCompound_name());
                if(c.getCompound_name().equalsIgnoreCase(m.getName())){full=full+1;System.out.println("                     MATCH");break;}
            }
        }
        
        System.out.println("top 1: "+((double)(top1)/30d)*100d+"%");
        
        System.out.println("top 5: "+((double)(top5)/30d)*100d+"%");
        
        System.out.println("full: "+((double)(full)/30d)*100d+"%");
        
        msmsFacade.disconnect();
    }
    
}
