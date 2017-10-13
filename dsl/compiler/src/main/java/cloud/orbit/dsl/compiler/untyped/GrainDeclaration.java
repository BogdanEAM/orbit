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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrainDeclaration extends TopLevelDeclaration {
    final OrbitDSLParser.GrainDeclarationContext gdc;

    private String grainName;
    private List<EnumDeclaration> grainEmbeddedEnums = new ArrayList<>();
    private List<StructDeclaration> grainEmbeddedStructs = new ArrayList<>();
    private Map<String, MessageDeclaration> grainMessages = new HashMap<>();

    public GrainDeclaration(final OrbitDSLParser.GrainDeclarationContext gdc, final String packagePath, final List<String> importedPackages) {
        super(packagePath, importedPackages);
        this.gdc = gdc;

    }

    @Override
    public void compile(CompilationContext compilationContext) {
        grainName = gdc.Identifier().getText();

        gdc.grainBody().grainBodyDeclaration().forEach((gbd) -> {
            // Nested Enum
            if (gbd.enumDeclaration() != null) {
                final EnumDeclaration edc = new EnumDeclaration(gbd.enumDeclaration(),
                        Util.producePackageString(getPackagePath(), grainName), getImportedPackages());
                edc.compile(compilationContext);
                grainEmbeddedEnums.add(edc);
            }

            // Nested Struct
            if (gbd.structDeclaration() != null) {
                final StructDeclaration esdc = new StructDeclaration(gbd.structDeclaration(),
                        Util.producePackageString(getPackagePath(), grainName), getImportedPackages());
                esdc.compile(compilationContext);
                grainEmbeddedStructs.add(esdc);
            }

            // Messages
            if(gbd.messageDeclaration() != null) {
                final OrbitDSLParser.MessageDeclarationContext mdc = gbd.messageDeclaration();

                final MessageDeclaration messageDeclaration = new MessageDeclaration();
                messageDeclaration.setMessageType(mdc.type().getText());
                messageDeclaration.setMessageName(mdc.Identifier().getText());

                if(mdc.messageFields() != null && mdc.messageFields().messageField() != null) {
                    mdc.messageFields().messageField().forEach((mf) -> {
                        final StructField structField = new StructField();
                        structField.setFieldName(mf.Identifier().getText());
                        structField.setFieldType(mf.type().getText());
                        structField.setFieldTag(Integer.parseInt(mf.IntegerLiteral().getText()));
                        messageDeclaration.addField(structField);
                    });
                }

                if (messageDeclaration.getMessageFields().values().stream().map((x) -> x.getFieldTag()).distinct().count() < messageDeclaration.getMessageFields().size()) {
                    throw new CompilerException("All message struct field tags must be unique. '" +
                            Util.producePackageString(getPackagePath(), grainName, messageDeclaration.getMessageName()) + "'.");
                }


                if(grainMessages.putIfAbsent(messageDeclaration.getMessageName(), messageDeclaration) != null) {
                    throw new CompilerException("Grain messages must have unique identifiers. '" +
                        Util.producePackageString(getPackagePath(), grainName, messageDeclaration.getMessageName()) + "'.");
                }
            }
        });

        compilationContext.getUntypedResults().putDeclaration(Util.producePackageString(getPackagePath(), grainName), this);
    }

    @Override
    public String getShortName() {
        return grainName;
    }
}
