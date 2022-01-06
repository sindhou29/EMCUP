/*
 * Copyright (c) 2018 Energy Market Company Pte. Ltd.
 * 
 * All rights reserved
 * DatabaseReader.java
 * Version:
 *   0.1 2018/03/01
 * 
 * Revisions:
  *   Converted from OPA 8.0 rules
 */
package com.emc.sett.rules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.emc.sett.model.FacilityT;

/*
 * 
 * @author Tse Hing Chuen
 */
public class SettlementFunctions {
    
    public SettlementFunctions( ) {
    }
	
	/*
	 * A.4.2, OSZ(z) >= OSZ(z+1)
	 * 
	 *   nodes  - array of current entities
	 *   shd    - size of the node
	 *   isPCU  - indicator of PCU
	 *   
	 * @return the size index
	 */
    public static BigDecimal getPCUSizeIndex(Collection<FacilityT> nodes, BigDecimal shd, Boolean isPCU) {
    	
		List <BigDecimal> listOfPCU = new ArrayList<BigDecimal>();
		List <Double> listOfUsedIdx = new ArrayList<Double>();
		Double posSoFar = 0.0;
		
    	if (isPCU == true) {
    	
	    	for (FacilityT nde : nodes) {
	    		
	    		if (nde.isPcu() == true) {
	    			listOfPCU.add(nde.getShd());
	    			
	    			BigDecimal sizeIdx = nde.getSizeIndex();
	    			if (sizeIdx != null && sizeIdx.compareTo(BigDecimal.ZERO) > 0) {
	    				listOfUsedIdx.add(sizeIdx.doubleValue());
	    			}
	    		}
	    	}
	
			Collections.sort(listOfPCU);
			Collections.reverse(listOfPCU);
			
			for (BigDecimal targetSize : listOfPCU) {
	            posSoFar = posSoFar + 1.0;
	            if (targetSize.compareTo(shd) == 0) {
	                if (listOfUsedIdx.contains(posSoFar) == false) {
	                    return new BigDecimal(posSoFar);
	                }
	            }
	        }
    	}
    	
        return BigDecimal.ZERO;
    }
    
	/*
	 * A.6.2, RTQ(z) = OSZ(z) - OSZ(z+1), with OSZ(Z+1) = CSZ
	 * 
	 *   nodes  - array of current entities
	 *   idx    - size index of the node
	 *   shd    - size of the node
	 *   csz    - cutoff size
	 *   
	 * @return the reserve tier quantity
	 */
    public static BigDecimal getReserveTierQuantity(Collection<FacilityT> nodes, BigDecimal idx, BigDecimal shd, BigDecimal csz) {
    	
        BigDecimal currentPCUSize;
        BigDecimal nextSizeIndex;
        BigDecimal nextPCUSize = null;
        BigDecimal cutoffSize;
        BigDecimal isPCU;
        
        if (idx == null || idx.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		nextSizeIndex = idx.add(BigDecimal.ONE);

        if (shd == null || shd.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
		
		currentPCUSize = shd;

        if (csz == null) {
            return BigDecimal.ZERO;
        }
		
		cutoffSize = csz;
 
    	for (FacilityT nde : nodes) {

    		isPCU = nde.getSizeIndex();

            if (isPCU != null) {
                if (nextSizeIndex.compareTo(isPCU) == 0) {
           			nextPCUSize = nde.getShd();
           			break;
                }
            }
        }
                
        if (nextPCUSize == null) {
        	currentPCUSize = currentPCUSize.subtract(cutoffSize);
        } else {
        	currentPCUSize = currentPCUSize.subtract(nextPCUSize);
        }
        
        return currentPCUSize;
    }
    
    /*
     * A.7.3, IPW(z) = sum from IPF(1) to IPF(z)
     * 
	 *   nodes  - array of current entities
     *   idx    - size index of the node
     *   ipf    - IPF of the node
     * 
     * @return interval probability weights
     */
    public static BigDecimal getIntervalProbabilityWeights(Collection<FacilityT> nodes, BigDecimal idx, BigDecimal ipf) {

		BigDecimal accumulatedIPF = null;
		BigDecimal currentIPF;
		BigDecimal isPCU;
		
		if (idx == null || idx.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		
		if (ipf == null || ipf.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}
		
		accumulatedIPF = ipf;
		
		for (FacilityT nde : nodes) {
		
			isPCU = nde.getSizeIndex();
		
		    if (isPCU != null) {
		        if (isPCU.compareTo(idx) < 0) {
		        	currentIPF = nde.getIpf();
		    		
		    		if (currentIPF != null) {
		                accumulatedIPF = accumulatedIPF.add(currentIPF);
		    		}
		        }
		    }
		}
		
		return accumulatedIPF;
	}

    /*
     * A.9.1, the factor(z) = sum from RTS(z)/IPW(z) to RTS(Z)/IPW(Z)
     * 
	 *   nodes  - array of current entities
     *   idx    - size index of the node
     *   rts    - RTS of the node
     *   ipw    - IPW of the node
     * 
     * @return the ordered reserve share factor
     */
    public static BigDecimal getOrderedReserveShareFactor(Collection<FacilityT> nodes, BigDecimal idx, BigDecimal rts, BigDecimal ipw) {

        BigDecimal accumulatedRSF = new BigDecimal("0.0");
        BigDecimal currentRTS;
        BigDecimal currentIPW;
        BigDecimal isPCU;

		if (idx == null || idx.compareTo(BigDecimal.ZERO) == 0) {
			return BigDecimal.ZERO;
		}

		if (rts == null) {
			return BigDecimal.ZERO;
		}

		if (ipw == null) {
			return BigDecimal.ZERO;
		}

		if(ipw.signum() != 0) {
			accumulatedRSF = accumulatedRSF.add(rts.divide(ipw, BigDecimal.ROUND_HALF_UP));
		}

		for (FacilityT nde : nodes) {
			    		
			isPCU = nde.getSizeIndex();

            if (isPCU != null) {
            	
                if (isPCU.compareTo(idx) > 0) {

                	currentRTS = nde.getRts();
           			currentIPW = nde.getIpw();
            		
            		if (currentRTS != null && currentIPW != null) {
            			
            			if(currentIPW.signum() != 0) {
            				
               				accumulatedRSF = accumulatedRSF.add(currentRTS.divide(currentIPW, BigDecimal.ROUND_HALF_UP));
            			}
            		}
                }
            }
        }

        return accumulatedRSF;
    }
}