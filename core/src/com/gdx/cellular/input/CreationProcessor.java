package com.gdx.cellular.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.gdx.cellular.CellMatrix;
import com.gdx.cellular.cells.ElementType;

public class CreationProcessor implements InputProcessor{

    // Declare field variables
    private final InputHandler inputHandler;
    private final CellMatrix matrix;

    // Constructor
    public CreationProcessor(InputHandler inputHandler, CellMatrix matrix){

        // Set them to the values passed in the constructor
        this.inputHandler = inputHandler;
        this.matrix = matrix;

    }

    // Override the keydown method which is run every time a key is pressed
    @Override
    public boolean keyDown(int keycode) {
        // keycode switch
        switch(keycode){
            case Keys.ENTER:
                inputHandler.isPaused = !inputHandler.isPaused; // If the key is enter, change the paused state
                break;
            case Keys.RIGHT:
                matrix.stepAll(); // If the key is right arrow, step the simulation once
                break;
            // Set the selected element based on which number / letter is selected
            /*
             * 1 = Sand
             * 2 = Water
             * 3 = Stone
             * 4 = Gravel
             * X = Delete
             */
            case Keys.NUM_1:
                inputHandler.setSelectedElement(ElementType.SAND);
                break;
            case Keys.NUM_2:
                inputHandler.setSelectedElement(ElementType.WATER);
                break;
            case Keys.NUM_3:
                inputHandler.setSelectedElement(ElementType.STONE);
                break;
            case Keys.NUM_4:
                inputHandler.setSelectedElement(ElementType.GRAVEL);
                break;
            case Keys.X:
                inputHandler.setSelectedElement(ElementType.EMPTYCELL);
                break;
        }
        return false;
    }

    /*
     * Unused methods just have a blank return
     */
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT){
            // Spawn elements when left click is pressed
            inputHandler.spawnElementByInput(matrix);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // When the button is lifted, set the touched last frame flag to false
        inputHandler.setTouchedLastFrame(false);
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        inputHandler.spawnElementByInput(matrix); // Also spawn elements when the touch is dragged
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        inputHandler.setBrushSize(-(int) amountY); // Change the brush size depending on the scroll direction
        return false;
    }
    
}
