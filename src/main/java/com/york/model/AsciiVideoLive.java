package com.york.model;

public class AsciiVideoLive implements AsciiArt {
    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getArea() {
        return 0;
    }

    @Override
    public int getCharWidth() {
        return 0;
    }

    @Override
    public String getPalette() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public AsciiArt setCharWidth(int newCharWidth) throws IllegalArgumentException {
        return null;
    }

    @Override
    public AsciiArt setPalette(String newPalette) throws IllegalArgumentException {
        return null;
    }

    @Override
    public AsciiArt setInvertedShading(boolean invertShading) {
        return null;
    }

    @Override
    public AsciiArt setName(String newName) {
        return null;
    }

    @Override
    public boolean usesDefaultPalette() {
        return false;
    }

    @Override
    public boolean shadingIsInverted() {
        return false;
    }
}
