package de.unruh.onthefly;

import de.unruh.onthefly.inlays.Inlay;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.Objects;

public class FakeComputationInlay extends Inlay {
    private final String var;
    private final Expression expr;
    private final int id;
    String text = "[initializing]";
    BufferedImage image = null;
    Thread thread;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeComputationInlay that = (FakeComputationInlay) o;
        return var.equals(that.var) &&
                expr.equals(that.expr);
    }

    protected void cancel() { thread.interrupt(); }

    @Override
    public int hashCode() {
        return Objects.hash(var, expr);
    }

    @Override
    public String toString() {
        return "FakeComputationInlay{" + var + " @ " + id + '}';
    }

    private static int idCounter = 0;
    public FakeComputationInlay(String var, Expression expr) {
        this.var = var;
        this.expr = expr;
        this.id = idCounter ++;

        thread = new Thread(() -> {
            try {
                String latex = expr.toLatex();
                Thread.sleep(500);
                text = "Rendering: " + latex;
                changed();
                Thread.sleep(500);
                image = Latex2Image.render(latex);
                changed();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public Dimension getInlayDimension(int width) {
        if (image==null) {
            System.out.println("No image.");
            return new Dimension(width, 50);
        }

//        int height = image.getHeight();
//        System.out.println("Height: " + height);
        return new Dimension(image.getWidth(), image.getHeight());
    }

    public void paintInlay(Graphics2D g, int width, int height) {
        if (image==null)
            g.drawString(text, 20, 30);
        else
            g.drawImage(image, 0, 0, null);
    }
}
