/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.javascript.checks;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.CheckForNull;
import org.sonar.check.Rule;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.Tree.Kind;
import org.sonar.plugins.javascript.api.tree.expression.BinaryExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.expression.IdentifierTree;
import org.sonar.plugins.javascript.api.visitors.SubscriptionVisitorCheck;

@Rule(key = "S2688")
public class ComparisonWithNaNCheck extends SubscriptionVisitorCheck {

  private static final String MESSAGE = "Use a test of the format \"a %s a\" instead.";

  @Override
  public List<Kind> nodesToVisit() {
    return ImmutableList.of(
      Kind.EQUAL_TO,
      Kind.NOT_EQUAL_TO,
      Kind.STRICT_EQUAL_TO,
      Kind.STRICT_NOT_EQUAL_TO);
  }

  @Override
  public void visitNode(Tree tree) {
    BinaryExpressionTree expression = (BinaryExpressionTree) tree;
    ExpressionTree nan = getNaN(expression);

    if (nan != null) {
      addIssue(nan, String.format(MESSAGE, expression.operator().text()))
        .secondary(expression.operator());
    }
  }

  private static boolean isNaN(ExpressionTree expression) {
    return expression.is(Kind.IDENTIFIER_REFERENCE) && "NaN".equals(((IdentifierTree) expression).name());
  }

  @CheckForNull
  private static ExpressionTree getNaN(BinaryExpressionTree expression) {
    if (isNaN(expression.leftOperand())) {
      return expression.leftOperand();
    } else if (isNaN(expression.rightOperand())) {
      return expression.rightOperand();
    }

    return null;
  }

}
