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
package com.eclipsesource.uml.modelserver.commands;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.UMLFactory;

public class AddAssociationCommand extends CompoundCommand {

   public AddAssociationCommand(final EditingDomain domain, final URI modelUri, final String sourceClassUriFragment,
      final String targetClassUriFragment) {

      Association newAssociation = UMLFactory.eINSTANCE.createAssociation();
      this.append(new AddAssociationElementCommand(domain, modelUri, newAssociation, sourceClassUriFragment,
         targetClassUriFragment));
      this.append(new AddAssociationEdgeCommand(domain, modelUri, newAssociation));
   }

}
