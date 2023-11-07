package com.gdx.cellular.cells.solids.MoveableSolids;

import com.badlogic.gdx.math.Vector3;
import com.gdx.cellular.CellMatrix;
import com.gdx.cellular.CellularAutomaton;
import com.gdx.cellular.cells.Element;
import com.gdx.cellular.cells.ElementType;
import com.gdx.cellular.cells.EmptyCell;
import com.gdx.cellular.cells.liquids.Liquid;
import com.gdx.cellular.cells.solids.Solid;

public abstract class MoveableSolid extends Solid{
    
    // Constructor
    public MoveableSolid(int x, int y){
        super(x, y); // Use the super constructor
    }

    @Override
    public void step(CellMatrix matrix){

        // Check if it has already stepped this frame. If it has, return
        if(stepped.get(0) == CellularAutomaton.stepped.get(0)) return;
        stepped.flip(0); // Otherwise, update the flag so it has stepped

        // Movement calculations are the same as the liquid class (everything up until the actOnNeighbour method)
        // Read documentation there if you are wondering how this works
        velocity.add(CellularAutomaton.gravity);
        if (isFreeFalling) velocity.x *= .9; 

        int yModifier = velocity.y < 0 ? -1 : 1;
        int xModifier = velocity.x < 0 ? -1 : 1;
        float velXFloatDelta = (Math.abs(velocity.x) * 1/60);
        float velYFloatDelta = (Math.abs(velocity.y) * 1/60);
        int velXDelta;
        int velYDelta;
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

        int velHi = Math.max(Math.abs(velXDelta), Math.abs(velYDelta));
        int velLo = Math.min(Math.abs(velXDelta), Math.abs(velYDelta));
        float slope = (velHi == 0 || velLo == 0) ? 0 : ((float) (velLo + 1) / (float) (velHi + 1));
        boolean xIsLarger = Math.abs(velXDelta) > Math.abs(velYDelta);
        int smallCount;
        Vector3 lastValidLocation = new Vector3(getMatrixX(), getMatrixY(), 0);
        for(int i = 1; i <= velHi; i++){

            smallCount = Math.round(i * slope);

            int xIncrease, yIncrease;

            if(xIsLarger){
                xIncrease = i;
                yIncrease = smallCount;
            }
            else{
                xIncrease = smallCount;
                yIncrease = i;
            }

            int modifiedX = getMatrixX() + xIncrease * xModifier;
            int modifiedY = getMatrixY() + yIncrease * yModifier;
            if(matrix.isWithinBounds(modifiedX, modifiedY)){
                Element neighbour = matrix.get(modifiedX, modifiedY);
                if(neighbour == this) continue;
                boolean shouldStop = actOnElementNeighbour(neighbour, modifiedX, modifiedY, matrix, i == velHi, i == 1, lastValidLocation, 0);
                if(shouldStop){
                    break;
                }
                lastValidLocation.x = modifiedX;
                lastValidLocation.y = modifiedY;
            }
            else{
                matrix.setElementAtIndex(getMatrixX(), getMatrixY(), ElementType.EMPTYCELL.createElementByMatrix(getMatrixX(), getMatrixY()));
                return;
            }
        }
    }

    // See parent Element class for method signature
    @Override
    protected boolean actOnElementNeighbour(Element neighbour, int modifiedX, int modifiedY, CellMatrix matrix, boolean isFinal, boolean isFirst, Vector3 lastValidLocaton, int depth){
        // Check the type of element that the neighbour is
        if(neighbour instanceof EmptyCell){
            // If it is an empty cell, set itself and surrounding neighbours to free falling
            setAdjacentNeighbourFreeFalling(matrix, depth, lastValidLocaton);
            if(isFinal){
                isFreeFalling = true;
                // Swap positions with the target if it is the destination
                swapPositions(matrix, neighbour, modifiedX, modifiedY);
            }
            else{
                // If the position is not the destination, continue moving
                return false;
            }
        }

        else if(neighbour instanceof Liquid){
            
            // If the depth is greater than 0, set neighbours to free falling and swap positions with the liquid
            if(depth > 0){
                setAdjacentNeighbourFreeFalling(matrix, depth, lastValidLocaton);
                isFreeFalling = true;
                swapPositions(matrix, neighbour, modifiedX, modifiedY);
            }
            else{
                // If the depth is 0, move to the spot before the liquid to simulate the solid slowing down when hitting the solid
                isFreeFalling = true;
                moveToLastValidAndSwap(matrix, neighbour, modifiedX, modifiedY, lastValidLocaton);
                return true;
            }
        }
        else if(neighbour instanceof Solid){

            // If the depth is greater than 0, return true as it should stop
            if(depth > 0) return true;
            if(isFinal){
                // If the target destination is a solid, move to the last valid location
                moveToLastValid(matrix, lastValidLocaton);
                return true;
            }
            if(isFreeFalling){
                // If the solid is free falling (to simualte inertia) then convert a portion of the y velocity to x velocity
                float absY = Math.max(Math.abs(velocity.y) / 31, 105);
                velocity.x = velocity.x < 0 ? -absY : absY;
            }
            
            // Calculate the normalized velocity of the element
            Vector3 normalizedVelocity = velocity.cpy().nor();

            // Get additional X and Y values based on the normalized velocity
            int additionalX = getAdditional(normalizedVelocity.x);
            int additionalY = getAdditional(normalizedVelocity.y);

            // Get the diagonal neighbor of the element
            Element diagonalNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY() + additionalY);
            if(isFirst){
                // If it's the first step, calculate the average vertical velocity or gravity to simulate it hitting it if it has motion already
                velocity.y = getAverageVelOrGravity(velocity.y, neighbour.velocity.y);
            }
            else{
                // If it's not the first step, set the vertical velocity to simulate decceleration
                velocity.y = -124;
            }

            // Update the neighboring element's vertical velocity to match the current element's vertical velocity
            neighbour.velocity.y = velocity.y;

            // Apply friction to the horizontal velocity to simulate slowing down
            velocity.x *= frictionFactor;

            if(diagonalNeighbour != null){
                // Check if the element can move diagonally without obstruction
                boolean stoppedDiagonally = actOnElementNeighbour(diagonalNeighbour, getMatrixX() + additionalX, getMatrixY() + additionalY, matrix, true, false, lastValidLocaton, depth + 1);
                if(!stoppedDiagonally){
                    isFreeFalling = true;
                    return true;
                }
            }

            // Get the adjacent neighbour of the element
            Element adjacentNeighbour = matrix.get(getMatrixX() + additionalX, getMatrixY());

            if(adjacentNeighbour != null && adjacentNeighbour != diagonalNeighbour){
                // Check if the element can move adjacently without obstruction
                boolean stoppedAdjacently = actOnElementNeighbour(adjacentNeighbour, getMatrixX() + additionalX, getMatrixY(), matrix, true, false, lastValidLocaton, depth + 1);
                if(stoppedAdjacently) velocity.x *= -1;
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
        return false;
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
