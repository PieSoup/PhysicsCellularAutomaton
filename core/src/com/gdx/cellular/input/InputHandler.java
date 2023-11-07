package com.gdx.cellular.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gdx.cellular.CellMatrix;
import com.gdx.cellular.cells.ElementType;

public class InputHandler{

    // Declare variables
    private final OrthographicCamera camera;
    private final CellMatrix matrix;
    public CreationProcessor creationProcessor;
    public Stage stage;

    private boolean touchedLastFrame = false;
    private Vector3 lastTouchPos = new Vector3();
    
    private ElementType selectedElementType = ElementType.WATER;
    private BRUSHTYPE brushType = BRUSHTYPE.CIRCLE;
    private int brushSize = 20;
    private final int maxBrushSize = 50;
    private final int minBrushSize = 3;
    
    boolean isPaused = true;

    // Constructor
    public InputHandler(OrthographicCamera camera, CellMatrix matrix, Viewport viewport){
        // Initialize variables
        this.camera = camera;
        this.matrix = matrix;
        this.stage = new Stage(viewport);
        creationProcessor = new CreationProcessor(this, matrix);
        Gdx.input.setInputProcessor(creationProcessor);
    }

    /**
     * This method is run every frame and is used to detect when the button is down but not being dragged
     */
    public void updateWhenNotMoving(){
        if(touchedLastFrame){
            spawnElementByInput(matrix);
        }
    }

    /**
     * This method handles all of the cases for when the user is spawning in elements using inputs
     * @param matrix The cell matrix
     */
    public void spawnElementByInput(CellMatrix matrix){
        // Get the touch point and convert it to world space coordinates
        Vector3 touchPos = new Vector3();
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(touchPos);

        if(touchedLastFrame){
            // If there was a touch last frame, spawn elements between two points to avoid spaces
            matrix.spawnElementBetweenTwoPoints(lastTouchPos, touchPos, selectedElementType, brushSize, brushType);

        }
        else{
            // If it is the first touch (no touch last frame) then just spawn normally
            matrix.spawnElementByPixelWithBrush((int) touchPos.x, (int) touchPos.y, selectedElementType, brushSize, brushType);
        }
        // Set the last touch pos to the current touch pos
        lastTouchPos = touchPos;
        touchedLastFrame = true; // Set the touched last frame flag to true
    }
    
    /**
     * Method to set the touched last frame flag externally
     * @param touchedLastFrame The state to set the flag to
     */
    public void setTouchedLastFrame(boolean touchedLastFrame) {
        this.touchedLastFrame = touchedLastFrame; // Set the flag
    }

    /**
     * Get the state of the touched last frame flag
     * @return The state of the touched last frame flag
     */
    public boolean getTouchedLastFrame(){
        return touchedLastFrame;
    }

    /**
     * Method to set the selected element type externally
     * @param elementType The element type to set it to
     */
    public void setSelectedElement(ElementType elementType){
        selectedElementType = elementType; // Set the element type
    }

    /**
     * Method to set the brush size externally
     * @param brushDelta The amount to change the brush size by
     */
    public void setBrushSize(int brushDelta) {
        // Clamp the size to a max and min, if the modified size is greater than or less than these values, set it to them
        if((brushSize += brushDelta) > maxBrushSize){
            brushSize = maxBrushSize;
        }
        else if((brushSize += brushDelta) < minBrushSize){
            brushSize = minBrushSize;
        }
        brushSize += brushDelta; // Add the delta to the size
    }

    /**
     * Method to get the paused state of the sim
     * @return The pause state
     */
    public boolean getIsPaused(){
        return isPaused;
    }
    
    // Enum for all the possible brush types
    public enum BRUSHTYPE{
        CIRCLE,
        SQUARE,
        RECTANGLE;
    }
}
