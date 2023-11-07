package com.gdx.cellular.cells.liquids;

import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellMatrix;
import com.gdx.cellular.CellularAutomaton;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.ElementType;
import com.gdx.cellular.cells.EmptyCell;
import com.gdx.cellular.cells.solids.Solid;
import com.gdx.cellular.utils.Helpers;

public abstract class Liquid extends Element{
    
    // Declare variables
    int dispersionRate;

    // Constructor
    public Liquid(int x, int y){
        super(x, y); // Run the super constructor
    }

    @Override
    public void step(CellMatrix matrix){
        // Check if it has already stepped this frame. If it has, return
        if(stepped.get(0) == CellularAutomaton.stepped.get(0)) return;
        stepped.flip(0); // Otherwise, update the flag so it has stepped
        
        // Add the gravity vector to the cell's velocity
        velocity.add(CellularAutomaton.gravity);
        if(isFreeFalling) velocity.x *= 0.9f; // If it is free falling, decrease the x velocity to mimic air resistance
        
        // Map the color of the cell based on its y velocity
        colorFactor = Helpers.Map(velocity.y, -129f, -200f, 0, 1);
        if(matrix.get(getMatrixX(), getMatrixY() + 1) instanceof EmptyCell && velocity.x > 0){
            colorFactor = (colorFactor + Helpers.Map(Math.abs(velocity.x), 94.5f, 105f, 0, 1)) / 2f;
        }
        colorFactor = Math.min(1, Math.max(0, colorFactor)); // Clamp the factor between 0 and 1
        color = setColor(color1, color2, colorFactor);

        // Determine the direction of movement in x and y based on velocity
        int yModifier = velocity.y < 0 ? -1 : 1;
        int xModifier = velocity.x < 0 ? -1 : 1;

        // Calculate float delta values for x and y based on velocity and time step
        float velXFloatDelta = (Math.abs(velocity.x) * 1/60);
        float velYFloatDelta = (Math.abs(velocity.y) * 1/60);

        // Integer delta values for x and y, considering thresholds and movement direction
        int velXDelta;
        int velYDelta;

        // Determine the integer delta by applying thresholds to the float delta for each coordinate
        // The thresholds allow for more accurate movement at smaller velocity values
        if(velXFloatDelta < 1){
            xThreshold += velXFloatDelta;
            velXDelta = (int) xThreshold;
            if(Math.abs(velXDelta) > 0){
                xThreshold = 0;
            }
        }
        else{
            xThreshold = 0;
            velXDelta = (int) velXFloatDelta;
        }
        if(velYFloatDelta < 1){
            yThreshold += velYFloatDelta;
            velYDelta = (int) yThreshold;
            if(Math.abs(velYDelta) > 0){
                yThreshold = 0;
            }
        }
        else{
            yThreshold = 0;
            velYDelta = (int) velYFloatDelta;
        }

        // Calculate maximum and minimum velocity components and slope between them
        int velHi = Math.max(Math.abs(velXDelta), Math.abs(velYDelta));
        int velLo = Math.min(Math.abs(velXDelta), Math.abs(velYDelta));
        float slope = (velHi == 0 || velLo == 0) ? 0 : ((float) (velLo + 1) / (float) (velHi + 1));

        // Determine of the x difference is larger
        boolean xIsLarger = Math.abs(velXDelta) > Math.abs(velYDelta);
        int smallCount;
        // Save the last valid location
        Vector3 lastValidLocation = new Vector3(getMatrixX(), getMatrixY(), 0);

        // Iterate through each cell the current cell would have to pass through to get to its destination
        for(int i = 1; i <= velHi; i++){

            // Calculate how much the other coordinate would change by based on the change in the first coordinate and the slope
            smallCount = Math.round(i * slope);

            int xIncrease, yIncrease;
            
            // Determine which direction to increase based on larger velocity component
            if(xIsLarger){
                xIncrease = i;
                yIncrease = smallCount;
            }
            else{
                xIncrease = smallCount;
                yIncrease = i;
            }

            // Calculate modified x and y positions based the increases and the velocity modifiers
            int modifiedX = getMatrixX() + xIncrease * xModifier;
            int modifiedY = getMatrixY() + yIncrease * yModifier;

            // If the modified coordinates are within the bounds of the simulation
            if(matrix.isWithinBounds(modifiedX, modifiedY)){
                
                // Get the element at the destination position
                Element neighbour = matrix.get(modifiedX, modifiedY);
                if(neighbour == this) continue;

                // Run the actOnElementNeighbour function to determine if the element should stop at its current position
                boolean shouldStop = actOnElementNeighbour(neighbour, modifiedX, modifiedY, matrix, i == velHi, i == 1, lastValidLocation, 0);
                if(shouldStop){
                    break; // Break out of the loop if it should stop
                }
                // Set the last valid location at the modified coordiantes if it was not stopped
                lastValidLocation.x = modifiedX;
                lastValidLocation.y = modifiedY;
            }
            else{
                // If its destination is not in bounds, set it to an empty cell to simulate it leaving the screen
                matrix.setElementAtIndex(getMatrixX(), getMatrixY(), ElementType.EMPTYCELL.createElementByMatrix(getMatrixX(), getMatrixY()));
                return;
            }
        }
    }

