package com.gdx.cellular.cells.solids.MoveableSolids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Gravel extends MoveableSolid{
    // Constructor
    public Gravel(int x, int y){
        super(x, y); // Use the super constructor of Solid

        // Set the colors
        color1 = new Color(144f/255f, 144f/255f, 144f/255f, 1f);
        color2 = new Color(61f/255f, 55f/255f, 51f/255f, 1f);
        color = setColor(color1, color2, MathUtils.random());

        // Set initial variable values
        velocity = new Vector3(Math.random() > 0.5 ? -1 : 1, -124f,0f);
        frictionFactor = 0.6f;
        inertiaResistance = 0.7f;
    }

    @Override
    public Color setColor(Color color1, Color color2, float colorFactor){

        float randomFactor = MathUtils.random(); // Generate a random float between 0 and 1

        // Interpolate the RGB values between color1 and color2 using the random factor
        float r = MathUtils.lerp(color1.r, color2.r, randomFactor);
        float g = MathUtils.lerp(color1.g, color2.g, randomFactor);
        float b = MathUtils.lerp(color1.b, color2.b, randomFactor);
        
        return(new Color(r, g, b, 1));
    }
}
