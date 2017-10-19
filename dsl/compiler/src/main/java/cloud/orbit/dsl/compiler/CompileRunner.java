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

package cloud.orbit.dsl.compiler;

import cloud.orbit.dsl.compiler.pass.SecondPass;
import cloud.orbit.dsl.compiler.exception.CompilerException;
import cloud.orbit.dsl.compiler.core.CompilationContext;
import cloud.orbit.dsl.compiler.pass.FirstPass;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompileRunner {
    public static void buildDirectory(final String inputDirectory, final String outputDirectory) {
        try {
            final Path inputPath = Paths.get(inputDirectory);
            final Path outputPath = Paths.get(outputDirectory);
            buildDirectory(inputPath, outputPath);
        }
        catch(CompilerException ce) {
            throw ce;
        }
        catch(Throwable e) {
            throw new CompilerException("Unknown compiler error", e);
        }
    }

    public static void buildDirectory(final Path inputDirectory, final Path outputDirectory) {
        List<Path> inputFiles = null;

        try {
            inputFiles = Files
                    .walk(inputDirectory)
                    .filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        } catch(Throwable e) {
            throw new CompilerException("Error gathering input files", e);
        }

        if(!Files.isDirectory(outputDirectory)) {
            throw new CompilerException("Output directory must be a directory. '" + outputDirectory + "'");
        }

        buildInternal(inputFiles, outputDirectory);
    }

    public static void buildFile(final String inputFile, final String outputDirectory) {
        try {
            final Path inputPath = Paths.get(inputFile);
            final Path outputPath = Paths.get(outputDirectory);
            buildFile(inputPath, outputPath);
        }
        catch(CompilerException ce) {
            throw ce;
        }
        catch(Throwable e) {
            throw new CompilerException("Unknown compiler error", e);
        }
    }

    public static void buildFile(final Path inputFile, final Path outputDirectory) {
        File file = inputFile.toFile();

        if(!file.exists() || !file.isFile()) {
            String abs = file.getAbsolutePath();
            throw new CompilerException("Input file does not exist. '" + inputFile.toString() + "'.");
        }

        buildInternal(Collections.singletonList(inputFile), outputDirectory);
    }

    public static void testRun(final String inputFile) {
        try {
            final Path inputPath = Paths.get(inputFile);
            buildFile(inputPath, null);
        }
        catch(CompilerException ce) {
            throw ce;
        }
        catch(Throwable e) {
            throw new CompilerException("Unknown compiler error", e);
        }
    }

    private static void buildInternal(final List<Path> inputFiles, final Path outputDirectory) {
        final CompilationContext compilationContext = new CompilationContext();
        compilationContext.setInputFiles(inputFiles);
        compilationContext.setOutputDirectory(outputDirectory);

        final FirstPass firstPass = new FirstPass();
        firstPass.compile(compilationContext);

        final SecondPass secondPass = new SecondPass();
        secondPass.compile(compilationContext);

    }


}
