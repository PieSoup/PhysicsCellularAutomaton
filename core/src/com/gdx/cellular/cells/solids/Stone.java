package com.gdx.cellular.cells.solids;

import java.util.Iterator;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.ElementType;
import com.gdx.cellular.CellMatrix;

public class Stone extends Solid{
    // Constructor
    public Stone(int x, int y){
        super(x, y); // Use the super constructor of Solid
        color = new Color(Color.WHITE); // Set the color to white
    }
    // Override the abstract method step()
    @Override
    public void step(CellMatrix matrix){
        List<Element> neighbours = getNeighbours(); // Get the neighbours from the array stored in the Inhertied Element class
        Iterator<Element> iterator = neighbours.iterator(); // Initialize a new iterator for the neighbours
        while(iterator.hasNext()){ // Iterate through all of the neighbours and remove any empty cells
            Element e = iterator.next();
            if(e.getElementEnumType() == ElementType.EMPTYCELL){
                iterator.remove();
            }
        }
        // If the cell has less than 2 or more than 3 living neighbours, then die
        if(neighbours.size() < 2 || neighbours.size() > 3){
            die(matrix);
        }
        neighbours.clear(); // Clear the neighbours after stepping
    }
}
