package com.gdx.cellular;

import java.util.BitSet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gdx.cellular.input.InputHandler;


/** 
 * Uses a celluar automaton to recreate simple physics for various elements.
 * 
 * @author Jack Wyand
 * @version 1.0
 * @since 2023-11-2
*/

public class CellularAutomaton extends ApplicationAdapter {
	
	// Setup screen
	private static int screenWidth = 1000;
	private static int screenHeight = 1000;
	private static int cellSizeModifier = 6;
	public static Vector3 gravity = new Vector3(0f, -5f, 0f);
	public static BitSet stepped = new BitSet(1);

	// Setup scene
	Viewport viewport;
	OrthographicCamera camera;
    ShapeRenderer shape;

	// Setup other classes as objects
	public CellMatrix cellMatrix;
	private static InputHandler inputHandler;

	/** 
	 * This method is ran once upon acitivation. 
	 * It is used for initializing various objects
	*/
	@Override
	public void create () {
		// Initialize various objects
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
		camera = new OrthographicCamera();
		viewport = new FitViewport(screenWidth, screenHeight, camera);
		
		stepped.set(0, true);
		cellMatrix = new CellMatrix(screenWidth, screenHeight, cellSizeModifier);

		inputHandler = new InputHandler(camera, cellMatrix, viewport);
		
		
	}

	/**
	 * This method is run once per frame after activation. 
	 * It is used to update the simulation
	 */
	@Override
	public void render() {
		// Good practice to clear the OpenGL buffers before drawing anything
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stepped.flip(0);
		cellMatrix.reshuffleXIndexes(); // Use randomized x indexes when looping to ensure that there more randomness in the behavior
		// Check if the game is paused
		boolean isPaused = inputHandler.getIsPaused();
		if(isPaused){
			cellMatrix.drawAll(shape); // If it is paused, only draw, not step
		}
		else{
			cellMatrix.stepAndDrawAll(shape); // If it is unpaused, draw and step everything
		}
		inputHandler.updateWhenNotMoving(); // Update the inputs
	}

	/**
	 * This method is run whenever the window is resized. 
	 * It is used to update the viewport
	 */
	@Override
	public void resize(int width, int height){
		viewport.update(width, height); // Update the viewport
	}
	
	/**
	 * This method returns the current screen height
	 * @return int
	 */
	public static int getScreenHeight(){
		return screenHeight;
	}
	
	/**
	 * This method returns the current screen width
	 * @return int
	 */
	public static int getScreenWidth(){
		return screenWidth;
	}

	/**
	 * This method returns the current cell size modifier
	 * @return int
	 */
	public static int getCellSizeModifier(){
		return cellSizeModifier;
	}

	/**
	 * This method returns the current input handler
	 * @return The Input Handler
	 */
	public static InputHandler getInputHandler(){
		return inputHandler;
	}

	// Manage memory by disposing resources
	@Override
	public void dispose () {
		shape.dispose();	
	}
}
