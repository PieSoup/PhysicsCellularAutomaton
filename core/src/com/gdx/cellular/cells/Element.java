package com.gdx.cellular.cells;

import java.util.List;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.Color;
import com.gdx.cellular.CellularAutomaton;
import com.gdx.cellular.CellMatrix;

public abstract class Element {
    
    // Declare variables
    private int matrixX;
    private int matrixY;
    private int pixelX;
    private int pixelY;

    private int neighbourhoodSearchSize = 1;
    private List<Element> neighbours = new ArrayList<Element>();

    public ElementType elementType;

    public Color color;

    // Constructor
    public Element(int x, int y){
        setCoordinatesByMatrix(x, y); // Set the coordinates of the element in the matrix
        elementType = getElementEnumType(); // Set the element type
    }

    // Set coordinates method
    private void setCoordinatesByMatrix(int x, int y){
        setMatrixX(x); // Set X
        setMatrixY(y); // Set Y
    }
    // Set both the matrix position and the pixel position using toPixel()
    private void setMatrixX(int x){
        matrixX = x;
        pixelX = toPixel(x);
    }

    private void setMatrixY(int y){
        matrixY = y;
        pixelY = toPixel(y);
    }

    // Method to return the element type of this element
    public ElementType getElementEnumType() {
        return ElementType.valueOf(this.getClass().getSimpleName().toUpperCase()); // Each enum type will be the class name in uppercase
    }

    // Get the neighbourhood of the current cell
    public void getNeighbourhood(CellMatrix matrix){
        // Loop through all of the cells around it in the range of neighbourhoodSearchSize
        for(int y = neighbourhoodSearchSize * -1; y <= neighbourhoodSearchSize; y++){
            for(int x = neighbourhoodSearchSize * -1; x <= neighbourhoodSearchSize; x++){
                // Check if the neighbour is in the bounds of the screen
                boolean inBoundsX = (matrixX + x >= 0) && (matrixX + x < matrix.getArraySizeX());
                boolean inBoundsY = (matrixY + y >= 0) && (matrixY + y < matrix.getArraySizeY());
                if(inBoundsX && inBoundsY && (x != 0 || y != 0)){ // If it is in bounds and not at the same position of the cell itself
                    neighbours.add(matrix.getElementByIndex(matrixX + x, matrixY + y)); // Add the neighbour to the array
                }
            }
        }
    }
    
    // Get the neighbours of the cell
    public List<Element> getNeighbours() {
        return neighbours;
    }

    // Method runs when the cell "dies"
    public void die(CellMatrix matrix){
        // Set the element to an empty cell
        matrix.setElementAtIndex(matrixX, matrixY, ElementType.EMPTYCELL.createElementByMatrix(matrixX, matrixY));
    }

    // Get the matrix coordinates on the X
    public int getMatrixX() {
        return matrixX;
    }

    // Get the matrix coordinates on the Y
    public int getMatrixY() {
        return matrixY;
    }

    // Get the pixel coordinates on the X
    public int getPixelX() {
        return pixelX;
    }

    // Get the pixel coordinates on the Y
    public int getPixelY() {
        return pixelY;
    }

    // Convert matrix coordinates to their pixel position
    private int toPixel(int val){
        return (int) Math.floor(val / CellularAutomaton.getCellSizeModifier());
    }
    public abstract void step(CellMatrix matrix); // Inheritable method step()
}
