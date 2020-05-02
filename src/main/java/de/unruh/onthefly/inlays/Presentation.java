package de.unruh.onthefly.inlays;

import com.intellij.codeInsight.hints.presentation.BasePresentation;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

@SuppressWarnings("UnstableApiUsage")
class Presentation extends BasePresentation implements Inlay.ChangeListener {
    private final Editor editor;
    private final Inlay inlay;
    Dimension dimension;
    ComponentListener listener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent componentEvent) {
            update(false);
        }
    };

    @Override
    public boolean updateState(@NotNull InlayPresentation previousPresentation) {
        if (previousPresentation instanceof Presentation)
            ((Presentation)previousPresentation).discard();
        return true;
    }

    private void discard() {
        inlay.setChangeListener(null);
        inlay.cancel();
    }


    public Presentation(Editor editor, Inlay inlay) {
        this.editor = editor;
        this.inlay = inlay;
        dimension = measure();
        editor.getComponent().addComponentListener(listener);
        inlay.setChangeListener(this);
    }

    Dimension measure() {
        final int width = editor.getContentComponent().getVisibleRect().width;
//        int height = 0;
//        if (dimension==null || dimension.width != width)
        return inlay.getInlayDimension(width);
//        else
//            height = dimension.height;
//        return new Dimension(width, height);
    }

    private void update(boolean contentChanged) {
        Dimension oldDimension = dimension;
        dimension = measure();
        System.out.println(dimension);
        System.out.println(oldDimension);
        if (!oldDimension.equals(dimension)) {
            System.out.println("Fire size changed.");
            fireSizeChanged(oldDimension, dimension);
        } else if (contentChanged) {
            System.out.println("Fire content changed." + inlay);
            fireContentChanged(new Rectangle(0, 0, dimension.width, dimension.height));
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO: finalize won't be called because listener has a reference to this, and the editor has a reference to listener
        editor.getComponent().removeComponentListener(listener);
        super.finalize();
    }

    @Override
    public int getWidth() {
        return dimension.width;
    }

    @Override
    public int getHeight() {
        return dimension.height;
    }

    @Override
    public void paint(@NotNull Graphics2D g, @NotNull TextAttributes textAttributes) {
        System.out.println("Redrawing "+inlay);
        g = (Graphics2D) g.create();
        try {
            g.setColor(JBColor.GRAY);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(JBColor.LIGHT_GRAY);
            g.fillRect(5, 5, getWidth() - 10, getHeight() - 10);
            g.setColor(JBColor.BLACK);
//            g.clipRect(10, 10, getWidth() - 20, getHeight() - 20);
            inlay.paintInlay(g, getWidth(), getHeight());
        } finally {
            g.dispose();
        }
    }

    @Override
    public void changed() {
        update(true);
    }
}
