package com.gdx.cellular;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		// Make a new lightweight java game library application
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60); // Set the FPS
		config.setTitle("Physics Sim"); // Set the title

		// Set widowed mode with proper size
		config.setWindowedMode(CellularAutomaton.getScreenWidth(), CellularAutomaton.getScreenHeight());
		new Lwjgl3Application(new CellularAutomaton(), config);
	}
}
