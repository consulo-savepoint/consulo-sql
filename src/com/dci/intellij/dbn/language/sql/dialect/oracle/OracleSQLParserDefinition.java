package com.dci.intellij.dbn.language.sql.dialect.oracle;

import org.jetbrains.annotations.NotNull;
import com.dci.intellij.dbn.language.sql.SQLParserDefinition;
import com.intellij.lang.LanguageVersion;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;


public class OracleSQLParserDefinition extends SQLParserDefinition {

    public OracleSQLParserDefinition(OracleSQLParser parser) {
        super(parser);
    }

    @NotNull
    public Lexer createLexer(Project project, LanguageVersion languageVersion) {
        return new FlexAdapter(new OracleSQLParserFlexLexer(getTokenTypes()));
    }
}