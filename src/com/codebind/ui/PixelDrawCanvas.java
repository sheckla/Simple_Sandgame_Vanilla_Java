package com.codebind.ui;

import com.codebind.util.CoordinateDimensionValidator;
import com.codebind.logic.ParticleEffect;
import com.codebind.logic.Pixel;
import com.codebind.logic.PixelMatrix;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*;

public class PixelDrawCanvas extends JPanel {
    private PixelMatrix matrix;
    private ParticleEffect currentParticle;
    private CoordinateDimensionValidator dim;
    private BufferedImage canvas;
    private Point mousePt = new Point(0, 0);

    private int pixelSize;
    private int rows;
    private int columns;
    private int simulationSpeed;
    private int drawSize;
    private boolean holdingDraw;
    private boolean holdingShowTemp;
    private boolean showGrid = false;

    // x, y
    public PixelDrawCanvas(int x, int y, int pixelSize) {
        currentParticle = ParticleEffect.HEAT;
        this.pixelSize = pixelSize;
        this.rows = x / pixelSize;
        this.columns = y / pixelSize;
        dim = new CoordinateDimensionValidator(x / pixelSize, y / pixelSize, x, y);
        canvas = new BufferedImage(rows * pixelSize, columns * pixelSize, BufferedImage.TYPE_INT_ARGB);
        matrix = new PixelMatrix(rows, columns, dim);
        drawSize = 1;
        simulationSpeed = 1;

        addMouseListener(new MouseAdapter() {

            // Main draw-brush function to draw on the canvas
            // works with the holdingDraw boolean to check if the mouse is being held down
            @Override
            public void mousePressed(MouseEvent e) {
                holdingDraw = true;
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            while (holdingDraw) {
                                Point p = MouseInfo.getPointerInfo().getLocation();
                                doSwing(p);
                                mousePt = p;
                                int x = mousePt.x / pixelSize;
                                int y = mousePt.y / pixelSize;
                                if (dim.insideMatrix(x, y)) drawSquare(x, y, drawSize, drawSize, currentParticle);
                                //System.out.println("painted " + x + " " + y);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                holdingDraw = false;
                repaint();
            }

            // Checks if the user has his mouse inside the canvas and updates "mousePt" accordingly
            @Override
            public void mouseEntered(MouseEvent e) {
                holdingShowTemp = true;
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            while (holdingShowTemp) {
                                Point p = MouseInfo.getPointerInfo().getLocation();
                                doSwing(p);
                                mousePt = p;
                                //System.out.println("TEMP: " + mousePt.x + " " + mousePt.y);
                                Thread.sleep(100);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                mousePt = e.getPoint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                holdingShowTemp = false;
            }
        });

        // Main Thread to update the Panel
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            for (int i = 0; i < simulationSpeed; i++) {
                                PixelDrawCanvas.this.run();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            }
        };
        Timer timer = new Timer(1, taskPerformer);
        timer.setRepeats(true);
        timer.start();
    }

    public void changePixelSize(int n) {
        pixelSize = n;
    }

    public void setCurrentParticle(ParticleEffect x) {
        currentParticle = x;
    }

    private void doSwing(Point p) {
        SwingUtilities.convertPointFromScreen(p, this);
    }

    public void setSimulationSpeed(int x) {
        simulationSpeed = x;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(canvas, null, null);
    }

    public void reset() {
        matrix.reset();
    }

    public Dimension getPreferredSize() {
        return new Dimension(canvas.getWidth(), canvas.getHeight());
    }

    public String getParticle() {
        if (dim.insideCanvasDimension(mousePt.x, mousePt.y)) {
            return "" + matrix.getPixel(mousePt.x / pixelSize, mousePt.y / pixelSize).getEffect();
        }
        return "";
    }

    public String getMoving() {
        if (dim.insideCanvasDimension(mousePt.x, mousePt.y)) {
            return "" + matrix.getPixel(mousePt.x / pixelSize, mousePt.y / pixelSize).isMoving();
        }
        return "";
    }

    public double getMouseTemp() {
        if (dim.insideCanvasDimension(mousePt.x, mousePt.y)) {
            return matrix.getPixel(mousePt.x / pixelSize, mousePt.y / pixelSize).getTemp();
        }
        return 0;
    }

    public void setDrawSize(int n) {
        drawSize = n;
    }

    public void fillCanvas(Color c) {
        int color = c.getRGB();
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                canvas.setRGB(x, y, color);
            }
        }
        repaint();
    }

    // Draws a square within the Canvas borders
    public void drawSquare(int row, int column, int width, int height, ParticleEffect effect) {
        Random rand = new Random();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (dim.insideMatrix(row + i - height / 2, column + j - width / 2)) {
                    if (rand.nextInt(100) < 1) {
                        Pixel current = matrix.getPixel(row + i - height / 2, column + j - width / 2);
                        current.setParticleEffect(effect);
                        drawPixel(row + i - height / 2, column + j - width / 2);
                    }
                }
            }
        }
    }

    // Main updating function to draw the Panel onscreen
    public void run() {
        matrix.run();
        for (int x = 0; x < canvas.getWidth() / pixelSize; x++) {
            for (int y = 0; y < canvas.getHeight() / pixelSize; y++) {
                if (dim.insideX(x) && dim.insideY(y)) {
                    drawPixel(x, y);
                }
            }
        }
        repaint();
    }

    // Single Pixel drawing function, used by drawSquare()
    private void drawPixel(int row, int column) {
        Pixel current = matrix.getPixel(row, column);
        int offsetX = pixelSize * row;
        int offsetY = pixelSize * column;

        for (int x = 0; x < pixelSize; x++) {
            for (int y = 0; y < pixelSize; y++) {
                if (dim.insideCanvasDimension(x, y)) {
                    canvas.setRGB(x + offsetX, y + offsetY, current.getColor().getRGB());
                    if (showGrid) {
                        if (x == 0 || x == pixelSize - 1 || y == 0 || y == pixelSize - 1) {
                            canvas.setRGB(x + offsetX, y + offsetY, new Color(55, 55, 55).getRGB());
                        }
                    }
                }
            }
        }
    }
}