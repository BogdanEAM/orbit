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

package cloud.orbit.dsl.compiler.pass;

import cloud.orbit.dsl.compiler.core.CompilationContext;
import cloud.orbit.dsl.compiler.exception.CompilerException;
import cloud.orbit.dsl.compiler.untyped.*;
import cloud.orbit.dsl.parser.OrbitDSLParser;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FirstPass {
    private CompilationContext compilationContext;

    public void compile(CompilationContext compilationContext) {
        this.compilationContext = compilationContext;
        compilationContext.getInputFiles().forEach(this::processFile);
    }

    private void processFile(final Path file) {
        final CompilationUnit compilationUnit = new CompilationUnit(file);
        final String cuPackage = compilationUnit.getParsedData().packageDeclaration().packageIdentifier().getText();
        final List<String> cuImports = compilationUnit.getParsedData().importDeclaration().stream().map((x) -> x.packageIdentifier().getText()).collect(Collectors.toList());

        compilationUnit.getParsedData().topLeveltypeDeclaration().forEach(
                (tldc) -> {
                    final TopLevelDeclaration tld = generateSpecificTypeContext(cuPackage, cuImports, tldc);
                    tld.compile(compilationContext);
                }
        );

    }

    private static TopLevelDeclaration generateSpecificTypeContext(final String packagePath, final List<String> importedPackages, final OrbitDSLParser.TopLeveltypeDeclarationContext tldc) {
        final OrbitDSLParser.GrainDeclarationContext gdc = tldc.grainDeclaration();
        final OrbitDSLParser.EnumDeclarationContext edc = tldc.enumDeclaration();
        final OrbitDSLParser.StructDeclarationContext sdc = tldc.structDeclaration();

        if(gdc != null) {
            return new GrainDeclaration(gdc, packagePath, importedPackages);
        } else if(edc != null) {
            return new EnumDeclaration(edc, packagePath, importedPackages);
        } else if (sdc != null) {
            return new StructDeclaration(sdc, packagePath, importedPackages);
        }

        throw new CompilerException("Unknown declaration type");
    }

}
