package com.gdx.cellular.cells;

import com.gdx.cellular.cells.solids.ImmovableSolids.Stone;
import com.gdx.cellular.cells.solids.MoveableSolids.Gravel;
import com.gdx.cellular.cells.solids.MoveableSolids.Sand;
import com.gdx.cellular.cells.liquids.Water;


// Define an enum called ElementType
public enum ElementType {
    // Define a constant named EMPTYCELL with associated class and class type
    EMPTYCELL(EmptyCell.class, ClassType.EMPTYCELL) {
        // Implement abstract method to create an element of type EmptyCell
        @Override
        public Element createElementByMatrix(int x, int y) {
            return EmptyCell.getInstance();
        }
    },
    // Define a constant named STONE with associated class and class type
    SAND(Sand.class, ClassType.MOVEABLESOLID) {
        // Implement abstract method to create an element of type Stone
        @Override
        public Element createElementByMatrix(int x, int y) {
            return new Sand(x, y);
        }
    },
    STONE(Stone.class, ClassType.IMMOVEABLESOLID){
        @Override
        public Element createElementByMatrix(int x, int y){
            return new Stone(x, y);
        }
    },
    GRAVEL(Gravel.class, ClassType.MOVEABLESOLID){
        @Override
        public Element createElementByMatrix(int x, int y){
            return new Gravel(x, y);
        }
    },
    WATER(Water.class, ClassType.LIQUID){
        @Override
        public Element createElementByMatrix(int x, int y){
            return new Water(x, y);
        }
    };

    // Declare instance variables for class and class type
    public final Class<? extends Element> class_;
    public final ClassType classType;

    // Private constructor to initialize class and class type for each constant
    private ElementType(Class<? extends Element> class_, ClassType classType) {
        this.class_ = class_;
        this.classType = classType;
    }

    /**
     * Abstract method to create an element based on position in a matrix
     * @param x The x position
     * @param y The y position
     * @return The element that is created
     */
    public abstract Element createElementByMatrix(int x, int y);

    // Define an enum for class types
    public enum ClassType {
        MOVEABLESOLID,
        IMMOVEABLESOLID,
        LIQUID,
        EMPTYCELL;
    }
}
