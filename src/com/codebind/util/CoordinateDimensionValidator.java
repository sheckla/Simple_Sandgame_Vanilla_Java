package com.codebind.util;

// Validates the index bounds
public class CoordinateDimensionValidator {
    private int rows;
    private int columns;
    private int xMax;
    private int yMax;

    public CoordinateDimensionValidator(int rows, int columns, int x, int y) {
        this.rows = rows;
        this.columns = columns;
        this.xMax = x;
        this.yMax = y;
    }

    public boolean insideX(int x) {
        if (x >= 0 && x < rows) {
            return true;
        }
        return false;
    }

    public boolean insideY(int y) {
        if (y >= 0 && y < columns) {
            return true;
        }
        return false;
    }

    public boolean insideMatrix(int x, int y) {
        return (insideX(x) && insideY(y));
    }

    public boolean insideCanvasDimension(int x, int y) {
        if (x >= 0 && x < xMax && y >= 0 && y < yMax) return true;
        return false;
    }
}
