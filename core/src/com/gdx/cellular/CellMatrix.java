package com.gdx.cellular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.ElementType;
import com.gdx.cellular.input.InputHandler;

public class CellMatrix {
    // Matrix of cells is stored in a 2D array
    Array<Array<Element>> matrix;

    // Initialize variables
    private int xArrSize;
    private int yArrSize;
    private int cellSizeModifier;

    private final List<Integer> shuffledXIndexes;
    // Constructor
    public CellMatrix(int width, int height, int cellSizeModifier){
        // Set variables
        this.cellSizeModifier = cellSizeModifier;
        this.xArrSize = toMatrix(width);
        this.yArrSize = toMatrix(height);
        this.shuffledXIndexes = generateShuffledIndexes(xArrSize);
        
        matrix = generateMatrix(); // Initialize the matrix with empty cells

    }
    /**
     * Generates a new empty matrix defined by the x and y array sizes
     * @return 2D Element Array
     */
    private Array<Array<Element>> generateMatrix(){
        Array<Array<Element>> yArray = new Array<>(true, yArrSize); // Array of rows
        for(int y = 0; y < yArrSize; y++){ // For each required row
            Array<Element> xArray = new Array<>(true, xArrSize); // Create a row of elements
            for(int x = 0; x < xArrSize; x++){ // For required element
                xArray.add(ElementType.EMPTYCELL.createElementByMatrix(x, y)); // Add a new empty cell
            }
            yArray.add(xArray); // Once the row has been generated, at it to the array of rows
        }
        return yArray; // Once the matrix has been generated, return it
    }

