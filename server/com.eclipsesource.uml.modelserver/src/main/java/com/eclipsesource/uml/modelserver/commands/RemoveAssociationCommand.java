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

public class RemoveAssociationCommand extends CompoundCommand {

   public RemoveAssociationCommand(final EditingDomain domain, final URI modelUri, final String semanticUriFragment) {
      this.append(new RemoveAssociationElementCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveAssociationEdgeCommand(domain, modelUri, semanticUriFragment));
   }

}
