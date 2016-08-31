package com.puppycrawl.tools.checkstyle.gui;

/**
 * Code selection presentation model. It's used to calculate and store selected code start and end.
 *
 * @see CodeSelectorDetailAstModel
 * @see CodeSelectorDetailNodeModel
 * @author Baratali
 *
 */
public interface CodeSelectorPModel {

	/**
	 * Start position of the selected code.
	 * @return start
	 */
	int getSelectionStart();

	/**
	 * End position of the selected code.
	 * @return end
	 */
	int getSelectionEnd();
}
