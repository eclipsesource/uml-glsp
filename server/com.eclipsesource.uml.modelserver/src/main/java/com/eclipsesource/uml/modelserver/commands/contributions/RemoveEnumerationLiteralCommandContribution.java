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
package com.eclipsesource.uml.modelserver.commands.contributions;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;

import com.eclipsesource.uml.modelserver.commands.semantic.RemoveEnumerationLiteralCommand;

public class RemoveEnumerationLiteralCommandContribution extends UmlSemanticCommandContribution {

   public static final String TYPE = "removeEnumerationLiteral";

   public static CCommand create(final String parentSemanticUri, final String semanticUri) {
      CCommand removeLiteralCommand = CCommandFactory.eINSTANCE.createCommand();
      removeLiteralCommand.setType(TYPE);
      removeLiteralCommand.getProperties().put(PARENT_SEMANTIC_URI_FRAGMENT, parentSemanticUri);
      removeLiteralCommand.getProperties().put(SEMANTIC_URI_FRAGMENT, semanticUri);
      return removeLiteralCommand;
   }

   @Override
   protected Command toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String parentSemanticUri = command.getProperties().get(PARENT_SEMANTIC_URI_FRAGMENT);
      String semanticUri = command.getProperties().get(SEMANTIC_URI_FRAGMENT);

      return new RemoveEnumerationLiteralCommand(domain, modelUri, parentSemanticUri, semanticUri);
   }

}
