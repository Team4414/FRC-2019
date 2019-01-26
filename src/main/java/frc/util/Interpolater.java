package frc.util;

import java.util.LinkedHashMap;
import java.util.AbstractMap.SimpleEntry;

public class Interpolater{
    public static double get(double c, LinkedHashMap<Double, Double> data){
        SimpleEntry<Double,Double> lowBound  = new SimpleEntry(Double.MIN_VALUE, 0);
        SimpleEntry<Double,Double> highBound = new SimpleEntry(Double.MIN_VALUE, 0);

        for (double val : data.keySet()){
            if (lowBound.getKey() <= c){
                lowBound = new SimpleEntry(lowBound.getKey(), data.get(lowBound.getKey()));
                highBound = new SimpleEntry(lowBound.getKey() + 1, data.get(lowBound.getKey() + 1));
            }
        }

        return ((highBound.getKey() - lowBound.getKey()) / (c - lowBound.getKey())) * (lowBound.getValue() + highBound.getValue());
    }
}