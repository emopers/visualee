package de.strullerbaumann.visualee.source.entity;

/*
 * #%L
 * visualee
 * %%
 * Copyright (C) 2013 Thomas Struller-Baumann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import de.strullerbaumann.visualee.logging.LogProvider;
import java.io.IOException;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class JavaSource {

   private static final int HASH = 7;
   private static final int HASH_MULTIPLIER = 13;
   private Path javaFile;
   // for D3.js, links need id's from the nodes (id's start with 0)
   private int id;
   // Nodes form the same package have the same group-number
   private int group;
   private String packagePath;
   private String sourceCode;
   private String name;

   public JavaSource(Path javaFile) {
      this.javaFile = javaFile;
      this.name = javaFile.getFileName().toString().substring(0, javaFile.getFileName().toString().indexOf(".java"));
      sourceCode = "";
   }

   public JavaSource(String name) {
      this.name = name;
      sourceCode = "Not available";
   }

   public Path getJavaFile() {
      return javaFile;
   }

   public int getId() {
      return id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public int getGroup() {
      return group;
   }

   public void setGroup(int group) {
      this.group = group;
   }

   public String getPackagePath() {
      return packagePath;
   }

   public void setPackagePath(String packagePath) {
      this.packagePath = packagePath;
   }

   public String getSourceCode() {
      return sourceCode;
   }

   public String getSourceCodeWithoutComments() {
      Scanner scanner = new Scanner(sourceCode);
      scanner.useDelimiter("[\n]+");
      StringBuilder sourceCodeWithoutComments = new StringBuilder();
      boolean isInCommentBlock = false;
      while (scanner.hasNext()) {
         String token = scanner.next();
         if (token.trim().startsWith("/*")) {
            isInCommentBlock = true;
         }
         if (!token.trim().startsWith("//") && !isInCommentBlock) {
            sourceCodeWithoutComments.append(token);
            sourceCodeWithoutComments.append("\n");
         }
         if (token.trim().startsWith("*/")) {
            isInCommentBlock = false;
         }
      }

      return sourceCodeWithoutComments.toString();
   }

   public String getEscapedSourceCode() {
      // &lt; and &gt; are important, e.g. a sourcecode like "List<Scripts> ..." causes problems with the javascript in the ui
      return sourceCode.replace("<", "&lt;").replace(">", "&gt;");
   }

   public void setSourceCode(String sourceCode) {
      this.sourceCode = sourceCode;
   }

   @Override
   public String toString() {
      return getName();
   }

   public String getFullClassName() {
      return packagePath + "." + name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void loadSourceCode() {
      if (this.getJavaFile() == null) {
         return;
      }
      Path pathJavaSource = this.getJavaFile();
      StringBuilder loadedSourceCode = new StringBuilder();
      try {
         List<String> sourceCodeLines = Files.readAllLines(pathJavaSource, UTF_8);
         for (String sourceCodeLine : sourceCodeLines) {
            loadedSourceCode.append(sourceCodeLine).append('\n');
         }
      } catch (IOException ex) {
         LogProvider.getInstance().error("Problems while reading " + this.getJavaFile(), ex);
      }
      setSourceCode(loadedSourceCode.toString());
   }

   @Override
   public int hashCode() {
      return HASH_MULTIPLIER * HASH + Objects.hashCode(this.name);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final JavaSource other = (JavaSource) obj;
      return Objects.equals(this.name, other.name);
   }
}
