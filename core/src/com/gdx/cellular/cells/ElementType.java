package com.gdx.cellular.cells;

import com.gdx.cellular.cells.solids.Stone;

// Define an enum called ElementType
public enum ElementType {
    // Define a constant named EMPTYCELL with associated class and class type
    EMPTYCELL(EmptyCell.class, ClassType.EMPTYCELL) {
        // Implement abstract method to create an element of type EmptyCell
        @Override
        public Element createElementByMatrix(int x, int y) {
            return new EmptyCell(x, y);
        }
    },
    // Define a constant named STONE with associated class and class type
    STONE(Stone.class, ClassType.SOLID) {
        // Implement abstract method to create an element of type Stone
        @Override
        public Element createElementByMatrix(int x, int y) {
            return new Stone(x, y);
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

    // Declare an abstract method to create an element based on position in a matrix
    public abstract Element createElementByMatrix(int x, int y);

    // Define an enum for class types (SOLID and EMPTYCELL)
    public enum ClassType {
        SOLID,
        EMPTYCELL;
    }
}
