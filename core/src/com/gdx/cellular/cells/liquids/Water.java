package com.gdx.cellular.cells.liquids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Water extends Liquid{
    
    // Constructor
    public Water(int x, int y){
        super(x, y); // Call the super constructor

        // Set the colors
        color1 = new Color(32f/255f, 135f/255f, 190f/255f, 1f);
        color2 = new Color(240f/255f, 246f/255f, 255f/255f, 1f);
        color = setColor(color1, color2, MathUtils.random());

        // Set variables
        dispersionRate = 3;
        frictionFactor = 1f;
        velocity = new Vector3(0, -124f, 0);
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
