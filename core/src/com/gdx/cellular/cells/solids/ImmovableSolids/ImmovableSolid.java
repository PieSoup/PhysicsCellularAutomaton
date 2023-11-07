package com.gdx.cellular.cells.solids.ImmovableSolids;

import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellMatrix;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.solids.Solid;

public abstract class ImmovableSolid extends Solid{
    
    // Constructor
    public ImmovableSolid(int x, int y){
        super(x, y); // Call the super constructor
        // Set the velocity to 0 as it does not move
        velocity = new Vector3(0f, 0f, 0f);
    }

    // Following methods are currently unused, but overriding them because they are abstract
    @Override
    public void step(CellMatrix matrix){
        return;
    }

    @Override
    protected boolean actOnElementNeighbour(Element neighbour, int modifiedX, int modifiedY, CellMatrix matrix, boolean isFinal, boolean isFirst, Vector3 lastValidLocaton, int depth){
        return true;
    }
}
