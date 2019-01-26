package frc.util;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.AbstractMap.SimpleEntry;

public class Interpolater{
    public static double get(double c, LinkedHashMap<Double, Double> data){
        SimpleEntry<Double,Double> lowBound  = new SimpleEntry<Double,Double>(Double.MIN_VALUE, 0d);
        SimpleEntry<Double,Double> highBound = new SimpleEntry<Double,Double>(Double.MAX_VALUE, 0d);

        double distLow = Double.MAX_VALUE;
        double distHigh = Double.MAX_VALUE;

        for (double val : data.keySet()){
            
            if (((c - val) > 0) & ((c - val) < distLow)){
                lowBound = new SimpleEntry<Double,Double>(val, data.get(val));
                distLow = (c - val);
            }

            if (((val - c) > 0) & ((val - c) < distHigh)){
                highBound = new SimpleEntry<Double,Double>(val, data.get(val));
                distHigh = (c - val);
            }
        }

        return ((highBound.getKey() - lowBound.getKey()) / (c - lowBound.getKey())) * (lowBound.getValue() + highBound.getValue());
    }
}