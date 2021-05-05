package com.codebind.logic;

import java.awt.*;
import java.util.Random;

// individual Pixel with information regarding it's current state in the Canvas
public class Pixel implements Comparable<Pixel> {
    private double temp;
    private Color color;
    private boolean moving;
    private boolean leftMoving;
    private ParticleEffect effect;

    public Pixel(ParticleEffect effect) {
        temp = 0;
        this.effect = effect;
        moving = false;
        leftMoving = false;
        color = Color.BLACK;
    }

    public void changeTemp(double val) {
        temp += val;
        if (temp < -277) {
            temp = -277;
        }
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public void setMoving(boolean x) {
        moving = x;
    }

    public boolean isMoving() {
        return moving;
    }

    public boolean isLeftMoving() {
        return leftMoving;
    }

    public void setLeftMoving(boolean x) {
        leftMoving = x;
    }

    public Color getColor() {
        switch (effect) {
            case EMPTY:
                int x = (int) Math.abs(temp);
                if (x > 155) x = 155;
                if (x < 0) x = 0;
                if (temp > 0) {
                    return new Color(x, 0, 0);
                } else {
                    return new Color(0, x/2, x);
                }
        }

        return color;
    }

    public void setColor(Color c) {
        color = c;
    }

    public void setParticleEffect(ParticleEffect n) {
        switch (n) {
            case HEAT:
                setTemp(temp + 10);
                break;
            case EMPTY:
                setTemp(0);
                setColor(Color.BLACK);
                break;
            case METAL:
                setTemp(20);
                setColor(colorIntervall(Color.GRAY, 10));
                break;
            case SAND:
                setTemp(20);
                setColor(colorIntervall(new Color(255, 201, 84), 25));
                break;
            case WATER:
                setTemp(20);
                setColor(colorIntervall(new Color(69, 151, 229), 25));
                break;
            case MOLTEN_METAL:
                setColor(colorIntervall(new Color(255, 128, 31), 55));
                break;
            case STEAM:
                setTemp(150);
                setColor(colorIntervall(new Color(187, 187, 187), 30));
                break;
            case ICE:
                setTemp(-20);
                setColor(colorIntervall(new Color(151, 210, 255), 15));
                break;
            case COOL:
                setTemp(temp - 10);
                break;
        }
        if (n != ParticleEffect.HEAT && n != ParticleEffect.COOL) effect = n;
    }

    public ParticleEffect getEffect() {
        return effect;
    }

    private Color colorIntervall(Color base, int intervall) {
        Random rand = new Random();
        int[] rgb = new int[3];
        rgb[0] = base.getRed();
        rgb[1] = base.getGreen();
        rgb[2] = base.getBlue();

        int sub = rand.nextInt(intervall) - intervall / 2;
        for (int i = 0; i < 3; i++) {
            rgb[i] = rgb[i] + sub;
            if (rgb[i] < 0) rgb[i] = 0;
            if (rgb[i] > 255) rgb[i] = 255;
        }
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    @Override
    public int compareTo(Pixel pixel) {
        if (this == pixel) return 0;
        if (pixel != null) {
            return Integer.compare((int) temp, (int) pixel.getTemp());
        }
        return -1;
    }
}
