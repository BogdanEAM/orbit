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

import cloud.orbit.dsl.compiler.core.CompilationContext;
import cloud.orbit.dsl.compiler.core.Util;
import cloud.orbit.dsl.compiler.exception.CompilerException;
import cloud.orbit.dsl.parser.OrbitDSLParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumDeclaration extends TopLevelDeclaration {

    final OrbitDSLParser.EnumDeclarationContext edc;


    private String enumName;
    private Map<String, Integer> enumFields = new HashMap<>();

    public EnumDeclaration(final OrbitDSLParser.EnumDeclarationContext edc, final String packagePath,
                           final List<String> importedPackages) {
        super(packagePath, importedPackages);
        this.edc = edc;

    }

    @Override
    public void compile(final CompilationContext compilationContext) {
        enumName = edc.Identifier().getText();

        if(edc.enumBody().enumFields() != null && edc.enumBody().enumFields().enumField() != null) {
            edc.enumBody().enumFields().enumField().forEach((field) -> {
                final String fieldIdent = field.Identifier().getText();
                final Integer fieldTag = Integer.parseInt(field.IntegerLiteral().getText());
                if (enumFields.putIfAbsent(fieldIdent, fieldTag) != null) {
                    throw new CompilerException("Can not have two enum fields with the same identifier. '" +
                            Util.producePackageString(getPackagePath(), enumName, fieldIdent) + "'.");
                }

            });
        }

        if(enumFields.values().stream().filter(x -> x == 0).count() == 0) {
            throw new CompilerException("Must define a 0 tag in an enum. '" +
                    Util.producePackageString(getPackagePath(), enumName) + "'.");
        }

        compilationContext.getUntypedResults().putDeclaration(
                Util.producePackageString(getPackagePath(), enumName), this);
    }

    @Override
    public String getShortName() {
        return enumName;
    }
}
