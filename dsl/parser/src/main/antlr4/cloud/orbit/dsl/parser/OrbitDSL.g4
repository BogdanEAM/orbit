/*
 Copyright (C) 2017 Electronic Arts Inc.  All rights reserved.
 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1.  Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2.  Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3.  Neither the name of Electronic Arts, Inc. ("EA") nor the names of
     its contributors may be used to endorse or promote products derived
     from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY ELECTRONIC ARTS AND ITS CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL ELECTRONIC ARTS OR ITS CONTRIBUTORS BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

grammar OrbitDSL;

compilationUnit:
        packageDeclaration
        (importDeclaration | optionDeclaration)*
        topLeveltypeDeclaration*
        EOF
    ;

packageDeclaration: 'package' packageIdentifier ';';
importDeclaration: 'import' packageIdentifier ';';
optionDeclaration: 'option' Identifier '=' literal ';';
topLeveltypeDeclaration: grainDeclaration | structDeclaration | enumDeclaration;

grainDeclaration: 'grain' Identifier grainBody;
grainBody: '{' grainBodyDeclaration* '}';
grainBodyDeclaration: (messageDeclaration | structDeclaration | enumDeclaration);
messageDeclaration: type Identifier '(' messageFields? ');';
messageFields: messageField | messageField (',' messageField)*;
messageField: type Identifier '=' IntegerLiteral;

structDeclaration: 'struct' Identifier structBody;
structBody: '{' structBodyDeclaration* '}';
structBodyDeclaration: (structField | structDeclaration | enumDeclaration);
structField: type Identifier '=' IntegerLiteral ';';

enumDeclaration: 'enum' Identifier enumBody;
enumBody: '{' enumFields? '}';
enumFields: enumField | enumField (',' enumField)*;
enumField: Identifier '=' IntegerLiteral;

packageIdentifier: Identifier | packageIdentifier '.' Identifier;

literal: StringLiteral | IntegerLiteral | BooleanLiteral;
type: Identifier;

StringLiteral: '"' StringCharacters? '"';
IntegerLiteral: Digit+;
BooleanLiteral: 'true' | 'false';

Identifier: IdentValidFirstChar IdentValidTrailingChar*;

Whitespace: (Tab | Space | Newline) -> skip;
Comment: (LineComment | BlockComment) -> skip;

fragment IdentValidTrailingChar: LetterOrDigit | Underscore;
fragment IdentValidFirstChar: Letter | Underscore;

fragment StringCharacters: StringCharacter+;
fragment StringCharacter: ~["\\\r\n] | EscapeSequence;
fragment EscapeSequence: '\\' [btnfr"'\\];

fragment LetterOrDigit: Letter | Digit;
fragment Letter: LowercaseLetter | UppercaseLetter;
fragment LowercaseLetter: [a-z];
fragment UppercaseLetter: [A-Z];
fragment Digit: [0-9];
fragment Underscore: '_';

fragment Newline: ('\r'? '\n' | '\r');
fragment Space: ' ';
fragment Tab: '\t';

fragment LineComment: '//' ~[\r\n]*;
fragment BlockComment: '/*' .*? '*/';