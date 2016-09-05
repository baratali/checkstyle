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

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.puppycrawl.tools.checkstyle.api.DetailNode;
import com.puppycrawl.tools.checkstyle.utils.JavadocUtils;

/**
 * Presentation model for CodeSelector.
 * @author unknown
 */
public class CodeSelectorDetailNodeModel implements CodeSelectorPModel {
    /** DetailAST node. */
    private final DetailNode node;
    /** Mapping. */
    private final List<Integer> lines2position;
    /** Selection start position. */
    private int selectionStart;
    /** Selection end position. */
    private int selectionEnd;

    /**
     * Constructor.
     * @param node ast node.
     * @param lines2position list to map lines.
     */
    public CodeSelectorDetailNodeModel(DetailNode node, List<Integer> lines2position) {
        this.node = node;
        this.lines2position = ImmutableList.copyOf(lines2position);
        findSelectionPositions();
    }

    /**
     * @return selection start position.
     */
    @Override
    public int getSelectionStart() {
        return selectionStart;
    }

    /**
     * @return selection end position.
     */
    @Override
    public int getSelectionEnd() {
        return selectionEnd;
    }

    /**
     * Find start and end selection positions from AST line and Column.
     */
    private void findSelectionPositions() {
        selectionStart = lines2position.get(node.getLineNumber()) + node.getColumnNumber();

        if (node.getChildren().length == 0
                && JavadocUtils.getTokenName(node.getType()).equals(node.getText())) {
            selectionEnd = selectionStart;
        }
        else {
            selectionEnd = findLastPosition(node);
        }
    }

    /**
     * Finds the last position of node without children.
     * @param astNode DetailAST node.
     * @return Last position of node without children.
     */
    private int findLastPosition(final DetailNode astNode) {
        final int childrenCount = astNode.getChildren().length;
        if (childrenCount == 0) {
            return lines2position.get(astNode.getLineNumber()) + astNode.getColumnNumber()
                    + astNode.getText().length();
        }
        else {
            return findLastPosition(astNode.getChildren()[childrenCount - 1]);
        }
    }
}