    /**
     * Steps and draws all of the cells
     * @param sr The Shaperenderer object
     */
    public void stepAndDrawAll(ShapeRenderer sr){
        stepAll();
        drawAll(sr);
    }
    /**
     * Steps all of the cells
     */
    public void stepAll(){
        // Loop through every cell using nested loops and run their step() method
        for(int y = 0; y < yArrSize; y++){
            Array<Element> row = getRow(y);
            for(int x : getShuffledXIndexes()){
                Element element = row.get(x);
                    element.step(this);
            }
        }
    }
    /**
     * Draws all of the cells
     * @param sr The Shaperenderer object
     */
    public void drawAll(ShapeRenderer sr){
        // Initialize the shape renderer
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Filled); // Set the type to filled
        // Loop through all of the elements
        for(int y = 0; y < yArrSize; y++){
            Array<Element> row = getRow(y);
            for(int x = 0; x < xArrSize; x++){
                Element element = row.get(x);
                // If the element does not have a set color, set it to black. Otherwise set it to the element's color
                if(element.color != null){
                    sr.setColor(element.color);
                }
                else{
                    sr.setColor(Color.BLACK);
                }
                sr.rect(toPixel(x), toPixel(y), rectDrawSize(x), rectDrawSize(y)); // Draw a rectangle to represent that cell
            }
        }
        sr.end(); // End the shape renderer
        //drawGrid(sr); // Draw the grid
    }
    /**
     * Draws the grid (optional)
     * @param sr The Shaperenderer object
     */
    private void drawGrid(ShapeRenderer sr){
		sr.setColor(0.3f, 0.3f, 0.3f, 0.3f); // Set the color to a gray color
		sr.begin(ShapeRenderer.ShapeType.Line); // Set the shape renderer type to line
        // Draw all of the horizontal lines
		for(int y = 0; y < yArrSize; y++){
			float linePos = toPixel(y);
			sr.line(0, linePos, CellularAutomaton.getScreenWidth(), linePos);
		}
        // Draw all of the vertical lines
		for(int x = 0; x < xArrSize; x++){
			float linePos = toPixel(x);
			sr.line(linePos, 0, linePos, CellularAutomaton.getScreenHeight());
		}
		sr.end(); // End the shape renderer
	}

    /**
     * Spawn elements at pixel coordinates using a brush
     * @param pixelX The x coordinate in pixel coordinates
     * @param pixelY The y coordinate in pixel coordinates
     * @param elementType The type of element to spawn
     * @param brushSize The diameter of the brush
     * @param brushType The type of brush
     */
    public void spawnElementByPixelWithBrush(int pixelX, int pixelY, ElementType elementType, int brushSize, InputHandler.BRUSHTYPE brushType){
        // Convert the pixel coordinates to matrix coordinates
        int matrixX = toMatrix(pixelX);
        int matrixY = toMatrix(pixelY);
        // Use the matrix coordinates to spawn the elements
        spawnElementByMatrixWithBrush(matrixX, matrixY, elementType, brushSize, brushType);
    }
    /**
     * Spawn a singular element using pixel coordinates
     * @param pixelX the x coordinate in pixel coordinates
     * @param pixelY the y coordinate in pixel coordinates
     * @param elementType the type of element to spawn
     */
    public void spawnElementByPixel(int pixelX, int pixelY, ElementType elementType){
        // Convert the pixel coordinates to matrix coordinates
        int matrixX = toMatrix(pixelX);
        int matrixY = toMatrix(pixelY);
        // Use the matrix coordinates to spawn the element
        spawnElementByMatrix(matrixX, matrixY, elementType);
    }

    /**
     * Spawn elements at matrix coordinates using a brush
     * @param matrixX the x coordinate using matrix coordinates
     * @param matrixY the y coordinate using matrix coordinates
     * @param elementType the type of element to spawn
     * @param brushSize the diameter of the brush
     * @param brushType the type of brush
     */
    private void spawnElementByMatrixWithBrush(int matrixX, int matrixY, ElementType elementType, int brushSize, InputHandler.BRUSHTYPE brushType){
        // Calculate the distance from the middle of the brush to the edge
        int halfBrush = (int) Math.floor(brushSize / 2);
        // Loop through each element within the brush's range
        for(int x = matrixX - halfBrush; x <= matrixX + halfBrush; x++){
            for(int y = matrixY - halfBrush; y <= matrixY + halfBrush; y++){
                if(brushType.equals(InputHandler.BRUSHTYPE.CIRCLE)){
                    // If the brush type is a circle, calculate the distance between the center of the brush and the current position it is spawning at
                    int distance = distanceBetweenTwoPoints(matrixX, x, matrixY, y);
                    // If the distance is less than the brush, then draw an element there
                    // Using the distance function allows for testing distance diagonally instead of just cardinally
                    if(distance < halfBrush){
                        spawnElementByMatrix(x, y, elementType);
                    }
                }
                else{
                    // Otherwise just create a square of the element
                    spawnElementByMatrix(x, y, elementType);
                }
            }
        }
    }
    
    /**
     * Spawn a singular element using matrix coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @param elementType the type of the new element
     */
    public void spawnElementByMatrix(int x, int y, ElementType elementType){
        if(!isWithinBounds(x, y)) return;
        setElementAtIndex(x, y, elementType.createElementByMatrix(x, y));
    }

    /**
     * Set the given element at given float matrix coordinatess
     * @param x the float x coordinate
     * @param y the float y coordinate
     * @param newElement the new element
     */
    public void setElementAtIndex(float x, float y, Element newElement){
        setElementAtIndex((int) x, (int) y, newElement);
    }
    /**
     * Set the given element at given matrix coordinates
     * @param x the x coordinate
     * @param y the y coordinate
     * @param newElement the new element
     */
    public void setElementAtIndex(int x, int y, Element newElement){
        // Get the coordinates and the element, and spawn the new element there if it is in bounds
        if(isWithinBounds(x, y)){
            matrix.get(y).set(x, newElement);
            newElement.setCoordinatesByMatrix(x, y);
        } 
    }

    /**
     * Determines if the given coordinates are within the bounds of the matrix
     * @param x The x coordinate in matrix coordinates
     * @param y The y coordinate in matrix coordinates
     * @return boolean True if it is in bounds, false if it isn't
     */
    public boolean isWithinBounds(int x, int y){
        if(x >= 0 && x < xArrSize && y >= 0 && y < yArrSize) return true;
        return false;
    }

    /**
     * Calculates the distance between two points on the matrix
     * @param x1 x coordinate of the first point in matrix coordinates
     * @param x2 x coordinate of the second point in matrix coordinates
     * @param y1 y coordinate of the first point in matrix coordinates
     * @param y2 y coordinate of the second point in matrix coordinates
     * @return int The distance between the two points
     */
    public static int distanceBetweenTwoPoints(int x1, int x2, int y1, int y2) {
        return (int) Math.ceil(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)));
    }

    /**
     * Gets a specified row
     * @param index The y coordinate of the row
     * @return Element Array All the elements in the row
     */
    private Array<Element> getRow(int index){
        return matrix.get(index);
    }

    public Element get(float x, float y) {
        return get((int) x, (int) y);
    }
    /**
     * Get the element at the given coordinates if it is in bounds
     * @param x The x coordinate in matrix coordinates
     * @param y The y coordinate in matrix coordinates
     * @return Element The element at the coordinates
     */
    public Element get(int x, int y){
        if(isWithinBounds(x, y)){
            return matrix.get(y).get(x);
        }
        else{
            return null;
        }
    }

    /**
     * Get the shuffled x indexes
     * @return List of Integers
     */
    public List<Integer> getShuffledXIndexes() {
        return shuffledXIndexes;
    }

    /**
     * Shuffles the x indexes
     */
    public void reshuffleXIndexes(){
        Collections.shuffle(shuffledXIndexes);
    }

    /**
     * Generate the x indexes
     * @param size the size of the array that the indexes are being shuffled for
     * @return List of Integers
     */
    private List<Integer> generateShuffledIndexes(int size){
        List<Integer> list = new ArrayList<>(size);
        for(int i = 0; i < size; i++){
            list.add(i);
        }
        return list;
    }

    /**
     * Determine where the end of the cell should be drawn
     * @param index the index of the cell
     * @return int
     */
    private int rectDrawSize(int index){
        // Get the index, convert it to matrix coordinates, and add because it starts at 0
        return index * cellSizeModifier + cellSizeModifier;
    }

    /**
     * Convert the given value to matrix coordinates
     * @param val The coordinate to convert
     * @return the converted int coordinate
     */
    public int toMatrix(int val){
        return val / cellSizeModifier;
    }

    /**
     * Convert the given value to pixel coordinates
     * @param val
     * @return
     */
    public int toPixel(int val){
        return val * cellSizeModifier;
    }

    /** 
     * Get how many cells are on the x axis
     * @return Integer of the number of cells on the x axis
    */
    public int getArraySizeX(){
        return xArrSize;
    }
    /**
     * Get the number of cells on the y axis
     * @return Integer of the number of cells on the y axis
     */
    public int getArraySizeY(){
        return yArrSize;
    }

    /**
     * Uses a linear function to draw elements in between frames to avoid spaces in between the drawing points
     * Especially useful at larger resolutions where it is laggier
     * @param pos1 The previous position
     * @param pos2 The new position
     * @param elementType The element to spawn
     * @param brushSize The size of the brush
     * @param brushType The type of the brush
     */
    public void spawnElementBetweenTwoPoints(Vector3 pos1, Vector3 pos2, ElementType elementType, int brushSize, InputHandler.BRUSHTYPE brushType){

        // Convert the position coordinates to matrix coordinates
        int matrixX1 = toMatrix((int) pos1.x);
        int matrixY1 = toMatrix((int) pos1.y);
        int matrixX2 = toMatrix((int) pos2.x);
        int matrixY2 = toMatrix((int) pos2.y);

        // If the positions are the same, no need to iterate
        if(pos1.epsilonEquals(pos2)){
            spawnElementByMatrixWithBrush(matrixX1, matrixY1, elementType, brushSize, brushType);
            return;
        }

        // Calculate the difference between the points on the x and y axis
        int xDifference = matrixX2 - matrixX1;
        int yDifference = matrixY2 - matrixY1;
        // Store if the difference on the x is larger
        boolean xDifferenceIsLarger = Math.abs(xDifference) > Math.abs(yDifference);

        // What the final value will be multiplied by depending on the direction
        int xModifier = xDifference > 0 ? 1 : -1;
        int yModifier = yDifference > 0 ? 1 : -1;

        // Store the larger and smaller differences in their own variables
        int max = Math.max(Math.abs(xDifference), Math.abs(yDifference));
        int min = Math.min(Math.abs(xDifference), Math.abs(yDifference));
        // Calculate the slope of the line based on the differences (y = ax + b) where a = slope
        float slope = (min == 0 || max == 0) ? 0 : ((float) (min + 1) / (max + 1));

        int smallCount;
        // Iterate through each point between the two positions
        for(int i = 1; i <= max; i++){
            // Calculate the increase in either axis using the slope
            smallCount = (int) Math.floor(i * slope);
            int xIncrease, yIncrease;
            // If the x difference is larger, add the value multiplied by the slope to the y axis, and the normal value to the x
            if(xDifferenceIsLarger){
                xIncrease = i;
                yIncrease = smallCount;
            }
            // If the x difference is smaller or the same, add the value multiplied by the slope to the x axis, and the normal value to the y
            else{
                xIncrease = smallCount;
                yIncrease = i;
            }
            // Add the calculated increases to the coordinates of the original position
            int currentX = matrixX1 + (xIncrease * xModifier);
            int currentY = matrixY1 + (yIncrease * yModifier);
            // Spawn the elements if the position is in the bounds of the screen
            if(isWithinBounds(currentX, currentY)){
                spawnElementByMatrixWithBrush(currentX, currentY, elementType, brushSize, brushType);
            }
        }
    }

}