    // See parent Element class for method signature
    @Override
    protected boolean actOnElementNeighbour(Element neighbour, int modifiedX, int modifiedY, CellMatrix matrix, boolean isFinal, boolean isFirst, Vector3 lastValidLocaton, int depth){
        
        if(neighbour instanceof EmptyCell){
            // Set the adjacent neighbours' inertia state
            setAdjacentNeighbourFreeFalling(matrix, depth, lastValidLocaton);
            if(isFinal){
                // Move to the position if it is the destination
                isFreeFalling = true;
                swapPositions(matrix, neighbour, modifiedX, modifiedY);
            }
            else{
                // If it is just passing through, return false to continue iterating
                return false;
            }
        }
        else if(neighbour instanceof Liquid){
            
            if(isFinal){
                // Move to the position right before the location of the neighbour cell if it is occupied already
                moveToLastValid(matrix, lastValidLocaton);
                return true;
            }

            // If it is free falling, convert a portion of the y velocity to x velocity
            if(isFreeFalling){
                float absY = Math.max(Math.abs(velocity.y) / 31, 105);
                velocity.x = velocity.x < 0 ? -absY : absY;
            }

            // Calculate the normalized velocity of the element
            Vector3 normalizedVelocity = velocity.cpy().nor();

            // Get additional X and Y values based on the normalized velocity
            int additionalX = getAdditional(normalizedVelocity.x);
            int additionalY = getAdditional(normalizedVelocity.y);

            // Calculate the distance the element should move based on dispersionRate and randomness
            int distance = additionalX * (Math.random() > 0.5 ? dispersionRate + 2 : dispersionRate - 1);

            // Get the diagonal neighbor of the element
            Element diagonalNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY() + additionalY);
            
            if (isFirst) {
                // If it's the first step, calculate the average vertical velocity or gravity
                velocity.y = getAverageVelOrGravity(velocity.y, neighbour.velocity.y);
            } 
            else {
                // If it's not the first step, set the vertical velocity to simulate free fall
                velocity.y = -124f;
            }
            
            // Update the neighboring element's vertical velocity to match the current element's vertical velocity
            neighbour.velocity.y = velocity.y;
            
            // Apply friction to the horizontal velocity to simulate slowing down
            velocity.x *= frictionFactor;

            
            if(diagonalNeighbour != null){
                // Check if the element can move diagonally without obstruction 
                boolean stoppedDiagonally = iterateToAdditional(matrix, getMatrixX() + additionalX, getMatrixY() + additionalY, distance, lastValidLocaton);
                if(!stoppedDiagonally){
                    isFreeFalling = true;
                    return true;
                }
            }

            // Get the adjacent neighbour of the element
            Element adjacentNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY());
            
            if(adjacentNeighbour != null){
                // Check if the element can move adjacently without obstruction
                boolean stoppedAdjacently = iterateToAdditional(matrix, getMatrixX() + additionalX, getMatrixY(), distance, lastValidLocaton);
                if(stoppedAdjacently) velocity.x *= -1; // If it was stopped, set the x velocity to the opposite direction
                if(!stoppedAdjacently){
                    isFreeFalling = false;
                    return true;
                }
            }
            
