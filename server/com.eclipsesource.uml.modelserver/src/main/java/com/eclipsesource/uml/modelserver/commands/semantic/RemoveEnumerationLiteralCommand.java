/********************************************************************************
 * Copyright (c) 2021 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ********************************************************************************/
package com.eclipsesource.uml.modelserver.commands.semantic;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveEnumerationLiteralCommand extends UmlSemanticElementCommand {

   protected final String parentSemanticUriFragment;
   protected final String semanticUriFragment;

   public RemoveEnumerationLiteralCommand(final EditingDomain domain, final URI modelUri,
      final String parentSemanticUriFragment,
      final String semanticUriFragment) {
      super(domain, modelUri);
      this.parentSemanticUriFragment = parentSemanticUriFragment;
      this.semanticUriFragment = semanticUriFragment;
   }

   @Override
   protected void doExecute() {
      Enumeration parentEnumeration = UmlSemanticCommandUtil.getElement(umlModel, parentSemanticUriFragment,
         Enumeration.class);
      EnumerationLiteral literalToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment,
         EnumerationLiteral.class);
      parentEnumeration.getOwnedLiterals().remove(literalToRemove);
   }

}
