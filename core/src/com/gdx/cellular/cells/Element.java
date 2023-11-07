package com.gdx.cellular.cells;

import java.util.BitSet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellularAutomaton;
import com.gdx.cellular.CellMatrix;

public abstract class Element {
    
    // Declare variables
    private int matrixX;
    private int matrixY;
    private int pixelX;
    private int pixelY;
    public BitSet stepped = new BitSet(1);
    public boolean isFreeFalling = true;
    public float inertiaResistance;

    public ElementType elementType;

    public Color color;
    public Color color1;
    public Color color2;
    public float colorFactor;

    public Vector3 velocity;
    public float xThreshold = 0;
    public float yThreshold = 0;
    public float frictionFactor;

    // Constructor
    public Element(int x, int y){
        setCoordinatesByMatrix(x, y); // Set the coordinates of the element in the matrix
        elementType = getElementEnumType(); // Set the element type

        stepped.set(0, CellularAutomaton.stepped.get(0));
    }

    /**
     * Set the coordinates of the element when initializing it
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setCoordinatesByMatrix(int x, int y){
        setMatrixX(x); // Set X
        setMatrixY(y); // Set Y
    }
    
    /**
     * Set the x coordinate in matrix and pixel space
     * @param x The x coordinate
     */
    private void setMatrixX(int x){
        matrixX = x;
        pixelX = toPixel(x);
    }
    /**
     * Set the y coordinate in matrix and pixel space
     * @param y The y coordinate
     */
    private void setMatrixY(int y){
        matrixY = y;
        pixelY = toPixel(y);
    }

    /**
     * Method to get the element type of the current instance
     * @return The element type
     */
    public ElementType getElementEnumType() {
        return ElementType.valueOf(this.getClass().getSimpleName().toUpperCase()); // Each enum type will be the class name in uppercase
    }

    /**
     * Method to swap the position of this element with another
     * @param matrix The cell matrix
     * @param toSwap The element to swap with
     * @param toSwapX The x coordinate of the element to swap with
     * @param toSwapY The y coordinate of the element to swap with
     */
    public void swapPositions(CellMatrix matrix, Element toSwap, int toSwapX, int toSwapY) {
        // Check if the element is already at the position it is swapping to
        if (this.getMatrixX() == toSwapX && this.getMatrixY() == toSwapY) {
            return; // If it is, return
        }
        // Otherwise, set the element at the current coordiantes to the element that it is swapping with
        matrix.setElementAtIndex(this.getMatrixX(), this.getMatrixY(), toSwap);
        // Then set the element that it is swapping with to itself, successfully swapping them
        matrix.setElementAtIndex(toSwapX, toSwapY, this);
    }

    /**
     * Method to move to the last valid location it was at
     * @param matrix The cell matrix
     * @param moveToLocation The location to move to
     */
    public void moveToLastValid(CellMatrix matrix, Vector3 moveToLocation) {
        // Check if the location it is moving to is already its current location, if so, return
        if ((int) (moveToLocation.x) == getMatrixX() && (int) (moveToLocation.y) == getMatrixY()) return;

        // Get the element at the position it will be moving to
        Element toSwap = matrix.get(moveToLocation.x, moveToLocation.y);
        // Swap positions with that cell
        swapPositions(matrix, toSwap, (int) moveToLocation.x, (int) moveToLocation.y);
    }

    /**
     * Moves the current element to the last valid position specified by the target position {@code moveToPosition}
     * in the given {@code CellMatrix}. If the current element or the element to be swapped is present at the target
     * position, swaps their positions. If the current element and the element to be swapped are the same, swaps the
     * positions with their neighbor at the target position.
     * @param matrix The CellMatrix in which the elements are stored.
     * @param toSwap The element that needs to be swapped with the current element.
     * @param toSwapX The X-coordinate of the element to be swapped in the matrix.
     * @param toSwapY The Y-coordinate of the element to be swapped in the matrix.
     * @param moveToPosition The target position to which the current element should be moved.
     */
    public void moveToLastValidAndSwap(CellMatrix matrix, Element toSwap, int toSwapX, int toSwapY, Vector3 moveToPosition){
        // Get x and y coordinates from target position Vector3 and store them in variables
        int movePosX = (int) moveToPosition.x;
        int movePosY = (int) moveToPosition.y;

        // Get the element at the target position in the matrix
        Element neighbour = matrix.get(movePosX, movePosY);

        // Check if the current element or the element to be swapped is the same as the neighbor at the target position.
        if(this == neighbour || toSwap == neighbour){
            // If true, swap the positions of the current element and the element to be swapped in the matrix.
            this.swapPositions(matrix, toSwap, toSwapX, toSwapY);
            return;
        }
        
        if(this == toSwap){
            // If true, swap the positions of the current element and the neighbor at the target position in the matrix.
            this.swapPositions(matrix, neighbour, movePosX, movePosY);
            return;
        }

        // Move the current element to the target position in the matrix.
        matrix.setElementAtIndex(this.getMatrixX(), this.getMatrixY(), neighbour);
        // Move the element to be swapped to the original position of the current element in the matrix.
        matrix.setElementAtIndex(toSwapX, toSwapY, this);
        // Move the element to the target position in the matrix.
        matrix.setElementAtIndex(movePosX, movePosY, toSwap);
    }

    /**
     * Gets the x coordinate in matrix space
     * @return The int x coordinate in matrix space
     */
    public int getMatrixX() {
        return matrixX;
    }

    /**
     * Gets the y coordinate in matrix space
     * @return The int y coordinate in matrix space
     */
    public int getMatrixY() {
        return matrixY;
    }

    /**
     * Gets the x coordinate in pixel space
     * @return The int x coordinate in pixel space
     */
    public int getPixelX() {
        return pixelX;
    }

    /**
     * Gets the y coordinate in pixel space
     * @return The int y coordinate in pixel space
     */
    public int getPixelY() {
        return pixelY;
    }

    /**
     * Gets the current type of the element
     * @return The type of the element
     */
    public ElementType getElementType(){
        return elementType;
    }

    /**
     * This method converts the given matrix coordinate to pixel coordinates
     * @param val The matrix coordinate
     * @return The converted pixel coordinate
     */
    private int toPixel(int val){
        return (int) Math.floor(val / CellularAutomaton.getCellSizeModifier());
    }
    
    /**
     * This method is run every frame for the cell and determines the behaviour it should follow
     * @param matrix The cell matrix
     */
    public abstract void step(CellMatrix matrix); // Inheritable method step()

    /**
     * This method performs actions on the neighboring element based on its type and position.
     * @param neighbour The neighboring element.
     * @param modifiedX The modified x-coordinate of the neighbor.
     * @param modifiedY The modified y-coordinate of the neighbor.
     * @param matrix The cell matrix containing the elements.
     * @param isFinal Indicates if this is the final iteration.
     * @param isFirst Indicates if this is the first iteration.
     * @param lastValidLocation The last valid location before encountering obstacles.
     * @param depth The depth of recursion.
     * @return True if movement should stop, false otherwise.
     */
    protected abstract boolean actOnElementNeighbour(Element neighbour, int modifiedX, int modifiedY, CellMatrix matrix, boolean isFinal, boolean isFirst, Vector3 lastValidLocation, int depth);

    /**
     * This method can be overwritten to determine how the color for the element should be set
     * @param color1 The first color
     * @param color2 The second color
     * @param colorFactor How much the colors should be interpolate by (0 - 1)
     * @return The interpolated color
     */
    public abstract Color setColor(Color color1, Color color2, float colorFactor);
}
