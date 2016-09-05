////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.gui.MainFrameModel.ParseMode;

/**
 * Displays information about a parse tree.
 * The user can change the file that is parsed and displayed
 * using a JFileChooser.
 *
 * @author Lars Kühne
 * @author Vladislav Lisetskiy
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 7970053543351871890L;

    /**
     * Modes titles which available in combobox.
     */
    private static final String[] MODES_TITLES = new String[] {
        "Plain Java",
        "Java with comments",
        "Java with comments and Javadocs",
    };

    /** Checkstyle frame model. */
    private final transient MainFrameModel model = new MainFrameModel();
    /** Reload action. */
    private final ReloadAction reloadAction = new ReloadAction();
    /** Code text area. */
    private JTextArea textArea;
    /** Tree table. */
    private JTreeTable treeTable;

    /** Create a new MainFrame. */
    public MainFrame() {
        createContent();
    }

    /** Create content of this MainFrame. */
    private void createContent() {
        setLayout(new BorderLayout());

        textArea = new JTextArea(20, 15);
        textArea.setEditable(false);
        final JScrollPane textAreaScrollPane = new JScrollPane(textArea);

        treeTable = new JTreeTable(model.getParseTreeTableModel());
        treeTable.setEditor(textArea);
        treeTable.setLinePositionMap(model.getLinesToPosition());
        final JScrollPane treeTableScrollPane = new JScrollPane(treeTable);

        final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, treeTableScrollPane,
            textAreaScrollPane);
        add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(0.7);

        add(createButtonsPanel(), BorderLayout.PAGE_END);

        pack();
    }

    /**
     * Create buttons panel.
     * @return buttons panel.
     */
    private JPanel createButtonsPanel() {
        final JButton openFileButton = new JButton(new FileSelectionAction());
        openFileButton.setMnemonic(KeyEvent.VK_S);
        openFileButton.setText("Open File");

        reloadAction.setEnabled(false);
        final JButton reloadFileButton = new JButton(reloadAction);
        reloadFileButton.setMnemonic(KeyEvent.VK_R);
        reloadFileButton.setText("Reload File");

        final JComboBox<String> modesCombobox = new JComboBox<>(MODES_TITLES);
        modesCombobox.setSelectedIndex(0);
        modesCombobox.addActionListener(e -> {
            switch (modesCombobox.getSelectedIndex()) {
                case 0:
                    model.setParseMode(ParseMode.PLAIN_JAVA);
                    break;
                case 1:
                    model.setParseMode(ParseMode.JAVA_WITH_COMMENTS);
                    break;
                case 2:
                    model.setParseMode(ParseMode.JAVA_WITH_JAVADOC_AND_COMMENTS);
                    break;
                default:
                    throw new IllegalStateException("Unknown GUI mode");
            }
            reloadAction.actionPerformed(null);
        }
        );

        final JLabel label = new JLabel("  Modes:  ", SwingConstants.RIGHT);

        final JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        buttonPanel.add(openFileButton);
        buttonPanel.add(reloadFileButton);

        final JPanel modesPanel = new JPanel();
        modesPanel.add(label);
        modesPanel.add(modesCombobox);

        final JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(buttonPanel);
        mainPanel.add(modesPanel, BorderLayout.EAST);

        return mainPanel;
    }

    /**
     * Open file and load it.
     * @param sourceFile the file to open.
     */
    public void openFile(File sourceFile) {
        try {
            model.openFile(sourceFile);
            setTitle(model.getTitle());
            reloadAction.setEnabled(model.isReloadActionEnabled());
            textArea.setText(model.getText());
            treeTable.setLinePositionMap(model.getLinesToPosition());
        }
        catch (final CheckstyleException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    /**
     * Handler for file selection action events.
     */
    private class FileSelectionAction extends AbstractAction {
        private static final long serialVersionUID = 1762396148873280589L;

        @Override
        public void actionPerformed(ActionEvent event) {
            final JFileChooser fileChooser = new JFileChooser(model.getLastDirectory());
            final FileFilter filter = new JavaFileFilter();
            fileChooser.setFileFilter(filter);

            final int returnCode = fileChooser.showOpenDialog(MainFrame.this);
            if (returnCode == JFileChooser.APPROVE_OPTION) {
                final File file = fileChooser.getSelectedFile();
                openFile(file);
            }
        }
    }

    /**
     * Handler for reload action events.
     */
    private class ReloadAction extends AbstractAction {
        private static final long serialVersionUID = -890320994114628011L;

        @Override
        public void actionPerformed(ActionEvent event) {
            openFile(model.getCurrentFile());
        }
    }

    /**
     * Filter for Java files.
     */
    private static class JavaFileFilter extends FileFilter {
        @Override
        public boolean accept(File file) {
            return MainFrameModel.shouldAcceptFile(file);
        }

        @Override
        public String getDescription() {
            return "Java Source File";
        }
    }
}
