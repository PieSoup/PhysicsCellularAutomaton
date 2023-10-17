package com.gdx.cellular;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.gdx.cellular.input.InputHandler;
import com.gdx.cellular.cells.ElementType;

public class CellularAutomaton extends ApplicationAdapter {
	
	// Setup screen
	private static int screenWidth = 1280; // 720p
	private static int screenHeight = 720;
	private static int cellSizeModifier = 6;

	// Setup scene
	Viewport viewport;
	OrthographicCamera camera;
    ShapeRenderer shape;

	// Setup other classes as objects
	public CellMatrix cellMatrix = new CellMatrix(screenWidth, screenHeight, cellSizeModifier);
	private InputHandler inputHandler;

	// create() runs once upon application startup
	@Override
	public void create () {
		// Initialize various objects
		shape = new ShapeRenderer();
		shape.setAutoShapeType(true);
		camera = new OrthographicCamera();
		viewport = new FitViewport(screenWidth, screenHeight, camera);

		inputHandler = new InputHandler(camera, cellMatrix, viewport);
		Gdx.input.setInputProcessor(inputHandler); // Set the input processor for the scene
		
		// Generate a bunch of random noise as a starting position for the scene
		for(int i = 0; i < 10000; i++){
			int x = (int)(Math.random() * cellMatrix.getArraySizeX());
			int y = (int)(Math.random() * cellMatrix.getArraySizeY());

			cellMatrix.setElementAtIndex(x, y, ElementType.STONE.createElementByMatrix(x, y));
		}
		
		
	}

	// render() is ran every frame upon activation
	@Override
	public void render() {
		// Good practice to clear the OpenGL buffers before drawing anything
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Check if the game is paused
		boolean isPaused = inputHandler.getIsPaused();
		if(isPaused){
			cellMatrix.drawAll(shape); // If it is paused, only draw, not step
		}
		else{
			cellMatrix.stepAndDrawAll(shape); // If it is unpaused, draw and step everything
		}
	}

	// resize() is ran every time the window is resized
	@Override
	public void resize(int width, int height){
		viewport.update(width, height); // Update the viewport
	}
	
	// return the screen height
	public static int getScreenHeight(){
		return screenHeight;
	}
	
	// return the screen width
	public static int getScreenWidth(){
		return screenWidth;
	}

	// return the cell size modifier
	public static int getCellSizeModifier(){
		return cellSizeModifier;
	}
	// Manage memory by disposing resources
	@Override
	public void dispose () {
		shape.dispose();	
	}
}
