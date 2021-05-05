package com.codebind.ui;

// To do: button für heat simulation on off

import com.codebind.logic.ParticleEffect;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

public class AppDisplay extends JFrame {
    private final int pixelSize = 16;
    private final int amount = 50 * 1;
    private final int x = pixelSize * amount;
    private final int y = pixelSize * amount;

    private final JSplitPane splitPane;
    public final PixelDrawCanvas drawPanel;
    private final JPanel bottomPanel;
    private final JPanel inputPanel;
    private final JButton button;
    private final JSlider slider;
    private final JSlider sliderSimSpeed;
    private final JLabel temperature;
    private final JButton buttonDelete;
    private final JButton buttonHeat;
    private final JButton buttonCool;
    private final JButton buttonMetal;
    private final JButton buttonSand;
    private final JButton buttonWater;
    private final JButton buttonIce;
    private final JButton buttonSteam;

    public AppDisplay(String title) throws Exception {
        super(title);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panel initializations
        splitPane = new JSplitPane();
        splitPane.setEnabled(false);
        drawPanel = new PixelDrawCanvas(x, y, pixelSize);
        bottomPanel = new JPanel();
        inputPanel = new JPanel();

        // Reset Button
        button = new JButton("reset");
        button.addActionListener(new ActionListener() { //lambda function
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.reset();
                System.out.println("Done");
            }
        });

        // Draw size slider
        slider = new JSlider(JSlider.HORIZONTAL, 1, 31, 1);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                drawPanel.setDrawSize(slider.getValue());
            }
        });
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);

        // Simulation speed slider
        sliderSimSpeed = new JSlider(JSlider.HORIZONTAL, 1, 5, 1);
        sliderSimSpeed.setMajorTickSpacing(1);
        sliderSimSpeed.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                drawPanel.setSimulationSpeed(sliderSimSpeed.getValue());
            }
        });
        sliderSimSpeed.setPaintTicks(true);
        sliderSimSpeed.setPaintLabels(true);

        // Simulation speed slider labels
        JLabel speed = new JLabel("Speed");
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(sliderSimSpeed.getMinimum(), new JLabel("normal"));
        labelTable.put(sliderSimSpeed.getMaximum(), new JLabel("very fast"));
        sliderSimSpeed.setLabelTable(labelTable);


        // Temperature label
        temperature = new JLabel("Temperature: " + 200);

        // Grid layout configuration
        GridLayout layout = new GridLayout(0, 4);
        Dimension size = drawPanel.getPreferredSize();
        size.height += 250;
        size.width += 16;
        Dimension dim = getPreferredSize();
        dim.width = drawPanel.getWidth();

        setPreferredSize(size);
        getContentPane().setLayout(new GridLayout(1, 1));
        getContentPane().add(splitPane);

        // Split pane configuration
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(drawPanel);
        splitPane.setBottomComponent(bottomPanel);

        // Bottom panel configuration with inputPanel
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(inputPanel);

        // Input panel for user control (DrawSize, SimulationSpeed, reset etc.)
        BoxLayout ui = new BoxLayout(inputPanel, BoxLayout.X_AXIS);
        GridLayout controlLayout = new GridLayout(0, 2);
        controlLayout.setHgap(10);
        inputPanel.setLayout(controlLayout);


        GridLayout particleButtonLayout = new GridLayout(2, 3);
        particleButtonLayout.setHgap(10);
        particleButtonLayout.setVgap(10);

        // Particle Button grid
        JPanel buttonGrid = new JPanel();
        buttonGrid.setLayout(particleButtonLayout);
        buttonDelete = new JButton("Delete");
        buttonDelete.addActionListener(new ActionListener() { //lambda function
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.EMPTY);
            }
        });

        buttonHeat = new JButton("Heat");
        buttonHeat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.HEAT);
            }
        });

        buttonCool = new JButton("Cool");
        buttonCool.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {drawPanel.setCurrentParticle(ParticleEffect.COOL);}
        });

        buttonMetal = new JButton("Metal");
        buttonMetal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.METAL);
            }
        });

        buttonSand = new JButton("Sand");
        buttonSand.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.SAND);
            }
        });

        buttonWater = new JButton("Water");
        buttonWater.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.WATER);
            }
        });

        buttonIce = new JButton("Ice");
        buttonIce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.ICE);
            }
        });

        buttonSteam = new JButton("Steam");
        buttonSteam.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setCurrentParticle(ParticleEffect.STEAM);
            }
        });

        ArrayList<Color> buttonColors = new ArrayList<>();
        buttonColors.add(new Color(205, 205, 205));
        buttonColors.add(new Color(255, 145, 145));
        buttonColors.add(new Color(99, 186, 255));
        buttonColors.add(new Color(163, 163, 163));
        buttonColors.add(new Color(255, 242, 111));
        buttonColors.add(new Color(78, 173, 255));
        buttonColors.add(new Color(156, 206, 255));
        buttonColors.add(new Color(208, 208, 208));
        buttonColors.add(new Color(1,1,1));
        buttonColors.add(new Color(1,1,1));


        buttonGrid.add(buttonDelete);
        buttonGrid.add(buttonHeat);
        buttonGrid.add(buttonCool);
        buttonGrid.add(buttonMetal);
        buttonGrid.add(buttonSand);
        buttonGrid.add(buttonWater);
        buttonGrid.add(buttonIce);
        buttonGrid.add(buttonSteam);

        for (int i = 0; i < buttonGrid.getComponents().length; i++) {
            buttonGrid.getComponent(i).setBackground(buttonColors.get(i));
            buttonGrid.getComponent(i).setForeground(new Color(66, 66, 66));
        }

        inputPanel.add(buttonGrid);
        inputPanel.add(button);
        inputPanel.add(slider);
        inputPanel.add(sliderSimSpeed);
        inputPanel.add(temperature);

        // Show current temperature of the draw canvas depending on mouse positions
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                temperature.setText("Temperature: " + String.format("%.8f", drawPanel.getMouseTemp(), 11) +
                        " " + drawPanel.getMoving() + " " + drawPanel.getParticle());
            }
        };
        Timer timer = new Timer(1, taskPerformer);
        timer.setRepeats(true);
        timer.start();

        pack();
    }

    public static void main(String[] args) throws Exception {
        AppDisplay frame = new AppDisplay("Pixelpainter");
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
    }

    // Fills the String-parameter to specified size with char '0' (chars are added in front)
    private String fillToChar(String word, int size) {
        for (int i = word.length(); i <= size; i++) {
            word = "0" + word;
        }
        return word;
    }
}