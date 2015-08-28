/*
 * Copyright 2012-2014 Dan Cioca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dci.intellij.dbn.code.sql.color;

import java.awt.Color;

import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.SyntaxHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.SimpleTextAttributes;

public interface SQLTextAttributesKeys
{
	TextAttributesKey LINE_COMMENT = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".LineComment", SyntaxHighlighterColors.LINE_COMMENT.getDefaultAttributes());
	TextAttributesKey BLOCK_COMMENT = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".BlockComment", SyntaxHighlighterColors.DOC_COMMENT.getDefaultAttributes());
	TextAttributesKey STRING = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.String",
			SyntaxHighlighterColors.STRING.getDefaultAttributes());
	TextAttributesKey NUMBER = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Number",
			SyntaxHighlighterColors.NUMBER.getDefaultAttributes());
	TextAttributesKey DATA_TYPE = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.DataType",
			SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
	TextAttributesKey ALIAS = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Alias",
			HighlighterColors.TEXT.getDefaultAttributes());
	TextAttributesKey IDENTIFIER = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".Identifier", HighlighterColors.TEXT.getDefaultAttributes());
	TextAttributesKey QUOTED_IDENTIFIER = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".QuotedIdentifier", HighlighterColors.TEXT.getDefaultAttributes());
	TextAttributesKey KEYWORD = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Keyword",
			SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
	TextAttributesKey FUNCTION = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Function",
			SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES.toTextAttributes());
	TextAttributesKey PARAMETER = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".Parameter", SyntaxHighlighterColors.KEYWORD.getDefaultAttributes());
	TextAttributesKey OPERATOR = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Operator",
			SyntaxHighlighterColors.OPERATION_SIGN.getDefaultAttributes());
	TextAttributesKey PARENTHESIS = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".Parenthesis", SyntaxHighlighterColors.PARENTHS.getDefaultAttributes());
	TextAttributesKey BRACKET = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Brackets",
			SyntaxHighlighterColors.BRACKETS.getDefaultAttributes());
	TextAttributesKey UNKNOWN_IDENTIFIER = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".UnknownIdentifier", new TextAttributes(Color.RED, null, null, null, 0));
	TextAttributesKey CHAMELEON = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL" +
			".Chameleon", new TextAttributes(null, null, null, null, 0));
	TextAttributesKey VARIABLE = TextAttributesKey.createTextAttributesKey("DBNavigator.TextAttributes.SQL.Variable",
			CodeInsightColors.INSTANCE_FIELD_ATTRIBUTES.getDefaultAttributes());
	TextAttributesKey BAD_CHARACTER = HighlighterColors.BAD_CHARACTER;
}