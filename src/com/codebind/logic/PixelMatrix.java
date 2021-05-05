package com.codebind.logic;

import com.codebind.util.CoordinateDimensionValidator;

import java.util.ArrayList;
import java.util.Random;

// Does controls and calculations for Pixels in the matrix
public class PixelMatrix {
    private Pixel[][] matrix;
    private CoordinateDimensionValidator dim;
    private int rows;
    private int columns;

    public PixelMatrix(int rows, int columns, CoordinateDimensionValidator dim) {
        matrix = new Pixel[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.dim = dim;

        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                matrix[x][y] = new Pixel(ParticleEffect.EMPTY);
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Pixel getPixel(int x, int y) {
        if (dim.insideMatrix(x, y)) {
            return matrix[x][y];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    // Main calculation function, runs all the movement/heat logic
    public void run() {
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < columns; y++) {
                Pixel current = matrix[x][y];
                eventCheck(current, x, y);
                double percentageLoss = 1;
                switch (current.getEffect()) {
                    case EMPTY -> percentageLoss = 100d;
                    case METAL -> percentageLoss = 10d;
                    case SAND -> {
                        step(current, x, y);
                        percentageLoss = 25d;
                    }
                    case WATER -> {
                        step(current, x, y);
                        percentageLoss = 25d;
                    }
                    case MOLTEN_METAL -> {
                        step(current, x, y);
                        percentageLoss = 30d;
                    }
                    case MOLTEN_GLASS -> {
                        step(current, x, y);
                        percentageLoss = 25d;
                    }
                    case STEAM -> {
                        percentageLoss = 15d;
                        step(current, x, y);
                    }
                    case ICE -> percentageLoss = 15d;
                }
                heatTransfer(current, x, y, percentageLoss);
            }
        }
    }

    // Heat transfer to adjacent Pixels
    private void heatTransfer(Pixel current, int x, int y, double percentageLoss) {
        Pixel[] adjacent = new Pixel[4]; // top, right, bottom, left
        if (y - 1 >= 0) adjacent[0] = matrix[x][y - 1];
        if (y + 1 < columns) adjacent[1] = matrix[x][y + 1];
        if (x + 1 < rows) adjacent[2] = matrix[x + 1][y];
        if (x - 1 >= 0) adjacent[3] = matrix[x - 1][y];

        int size = 0;
        for (Pixel pel : adjacent) if (pel != null) size++;

        for (int i = 0; i < 1; i++) {
            for (Pixel pel : adjacent) {
                if (pel != null) {
                    if (current.getEffect() != ParticleEffect.EMPTY && pel.getEffect() == ParticleEffect.EMPTY)
                        percentageLoss /= 1000;
                    if (current.getEffect() != current.getEffect())
                        percentageLoss /= 100;
                    transfer(current, pel, percentageLoss / size); // über 100 gibt es spielereien
                }
                //transfer(current, top, 110d);
            }
        }
    }

    // Movement logic for Pixels
    private void step(Pixel current, int x, int y) {
        Random rand = new Random();
        Pixel[] adjacent = updateMovable(current, x, y); //left, bottom, right
        if (current.isMoving()) {
            try {
                switch (current.getEffect()) {
                    case HEAT:
                        break;
                    case EMPTY:
                        break;
                    case METAL:
                        break;
                    case SAND:
                        if (getPixel(x, y + 1).getEffect() == ParticleEffect.EMPTY || getPixel(x, y + 1).getEffect() == ParticleEffect.WATER) {
                            fall(x, y, 35); // current = (x,y+1)
                        }

                        if (getPixel(x, y + 2).getEffect() == ParticleEffect.SAND) {
                            ArrayList<Pixel> bottom = new ArrayList<>();
                            ArrayList<Pixel> left = new ArrayList<>();
                            ArrayList<Pixel> right = new ArrayList<>();

                            for (int i = 0; i < 4; i++) {
                                if (getPixel(x + 1, y + 1 + i).getEffect() == ParticleEffect.EMPTY) {
                                    right.add(getPixel(x + 1, y + 1 + i));
                                }
                                if (getPixel(x - 1, y + 1 + i).getEffect() == ParticleEffect.EMPTY) {
                                    left.add(getPixel(x - 1, y + 1 + i));
                                }
                                if (getPixel(x, y - 1 - i).getEffect() == ParticleEffect.SAND) {
                                    bottom.add(getPixel(x, y - 1 - i));
                                }
                            }
                            System.out.println(left.size() + " " + right.size());
                        }
                        break;
                    case WATER:
                        // Mit eigener Klasse arbeiten?
                        fluidStep(current, x, y);
                        break;
                    case MOLTEN_METAL:
                        fluidStep(current, x, y);
                        break;
                    case MOLTEN_GLASS:
                        fluidStep(current,x,y);
                        break;
                    case STEAM:
                        if (70 > rand.nextInt(101) && getPixel(x, y - 1).getEffect() != ParticleEffect.METAL) {
                            swap(x, y, x, y - 1);
                        } else {
                            if (rand.nextInt(2) == 1) {
                                swap(x, y, x - 1, y);
                            } else {
                                swap(x, y, x + 1, y);
                            }
                        }
                        break;
                }
            } catch (IndexOutOfBoundsException io) {
            }
        }
    }

    // Event check for different states (ice -> water, water -> steam etc.)
    private void eventCheck(Pixel current, int x, int y) {
        Random rand = new Random();
        switch (current.getEffect()) {
            case WATER:
                if (current.getTemp() > 100) {
                    current.setParticleEffect(ParticleEffect.STEAM);
                } else if (current.getTemp() < 0) {
                    current.setParticleEffect(ParticleEffect.ICE);
                }
                break;
            case STEAM:
                if (current.getTemp() <= 100 && rand.nextInt(101) > 65) {
                    current.setParticleEffect(ParticleEffect.WATER);
                }
                break;
            case METAL:
                if (current.getTemp() > 500) {
                    current.setParticleEffect(ParticleEffect.MOLTEN_METAL);
                }
                break;
            case MOLTEN_METAL:
                if (current.getTemp() <= 500) {
                    current.setParticleEffect(ParticleEffect.METAL);
                }
                break;
            case ICE:
                if (current.getTemp() >= 0) {
                    current.setParticleEffect(ParticleEffect.WATER);
                }
                break;
            case GLASS:
            case SAND:
                if (current.getTemp() >= 800) {
                    current.setParticleEffect(ParticleEffect.MOLTEN_GLASS);
                }
                break;
            case MOLTEN_GLASS:
                if (current.getTemp() <= 800) {
                    current.setParticleEffect(ParticleEffect.GLASS);
                }
        }
    }

    // Changes if the Pixel is "able" to move depending on the ParticleEffect
    // generally checks if it's enclosed by other adjacent Pixels of the same ParticleEffect-type
    private Pixel[] updateMovable(Pixel current, int x, int y) {
        Pixel[] adjacent = new Pixel[3]; //left, bottom, right;
        switch (current.getEffect()) {
            case SAND -> current.setMoving(!dim.insideY(y - 1) || getPixel(x, y - 1).getEffect() != ParticleEffect.SAND);
            case WATER, MOLTEN_METAL -> {
                if (dim.insideX(x - 1)) adjacent[0] = getPixel(x - 1, y);
                if (dim.insideY(y + 1)) adjacent[1] = getPixel(x, y + 1);
                if (dim.insideX(x + 1)) adjacent[2] = getPixel(x + 1, y);
                for (Pixel pel : adjacent) {
                    if (pel != null && pel.getEffect() == ParticleEffect.EMPTY) {
                        current.setMoving(true);
                        return adjacent;
                    }
                    current.setMoving(false);
                }
                return adjacent;
            }
        }
        current.setMoving(true);
        return adjacent;
    }

    // drops a Pixel by 1
    private void fall(int x, int y, int speed) {
        Random rand = new Random();
        if (rand.nextInt(100) <= 35) {
            switch (getPixel(x, y).getEffect()) {
                case SAND:
                    if (dim.insideY(y + 1)) {
                        if (getPixel(x, y + 1).getEffect() == ParticleEffect.EMPTY || getPixel(x, y + 1).getEffect() == ParticleEffect.WATER) {
                            swap(x, y, x, y + 1);
                        }
                    }
                    break;
                case WATER:
                case MOLTEN_GLASS:
                case MOLTEN_METAL:
                    if (dim.insideY(y + 1)) {
                        if (getPixel(x, y + 1).getEffect() == ParticleEffect.EMPTY) {
                            swap(x, y, x, y + 1);
                        }
                    }
                    break;
            }
        }
    }

    // Movement logic for fluid Pixels
    private void fluidStep(Pixel current, int x, int y) {
        Random rand = new Random();
        if (getPixel(x, y + 1).getEffect() == ParticleEffect.EMPTY) {
            fall(x, y, 90);
        } else {
            if (x - 1 < 0) {
                swap(x, y, x + 1, y);
                return;
            } else if (x + 1 >= columns) {
                swap(x, y, x - 1, y);
                return;
            }

            if (dim.insideX(x + 1) && getPixel(x + 1, y).getEffect() == current.getEffect()) {
                current.setLeftMoving(true);
            } else if (dim.insideX(x - 1) && getPixel(x - 1, y).getEffect() == current.getEffect()) {
                current.setLeftMoving(false);
            }

            if (rand.nextInt(101) < 32) {
                if (dim.insideX(x + 1) && getPixel(x + 1, y).getEffect() == ParticleEffect.EMPTY && !current.isLeftMoving()) {
                    swap(x, y, x + 1, y);
                } else if (dim.insideX(x - 1) && getPixel(x - 1, y).getEffect() == ParticleEffect.EMPTY && current.isLeftMoving()) {
                    swap(x, y, x - 1, y);
                }
            }
        }
    }

    // Transfers heat from one Pixel to the other
    private void transfer(Pixel a, Pixel b, double percentageLoss) {
        double difference = a.getTemp() - b.getTemp();
        if (difference > 0) {
            a.changeTemp(-difference * (percentageLoss / 100));
            b.changeTemp(difference * (percentageLoss / 100));
        }
    }

    // changes the state of every Pixel to be empty
    public void reset() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j].setTemp(0);
                matrix[i][j].setParticleEffect(ParticleEffect.EMPTY);
            }
        }
    }

    // swaps two Pixels
    public void swap(int row1, int col1, int row2, int col2) {
        if (dim.insideMatrix(row1, col1) && dim.insideMatrix(row2, col2)) {
            Pixel temp = matrix[row1][col1];
            matrix[row1][col1] = matrix[row2][col2];
            matrix[row2][col2] = temp;
        }
    }
}
