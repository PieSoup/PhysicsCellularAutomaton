package com.gdx.cellular.utils;

public class Helpers {
    
    /**
     * Maps the given value in a certain range to a value in another range
     * @param numberMap The value that is being mapped
     * @param minMap The min range of the initial value
     * @param maxMap The max range of the intial value
     * @param minMapIndex The min range of the mapped value
     * @param maxMapIndex The max range of the mapped vavlue
     * @return The mapped value
     */
    public static float Map(float numberMap, float minMap, float maxMap, int minMapIndex, int maxMapIndex){
        return (numberMap - minMap) * (maxMapIndex - minMapIndex) / (maxMap - minMap) + minMapIndex;
    }
}
