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

package cloud.orbit.dsl.compiler.untyped;

import cloud.orbit.dsl.compiler.exception.CompilerException;
import cloud.orbit.dsl.compiler.exception.ParserException;
import cloud.orbit.dsl.parser.OrbitDSLLexer;
import cloud.orbit.dsl.parser.OrbitDSLParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationUnit {
    final private Path filePath;
    private String rawFile;
    private OrbitDSLLexer dslLexer;
    private CommonTokenStream commonTokenStream;
    private OrbitDSLParser dslParser;
    private OrbitDSLParser.CompilationUnitContext cuContext;

    public CompilationUnit(final Path filePath) {
        this.filePath = filePath;
        init();
    }

    private void init() {
        try {
            this.rawFile = new String(Files.readAllBytes(filePath));

        } catch(final IOException e) {
            throw new CompilerException("Error opening file", e);
        }

        try {
            this.dslLexer = new OrbitDSLLexer(CharStreams.fromString(rawFile));
            this.commonTokenStream = new CommonTokenStream(dslLexer);
            this.dslParser = new OrbitDSLParser(commonTokenStream);
            dslParser.setErrorHandler(new BailErrorStrategy());
            this.cuContext = this.dslParser.compilationUnit();
        } catch(ParseCancellationException e) {
            RecognitionException re = (RecognitionException) e.getCause();
            re.getMessage();

            throw new ParserException(filePath.toString(), re.getOffendingToken().getLine(),
                    re.getOffendingToken().getCharPositionInLine(), re.getOffendingToken().getText());

        }
    }

    public OrbitDSLParser.CompilationUnitContext getParsedData() {
        return cuContext;
    }
}
