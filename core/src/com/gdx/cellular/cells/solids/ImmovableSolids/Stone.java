package com.gdx.cellular.cells.solids.ImmovableSolids;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

public class Stone extends ImmovableSolid{
    static Random rand = new Random(); // Create a static random object to ensure that the gaussian distribution is uniform

    // Constructor
    public Stone(int x, int y){
        
        super(x, y); // Call the super constructor

        // Set the colors
        Color color1 = new Color(102f/255f, 102f/255f, 102f/255f, 1f);
        Color color2 = new Color(140f/255f, 140f/255f, 140f/255f, 1f);
        // Random factor is gaussian to cause values to be closer to the average so there is not as much change
        color = setColor(color1, color2, (float) Math.abs(rand.nextGaussian()));
        
    }

    @Override
    public Color setColor(Color color1, Color color2, float colorFactor){

        // Interpolate the RGB values between color1 and color2 using the provided factor
        float r = MathUtils.lerp(color1.r, color2.r, colorFactor);
        float g = MathUtils.lerp(color1.g, color2.g, colorFactor);
        float b = MathUtils.lerp(color1.b, color2.b, colorFactor);
        
        return(new Color(r, g, b, 1));
    }
}
