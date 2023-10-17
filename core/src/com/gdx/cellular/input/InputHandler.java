package com.gdx.cellular.input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.gdx.cellular.CellMatrix;

public class InputHandler implements InputProcessor{

    // Declare variables
    private final OrthographicCamera camera;
    private final CellMatrix matrix;

    public Stage stage;

    Vector3 touchPoint = new Vector3();
    boolean isPaused = true;

    int button;

    // Constructor
    public InputHandler(OrthographicCamera camera, CellMatrix matrix, Viewport viewport){
        // Initialize variables
        this.camera = camera;
        this.matrix = matrix;
        this.stage = new Stage(viewport);
    }

    // Returns the screen X and Y, pointer, and button when the screen is "touched"
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.button = button;
        if(button != Input.Buttons.LEFT || pointer > 0) return false; // If it is not a left click, return false
        touchPoint = camera.unproject(touchPoint.set(screenX, screenY, 0)); // Get the touch point
        matrix.spawnElementByPixelToMatrix((int)touchPoint.x, (int)touchPoint.y, false); // Spawn a new element at the touch point
        return true;
    }

    // Method runs when a touch is dragged across the screen
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(button != Input.Buttons.LEFT || pointer > 0) return false; // If it is not left click, return false
        touchPoint = camera.unproject(touchPoint.set(screenX, screenY, 0)); // Calculate touch point
        matrix.spawnElementByPixelToMatrix((int)touchPoint.x, (int)touchPoint.y, true); // Spawn a new element there
        return true;
    }

    // Method runs whenever a key is pressed
    @Override
    public boolean keyDown(int keycode) {
        // keycode switch
        switch(keycode){
            case Keys.ENTER:
                isPaused = !isPaused; // If the key is enter, change the paused state
            case Keys.RIGHT:
                matrix.stepAll(); // If the key is right arrow, step the simulation once
        }
        return true;
    }

    // Get the touch point
    public Vector3 getTouchPoint() {
        return touchPoint;
    }

    // Get whether the simulation is paused or not
    public boolean getIsPaused(){
        return isPaused;
    }

    // Following methods are unused but need to be overrided because of the interface
    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
    
}
