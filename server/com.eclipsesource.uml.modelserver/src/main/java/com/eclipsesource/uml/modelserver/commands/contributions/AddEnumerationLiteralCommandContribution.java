/********************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
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

import com.eclipsesource.uml.modelserver.commands.semantic.AddEnumerationLiteralCommand;

public class AddEnumerationLiteralCommandContribution extends UmlSemanticCommandContribution {

   public static final String TYPE = "addEnumerationLiteralContributuion";

   public static CCommand create(final String parentSemanticUri) {
      CCommand addEnumerationLiteralCommand = CCommandFactory.eINSTANCE.createCommand();
      addEnumerationLiteralCommand.setType(TYPE);
      addEnumerationLiteralCommand.getProperties().put(PARENT_SEMANTIC_URI_FRAGMENT, parentSemanticUri);
      return addEnumerationLiteralCommand;
   }

   @Override
   protected Command toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      String parentSemanticUriFragment = command.getProperties().get(PARENT_SEMANTIC_URI_FRAGMENT);
      return new AddEnumerationLiteralCommand(domain, modelUri, parentSemanticUriFragment);
   }

}
