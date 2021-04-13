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
package com.eclipsesource.uml.modelserver.commands.compound;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.notation.AddEnumerationShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.AddEnumerationCommand;

public class AddEnumerationCompoundCommand extends CompoundCommand {

   public AddEnumerationCompoundCommand(final EditingDomain domain, final URI modelUri, final GPoint enumerationPosition) {

      Enumeration newEnumeration = UMLFactory.eINSTANCE.createEnumeration();
      this.append(new AddEnumerationCommand(domain, modelUri, newEnumeration));
      this.append(new AddEnumerationShapeCommand(domain, modelUri, newEnumeration, enumerationPosition));
   }

}
