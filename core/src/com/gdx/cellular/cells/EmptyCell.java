package com.gdx.cellular.cells;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellMatrix;

public class EmptyCell extends Element{
    private static Element element;
    // Constructor
    public EmptyCell(int x, int y){
        super(x, y); // Use the super constructor
        // Set the color
        color = setColor(null, null, -1);
    }

    /**
     * This method is unique to empty cells, since they have no behviour there is no need to update them, so it is more efficient to use a singleton
     * @return The singleton element instance
     */
    public static Element getInstance(){
        if(element == null){
            element = new EmptyCell(-1, -1);
        }
        return element;
    }
    
    // Override the abstract method step()
    @Override
    public void step(CellMatrix matrix) {}

    @Override
    public Color setColor(Color color1, Color color2, float colorFactor){
        // Since empty cells are empty, just show black as the color
        return Color.BLACK;
    }
    
    // Override the abstract method actOnElementNeighbour
    @Override
    protected boolean actOnElementNeighbour(Element neighbour, int modifiedX, int modifiedY, CellMatrix matrix, boolean isFinal, boolean isFirst, Vector3 lastValidLocation, int depth){
        return true; // Just return since empty cells to nothing
    }
}
