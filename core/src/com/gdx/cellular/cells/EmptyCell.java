package com.gdx.cellular.cells;

import java.util.Iterator;
import java.util.List;
import com.gdx.cellular.CellMatrix;

public class EmptyCell extends Element{
    // Constructor
    public EmptyCell(int x, int y){
        super(x, y); // Use the super constructor
    }
    // Override the abstract method step()
    @Override
    public void step(CellMatrix matrix) {
        List<Element> neighbours = getNeighbours(); // Get the neighbours from the array stored in the Inhertied Element class
        Iterator<Element> iterator = neighbours.iterator(); // Initialize a new iterator
        while(iterator.hasNext()){ // Iterate through all of the neighbours and remove any empty cells
            Element e = iterator.next();
            if(e.getElementEnumType() == ElementType.EMPTYCELL){
                iterator.remove();
            }
        }
        // If there is exactly 3 neighbours, cause the cell to come to life
        if(neighbours.size() == 3){
            matrix.setElementAtIndex(this.getMatrixX(), this.getMatrixY(), ElementType.STONE.createElementByMatrix(this.getMatrixX(), this.getMatrixY()));
        }
        neighbours.clear(); // Clear all the neighbours after stepping
    }
}
