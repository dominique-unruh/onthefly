package de.unruh.onthefly;

import com.intellij.codeInsight.hints.InlayHintsCollector;
import com.intellij.codeInsight.hints.InlayHintsSink;
import com.intellij.codeInsight.hints.presentation.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import de.unruh.onthefly.psi.SimpleProperty;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Arrays;
import java.util.LinkedList;

@SuppressWarnings("UnstableApiUsage")
public class SimpleInlayCollector implements InlayHintsCollector {
    @Override
    public boolean collect(@NotNull PsiElement element, @NotNull Editor editor, @NotNull InlayHintsSink inlayHintsSink) {
        if (!(element instanceof SimpleProperty)) return true;

        SimpleProperty property = (SimpleProperty) element;
        String value = property.getValue();
        String key = property.getValue();

        System.out.println("Collecting: "+key);

        InlayPresentation inlayPresentation = new Presentation(editor, key);
//        JTextField text = new JTextField(value);
//        InlayPresentation inlayPresentation = new ComponentPresentation(editor, text);

//        inlayHintsSink.addInlineElement(element.getTextRange().getEndOffset(), true, inlayPresentation);
        inlayHintsSink.addBlockElement(element.getTextRange().getEndOffset(), false, false, 0, inlayPresentation);
        return true;
    }

    private static class ComponentPresentation extends BasePresentation {
        private final Editor editor;
        private final Component component;
        private Dimension dimension;
        final private Container container = new JPanel() {
            @Override
            public boolean isValidateRoot() {
                return true;
            }
        };

        ComponentListener listener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                update();
            }
        };

        public ComponentPresentation(Editor editor, Component component) {
            this.editor = editor;
            this.component = component;
            container.add(component);
            // TODO: remove the listener again once the inlay is gone
            editor.getComponent().addComponentListener(listener);
            layout();
        }

        private void layout() {
            try {
                int width = editor.getContentComponent().getVisibleRect().width;
                System.out.println("Width: "+width);
//                component.setMinimumSize(new Dimension(width, 0));
//                component.setMaximumSize(new Dimension(width, 1000));
                container.setSize(width, 1000);
                container.revalidate();
                component.revalidate();
                int height = component.getHeight();
                System.out.println("Height: "+height);
                dimension = new Dimension(width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void update() {
            Dimension oldDimension = dimension;
            layout();
            if (!dimension.equals(oldDimension))
                fireSizeChanged(oldDimension, dimension);
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
        public void paint(@NotNull Graphics2D graphics2D, @NotNull TextAttributes textAttributes) {
            try {
            graphics2D.clipRect(0, 0, dimension.width, dimension.height);
            component.paintAll(graphics2D);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class Presentation extends BasePresentation {
        private final Editor editor;
        private final String text;
        Dimension dimension;
        ComponentListener listener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                update();
            }
        };

        public Presentation(Editor editor, String text) {
            this.editor = editor;
            this.text = text;
            dimension = measure();
            editor.getComponent().addComponentListener(listener);
        }

        Dimension measure() {
            return new Dimension(editor.getContentComponent().getVisibleRect().width, 50);
        }

        private void update() {
            Dimension oldDimension = dimension;
            dimension = measure();
            fireSizeChanged(oldDimension, dimension);
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
            g = (Graphics2D) g.create();
            try {
                g.setColor(JBColor.GRAY);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(JBColor.LIGHT_GRAY);
                g.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
                g.setColor(JBColor.BLACK);
                g.clipRect(10, 10, getWidth() - 20, getHeight() - 20);
                g.drawString("[" + text + "]", 20, 30);
            } finally {
                g.dispose();
            }
        }
    }
}
