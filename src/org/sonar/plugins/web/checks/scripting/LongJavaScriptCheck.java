/*
 * SonarQube Web Plugin
 * Copyright (C) 2010 SonarSource and Matthijs Galesloot
 * dev@sonar.codehaus.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sonar.plugins.web.checks.scripting;

import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.plugins.web.checks.AbstractPageCheck;
import org.sonar.plugins.web.node.TagNode;
import org.sonar.plugins.web.node.TextNode;

/**
 * Checker to find long javascripts.
 *
 * @see http://pmd.sourceforge.net/rules/basic-jsp.html
 */
@Rule(
  key = "LongJavaScriptCheck",
  priority = Priority.CRITICAL)
public class LongJavaScriptCheck extends AbstractPageCheck {

  private static final int DEFAULT_MAX_LINES = 5;
  private static final int  DEFAULT_MAX_LENGTH=300;
  @RuleProperty(
    key = "maxLines",
    defaultValue = "" + DEFAULT_MAX_LINES)
  public int maxLines = DEFAULT_MAX_LINES;

  
  @RuleProperty(
		    key = "maxLength",
		    defaultValue = "" + DEFAULT_MAX_LENGTH)  
  public int maxLength = DEFAULT_MAX_LENGTH;
  
  
  private int linesOfCode;
  private int lengthOfCode;
  private TagNode scriptNode;

  @Override
  public void characters(TextNode textNode) {
    if (scriptNode != null) {
      linesOfCode += textNode.getLinesOfCode();
      lengthOfCode+=textNode.getCode().length();
       
      int start=scriptNode.getStartLinePosition();
      if (linesOfCode > maxLines) {
        createViolation(start, 
        		"The line length of this JS script (" + linesOfCode
        		+ ") exceeds the maximum set to " + maxLines + ".",textNode);
        scriptNode = null;
        linesOfCode = 0;
        lengthOfCode=0;
      }
      
      if (lengthOfCode > maxLength) {
          createViolation(start, 
          		"The length of this JS script (" + maxLength
          		+ ") exceeds the maximum set to " + maxLength + ".",textNode);
          scriptNode = null;
          linesOfCode = 0;
          lengthOfCode=0;
        }
      
    }
  }

  @Override
  public void endElement(TagNode element) {
    scriptNode = null;
    linesOfCode = 0;
    lengthOfCode=0;
  }

  @Override
  public void startElement(TagNode element) {
    if ("script".equalsIgnoreCase(element.getNodeName())) {
      scriptNode = element;
      linesOfCode = 0;
      lengthOfCode=0;
    }
  }

}
