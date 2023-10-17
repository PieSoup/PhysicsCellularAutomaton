package com.gdx.cellular;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.ElementType;

public class CellMatrix {
    // Matrix of cells is stored in a 2D array
    Array<Array<Element>> matrix;

    // Initialize variables
    private int xArrSize;
    private int yArrSize;
    private int cellSizeModifier;

    boolean touchedLastFrame = false;
    // Constructor
    public CellMatrix(int width, int height, int cellSizeModifier){
        // Set variables
        this.cellSizeModifier = cellSizeModifier;
        this.xArrSize = toMatrix(width);
        this.yArrSize = toMatrix(height);
        
        matrix = generateMatrix(); // Initialize the matrix with empty cells

    }
    // Generate matrix method -> 2D array
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

    // Run both the step and draw functons
    public void stepAndDrawAll(ShapeRenderer sr){
        stepAll();
        drawAll(sr);
    }
    // Method to step all of the cells
    public void stepAll(){
        scanNeighbours(); // First scan the neighbours of all the cells before stepping them
        // Loop through every cell using nested loops and run their step() method
        for(int y = 0; y < yArrSize; y++){
            Array<Element> row = getRow(y);
            for(int x = 0; x < xArrSize; x++){
                Element element = row.get(x);
                    element.step(this);
            }
        }
    }
    // Method to draw everything in the scene
    public void drawAll(ShapeRenderer sr){
        // Initialize the shape renderer
        sr.begin();
        sr.set(ShapeRenderer.ShapeType.Filled); // Set the type to filled
        // Loop through all of the elements
        for(int y = 0; y < yArrSize; y++){
            Array<Element> row = getRow(y);
            for(int x = 0; x < xArrSize; x++){
                Element element = row.get(x);
                // If the element does not have a set color, set it to black. Otherwise set it to the elements color
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
        drawGrid(sr); // Draw the grid
    }
    // Method to draw the grid
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
    // Method to scan all of the neighbours of the cells
    private void scanNeighbours(){
        // Loop through all of the cells
        for(int y = 0; y < yArrSize; y++){
            Array<Element> row = getRow(y);
            for(int x = 0; x < xArrSize; x++){
                Element element = row.get(x);
                if(element != null){
                    element.getNeighbourhood(this); // Store all of the neighbours into the cells own array for it to use later
                }
            }
        }
    }

    // Method to spawn elements depending on the pixel position of the mouse converted to the cell position
    public void spawnElementByPixelToMatrix(int x, int y, boolean dragged){
        Vector2 touchPos = new Vector2(toMatrix(x), toMatrix(y)); // Convert the pixel touch pos to matrix pos
        boolean inBoundsX = touchPos.x >= 0 && touchPos.x < xArrSize; // Check if it is in bounds on the X
        boolean inBoundsY = touchPos.y >= 0 && touchPos.y < yArrSize; // Check if it is in bounds on the Y
        // If the touch pos is not in bounds, set the spawn position to a spot in bounds depending on the touch pos
        if(!inBoundsX){
            touchPos = new Vector2(touchPos.x - (touchPos.x - xArrSize) - 1, touchPos.y);
        }
        else if(!inBoundsY){
            touchPos = new Vector2(touchPos.x, touchPos.y - (touchPos.y - yArrSize) - 1);
        }
        // If they are dragging the mouse out of bounds, ust return so they can't draw anything
        if((!inBoundsX || !inBoundsY) && dragged){
            return;
        }
        // TODO: implement a way to connect touch points because they are usually spaced apart, especially on lower frame rates
        // Use y = ax + b
        if(touchedLastFrame){

        } else{
            // Set the elemend at the touch pos to a new living cell
            setElementAtIndex(touchPos.x, touchPos.y, ElementType.STONE.createElementByMatrix((int)touchPos.x, (int)touchPos.y));
        }
    }

    // Methods to set the element in the matrix
    public void setElementAtIndex(float x, float y, Element newElement){
        setElementAtIndex((int) x, (int) y, newElement);
    }
    public boolean setElementAtIndex(int x, int y, Element newElement){
        matrix.get(y).set(x, newElement); // Get the coordinates and the element, and spawn the new element there
        return true;
    }

    // Get the element at a certain position in the matrix
    public Element getElementByIndex(int x, int y){
        return matrix.get(y).get(x);
    }

    // Get a certain row of the matrix
    private Array<Element> getRow(int index){
        return matrix.get(index);
    }

    // Calculate how big the rectangle draw size should be
    private int rectDrawSize(int index){
        return index * cellSizeModifier + cellSizeModifier;
    }

    // Convert pixel coordinates to matrix coordinates
    public int toMatrix(int val){
        return val / cellSizeModifier;
    }

    // Convert matrix coordinates to pixel coordinates
    public int toPixel(int val){
        return val * cellSizeModifier;
    }

    // Get the size of the array on the X
    public int getArraySizeX(){
        return xArrSize;
    }
    // Get the size of the array on the Y
    public int getArraySizeY(){
        return yArrSize;
    }
}
