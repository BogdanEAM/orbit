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

package cloud.orbit.dsl.compiler.test;

import cloud.orbit.dsl.compiler.CompileRunner;
import cloud.orbit.dsl.compiler.exception.CompilerException;
import cloud.orbit.dsl.compiler.exception.EnumDuplicateFieldException;
import cloud.orbit.dsl.compiler.exception.EnumMissingZeroTagException;
import cloud.orbit.dsl.compiler.exception.ParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnumTests {

    @Test
    public void duplicateFieldNameException() {
        Assertions.assertThrows(EnumDuplicateFieldException.class, () -> {
            CompileRunner.testRun("src/test/resources/enum/duplicateFieldName.opf");
        });
    }

    @Test
    public void missingZeroTagException() {
        Assertions.assertThrows(EnumMissingZeroTagException.class, () -> {
            CompileRunner.testRun("src/test/resources/enum/missingZeroTag.opf");
        });
    }

    @Test
    public void reservedEnumNameException() {
        Assertions.assertThrows(ParserException.class, () -> {
            CompileRunner.testRun("src/test/resources/enum/reservedEnumName.opf");
        });
    }

    @Test
    public void duplicatedTagAllowed() {
        CompileRunner.testRun("src/test/resources/enum/duplicateTag.opf");
    }
}
