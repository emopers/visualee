package de.strullerbaumann.visualee.examiner.cdi;

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
import de.strullerbaumann.visualee.dependency.entity.DependencyType;
import de.strullerbaumann.visualee.examiner.Examiner;
import de.strullerbaumann.visualee.source.entity.JavaSource;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Thomas Struller-Baumann <thomas at struller-baumann.de>
 */
public class ExaminerResource extends Examiner {

   @Override
   protected boolean isRelevantType(DependencyType type) {
      return Arrays.asList(DependencyType.RESOURCE).contains(type);
   }

   @Override
   protected DependencyType getTypeFromToken(String token) {
      DependencyType type = null;
      if (token.indexOf("@Resource(") > -1 || "@Resource".equals(token)) {
         type = DependencyType.RESOURCE;
      }
      return type;
   }

   @Override
   public void examineDetail(JavaSource javaSource, Scanner scanner, String currentToken, DependencyType type) {
      String token = currentToken;
      if (token.indexOf('(') > - 1) {
         token = scanAfterClosedParenthesis(token, scanner);
      }
      token = jumpOverJavaToken(token, scanner);
      token = extractClassInstanceOrEvent(token);
      String className = cleanupGeneric(token);
      createDependency(className, type, javaSource);
   }
}
