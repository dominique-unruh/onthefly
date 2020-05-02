package de.unruh.onthefly.unsorted;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import de.unruh.onthefly.unsorted.psi.SimpleTypes;
import com.intellij.psi.TokenType;

%%

%class SimpleLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

CRLF=\R
WHITE_SPACE=[\ \n\t\f]
// FIRST_VALUE_CHARACTER=[^ \n\f\\] | "\\"{CRLF} | "\\".
// VALUE_CHARACTER=[^\n\f\\] | "\\"{CRLF} | "\\".
END_OF_LINE_COMMENT="#" [^\r\n]*
ASSIGN=[:=]
IDENTIFIER=[a-z]+[a-z0-9_]*
TIMES = "*"
PLUS = "+"
MINUS = "-"
DIV = "/"
SQRT = "sqrt"
INT = [0-9]+

//%state WAITING_VALUE

%%

<YYINITIAL> {END_OF_LINE_COMMENT}                           { yybegin(YYINITIAL); return SimpleTypes.COMMENT; }

<YYINITIAL> {SQRT}                                    { yybegin(YYINITIAL); return SimpleTypes.SQRT; }
<YYINITIAL> {IDENTIFIER}                                    { yybegin(YYINITIAL); return SimpleTypes.IDENTIFIER; }
<YYINITIAL> {INT}                                    { yybegin(YYINITIAL); return SimpleTypes.INT; }

<YYINITIAL> {ASSIGN}                                     { yybegin(YYINITIAL); return SimpleTypes.ASSIGN; }

<YYINITIAL> {CRLF}                                          { yybegin(YYINITIAL); return SimpleTypes.CRLF; }

<YYINITIAL> {WHITE_SPACE}+                                  { yybegin(YYINITIAL); return TokenType.WHITE_SPACE; }

<YYINITIAL> {TIMES}                     { yybegin(YYINITIAL); return SimpleTypes.TIMES; }
<YYINITIAL> {MINUS}                     { yybegin(YYINITIAL); return SimpleTypes.MINUS; }
<YYINITIAL> {PLUS}                     { yybegin(YYINITIAL); return SimpleTypes.PLUS; }
<YYINITIAL> {DIV}                     { yybegin(YYINITIAL); return SimpleTypes.DIV; }

[^]                                                         { return TokenType.BAD_CHARACTER; }
