package com.gdx.cellular.cells.solids.MoveableSolids;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Sand extends MoveableSolid{
    // Constructor
    public Sand(int x, int y){
        super(x, y); // Use the super constructor of Solid

        // Set the colors
        color1 = new Color(0.949019607843f, 0.796078431373f, 0.341176470588f, 1f);
        color2 = new Color(0.650980392157f, 0.411764705882f, 0.2f, 1f);
        color = setColor(color1, color2, MathUtils.random());

        // Set initial values for physics variables
        velocity = new Vector3(Math.random() > 0.5 ? -1 : 1, -124f,0f);
        frictionFactor = 0.9f;
        inertiaResistance = 0.1f;
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