            isFreeFalling = false;

            // Move the element to the last valid location if it cannot go anywhere else
            moveToLastValid(matrix, lastValidLocaton);
            return true;
        }

        
        else if(neighbour instanceof Solid){

            // If the solid neighbour is at the final destination of the current cell, move the cell to the position before it
            if(isFinal){
                moveToLastValid(matrix, lastValidLocaton);
                return true;
            }

            // If the cell is free falling and hits the solid, convert a portion of the y velocity to x velocity
            if(isFreeFalling){
                float absY = Math.max(Math.abs(velocity.y) / 31, 105);
                velocity.x = velocity.x < 0 ? -absY : absY;
            }
            
            // Calculate the normalized velocity of the element
            Vector3 normalizedVelocity = velocity.cpy().nor();

            // Get additional X and Y values based on the normalized velocity
            int additionalX = getAdditional(normalizedVelocity.x);
            int additionalY = getAdditional(normalizedVelocity.y);

            // Calculate the distance the element should move based on dispersionRate and randomness
            int distance = additionalX * (Math.random() > 0.5 ? dispersionRate + 2 : dispersionRate - 1);

            // Get the diagonal neighbor of the element
            Element diagonalNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY() + additionalY);

            if(isFirst){
                // If it's the first step, calculate the average vertical velocity or gravity
                velocity.y = getAverageVelOrGravity(velocity.y, neighbour.velocity.y);
            }
            else{
                // If it's not the first step, set the vertical velocity to simulate decceleration
                velocity.y = -124f;
            }

            // Update the neighboring element's vertical velocity to match the current element's vertical velocity
            neighbour.velocity.y = velocity.y;

            // Apply friction to the horizontal velocity to simulate slowing down
            velocity.x *= frictionFactor;

            if(diagonalNeighbour != null){
                // Check if the element can move diagonally without obstruction
                boolean stoppedDiagonally = iterateToAdditional(matrix, getMatrixX() + additionalX, getMatrixY() + additionalY, distance, lastValidLocaton);
                if(!stoppedDiagonally){
                    isFreeFalling = true;
                    return true;
                }
            }

            // Get the adjacent neighbour of the element
            Element adjacentNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY());

            if(adjacentNeighbour != null){
                // Check if the element can move adjacently without obstruction
                boolean stoppedAdjacently = iterateToAdditional(matrix, getMatrixX() + additionalX, getMatrixY(), distance, lastValidLocaton);
                if(stoppedAdjacently) velocity.x *= -1; // If it is stopped, set the x velocity in the opposite direction
                if(!stoppedAdjacently){
                    isFreeFalling = false;
                    return true;
                }
            }

            isFreeFalling = false;

            // Move the element to the last valid location if it cannot go anywhere else
            moveToLastValid(matrix, lastValidLocaton);
            return true;
        }

        return false; // Return false if it does not return anywhere else
    }

    /**
     * Iterates through the matrix to check if the element can move to the additional position without obstruction
     * @param matrix The cell matrix
     * @param startingX The starting x coordinate
     * @param startingY The starting y coordinate
     * @param distance The distance to move
     * @param lastValid The last valid location of the element
     * @return True if it can move, false if it encounters an obstacle
     */
    private boolean iterateToAdditional(CellMatrix matrix, int startingX, int startingY, int distance, Vector3 lastValid){
        // Store the direction the distance is in based on its sign
        int distanceModifier = distance > 0 ? 1 : -1;
        // Store the last valid location
        Vector3 lastValidLocation = lastValid;

        // Iterate through the matrix to check for obstacles along the movement path
        for(int i = 0; i <= Math.abs(distance); i++){

            // Calculate the modified X-coordinate based on the current iteration and distanceModifier
            int modifiedX = startingX + i * distanceModifier;

            // Get the neighbor at the modified position
            Element neighbour = matrix.get(modifiedX, startingY);
            if(neighbour == null){
                // If the neighbor is empty, the element can move freely to this position
                return true;
            }

            // Determine if the current iteration is the first or last step of the movement
            boolean isFirst = i == 0;
            boolean isFinal = i == Math.abs(distance);

            if(neighbour instanceof EmptyCell){
                // If the neighbor is an EmptyCell, check if it's the final destination
                if(isFinal){
                    // Swap positions with the EmptyCell to complete the movement
                    swapPositions(matrix, neighbour, modifiedX, startingY);
                    return false;
                }
                // Update the last valid location to the current position if it's not the final step
                lastValidLocation.x = modifiedX;
                lastValidLocation.y = startingY;
            }
            else if(neighbour instanceof Liquid){
                // Handle interactions with Liquid neighbors (currently empty, no specific action required)
            }
            else if(neighbour instanceof Solid){
                // If the neighbor is a Solid, check if it's the first step or the final destination
                if(isFirst){
                    // The element is blocked by a Solid neighbor at the starting position
                    return true;
                }

                // Move to the last valid location if it encounters a Solid neighbour
                moveToLastValid(matrix, lastValidLocation);
                return false; 
            }
        }
        return true;
    }

    /**
     * This method takes a float value as input and calculates the additional integer value based on certain conditions.
     * @param val val The input float value
     * @return An integer value calculated based on the input float value
     */
    private int getAdditional(float val) {
        // Check if the input value is less than -0.1
        if (val < -.1f) {
            // If true, round down the input float value to the nearest integer and return the result
            return (int) Math.floor(val);
        } else if (val > .1f) {
            // If the input value is greater than 0.1, round up the input float value to the nearest integer and return the result
            return (int) Math.ceil(val);
        } else {
            // If the input value is between -0.1 and 0.1 (inclusive), return 0
            return 0;
        }
    }

    /**
     * This method calculates the average of two float values (vel and otherVel), subject to specific conditions,
     * and returns the result as a float value.
     * @param vel The first input float value
     * @param otherVel The second input float value
     * @return The calculated average of vel and otherVel based on the conditions
     */
    private float getAverageVelOrGravity(float vel, float otherVel) {
        // Check if otherVel is greater than -125f
        if (otherVel > -125f) {
            // If true, return a predefined value -124f
            return -124f;
        }
        // Calculate the average of vel and otherVel
        float avg = (vel + otherVel) / 2;
        // Check if the average is greater than 0
        if (avg > 0) {
            // If true, return the calculated average
            return avg;
        } else {
            // If the average is not greater than 0, return the minimum of the average and -124f
            return Math.min (avg, -124f);
        }
    }

    /**
     * This method sets adjacent neighbors as free-falling elements based on certain conditions.
     * @param matrix The CellMatrix containing elements
     * @param depth The depth of recursion to control the number of recursive iterations
     * @param lastValidLocation The Vector3 representing the last valid location in the matrix
     */
    private void setAdjacentNeighbourFreeFalling(CellMatrix matrix, int depth, Vector3 lastValidLocation){
        // Base case: If depth is greater than 0, return and stop the recursion
        if(depth > 0) return;

        // Get the element to the right of the last valid location
        Element neighbour1 = matrix.get(lastValidLocation.x + 1, lastValidLocation.y);
        // Check if the right neighbor is a solid element
        if(neighbour1 instanceof Solid){
            // If true, set inertia for the right neighbor
            setInertia(neighbour1);
        }

        // Get the element to the left of the last valid location
        Element neighbour2 = matrix.get(lastValidLocation.x - 1, lastValidLocation.y);
        // Check if the left neighbor is a solid element
        if(neighbour2 instanceof Solid){
            // If true, set inertia for the left neighbor
            setInertia(neighbour2);
        }
    }

    /**
     * This method sets inertia for the given element based on a random probability and inertia resistance.
     * @param element The Element for which inertia needs to be set
     */
    private void setInertia(Element element){
        // Check if a random number between 0 and 1 is greater than the element's inertia resistance
        element.isFreeFalling = Math.random() > element.inertiaResistance;
        // If the random number is greater than inertia resistance, set the element as free-falling
    }
}