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

import java.util.*;

public class StructDeclaration extends TopLevelDeclaration {
    final OrbitDSLParser.StructDeclarationContext sdc;

    private String structName;
    private List<EnumDeclaration> structEmbeddedEnums = new ArrayList<>();
    private List<StructDeclaration> structEmbeddedStructs = new ArrayList<>();
    private Map<String, StructField> structFields = new HashMap<>();

    public StructDeclaration(final OrbitDSLParser.StructDeclarationContext sdc, final String packagePath,
                             final List<String> importedPackages) {
        super(packagePath, importedPackages);
        this.sdc = sdc;
    }

    @Override
    public void compile(final CompilationContext compilationContext) {
        structName = sdc.Identifier().getText();

        sdc.structBody().structBodyDeclaration().forEach((sbd) -> {
            // Nested Enum
            if (sbd.enumDeclaration() != null) {
                final EnumDeclaration edc = new EnumDeclaration(sbd.enumDeclaration(),
                        Util.producePackageString(getPackagePath(), structName), getImportedPackages());
                edc.compile(compilationContext);
                structEmbeddedEnums.add(edc);
            }

            // Nested Struct
            if (sbd.structDeclaration() != null) {
                final StructDeclaration esdc = new StructDeclaration(sbd.structDeclaration(),
                        Util.producePackageString(getPackagePath(), structName), getImportedPackages());
                esdc.compile(compilationContext);
                structEmbeddedStructs.add(esdc);
            }

            // Struct Field
            if (sbd.structField() != null) {
                final OrbitDSLParser.StructFieldContext sfc = sbd.structField();
                final StructField structField = new StructField();
                structField.setFieldType(sfc.type().getText());
                structField.setFieldName(sfc.Identifier().getText());
                structField.setFieldTag(Integer.parseInt(sfc.IntegerLiteral().getText()));
                if (structFields.putIfAbsent(structField.getFieldName(), structField) != null) {
                    throw new CompilerException("Field with that name already exists. '" +
                            Util.producePackageString(getPackagePath(), structName, structField.getFieldName()) + "'.");
                }
            }
        });

        if (structFields.values().stream().map((x) -> x.getFieldTag()).distinct().count() < structFields.size()) {
            throw new CompilerException("All struct field tags must be unique. '" +
                    Util.producePackageString(getPackagePath(), structName) + "'.");
        }

        compilationContext.getUntypedResults().putDeclaration(Util.producePackageString(getPackagePath(), structName), this);
    }

    @Override
    public String getShortName() {
        return structName;
    }
}
