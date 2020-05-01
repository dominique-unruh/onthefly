package de.unruh.onthefly;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class PopupDialogAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        // Using the event, create and show a dialog
        Project currentProject = event.getProject();
        String dlgMsg = System.getProperty("idea.auto.reload.plugins");
        Messages.showMessageDialog(currentProject, dlgMsg, "Popup", Messages.getInformationIcon());
    }
}
