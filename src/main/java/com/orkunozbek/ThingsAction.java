package com.orkunozbek;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ThingsAction extends AnAction {

    private static final Pattern p = Pattern.compile("\\s*//\\s*todo `(.*)`(.*)", Pattern.CASE_INSENSITIVE);

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Caret caret = e.getRequiredData(CommonDataKeys.CARET);
        int startOfLine = caret.getVisualLineStart();
        int endOfLine = caret.getVisualLineEnd();
        caret.setSelection(startOfLine, endOfLine);
        String todoLine = caret.getSelectedText().replaceAll("\\s+$", "");
        if (todoLine.length() > 0) {
            Matcher matcher = p.matcher(todoLine);
            if (matcher.matches()) {
                MatchResult result = matcher.toMatchResult();
                Runtime rt = Runtime.getRuntime();
                try {
                    rt.exec("open " + convertToThingsUrl(result.group(1), result.group(2), "Inbox"));
                    caret.removeSelection();
                    WindowManager.getInstance().getStatusBar(e.getProject()).setInfo("Todo added to Things3");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        // Set the availability based on whether a project is open
        Project project = anActionEvent.getProject();
        anActionEvent.getPresentation().setEnabledAndVisible(project != null);
    }

    private String convertToThingsUrl(String title, String todo, String list) throws UnsupportedEncodingException {
        String urlEncodedTitle = URLEncoder.encode(title, "UTF-8").replace("+", "%20");
        String urlEncodedTodo = URLEncoder.encode(todo, "UTF-8").replace("+", "%20");
        String todoUrl = "things:///add?title=" + urlEncodedTitle + "&notes=" + urlEncodedTodo +
                "&list=" + list + "&reveal=true";
        if (todo.length() == 0) {
            todoUrl += "&show-quick-entry=true";
        }
        return todoUrl;
    }
}
