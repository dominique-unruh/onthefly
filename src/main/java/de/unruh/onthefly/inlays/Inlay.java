package de.unruh.onthefly.inlays;

import java.awt.*;

public abstract class Inlay {
    private ChangeListener changeListener;

    /** Indicates that this Inlay will not be rendered any more. Resources can be released. */
    protected void cancel() {};

    interface ChangeListener {
        void changed();
    }

    /** Recompute the inlay size given available width
     * @param width the available width
     * @return the height of the inlay */
    public abstract Dimension getInlayDimension(int width);

    /**
     * Draw the inlay.
     * A call to getInlayHeight is guaranteed to precede this.
     * @param g the graphics context on which to draw (at 0,0 coordinates)
     * @param width The width (guaranteed to be the width returned the most recent call to getInlayHeight)
     * @param height The height (guaranteed to be the height returned the most recent call to getInlayHeight)
     */
    public abstract void paintInlay(Graphics2D g, int width, int height);

    void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    /**
     * This should be invoked whenever the content or size of the
     * inlay changes.
     */
    final protected void changed() {
        if (changeListener!=null)
            if (EventQueue.isDispatchThread())
                changeListener.changed();
            else
                EventQueue.invokeLater(() -> changeListener.changed());
    }

    // TODO: should this be destroyed somehow?
}
