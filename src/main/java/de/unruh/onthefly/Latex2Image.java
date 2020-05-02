package de.unruh.onthefly;

import com.intellij.ui.JBColor;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;


import java.awt.*;
import java.awt.image.BufferedImage;

import static org.scilab.forge.jlatexmath.TeXConstants.STYLE_DISPLAY;

public class Latex2Image {
    public static BufferedImage render(String latex) {
        TeXFormula formula = new TeXFormula(latex);
        long t = System.currentTimeMillis();
        Image image = formula.createBufferedImage(STYLE_DISPLAY, 30, JBColor.BLACK, JBColor.WHITE);
        long elapsed = System.currentTimeMillis() - t;
        System.out.println("Elapsed: " + elapsed);
        return (BufferedImage) image;
    }
}
