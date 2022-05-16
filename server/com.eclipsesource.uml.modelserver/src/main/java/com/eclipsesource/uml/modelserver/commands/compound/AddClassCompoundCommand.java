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
package com.eclipsesource.uml.modelserver.commands.compound;

import java.util.function.Supplier;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.uml2.uml.PackageableElement;

import com.eclipsesource.uml.modelserver.commands.notation.AddPackageableShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.AddClassCommand;

public class AddClassCompoundCommand extends CompoundCommand {

   public AddClassCompoundCommand(final EditingDomain domain, final URI modelUri, final GPoint classPosition) {

      // Chain semantic and notation command
      AddClassCommand command = new AddClassCommand(domain, modelUri);
      this.append(command);
      Supplier<PackageableElement> semanticResultSupplier = () -> command.getNewClass();
      this.append(new AddPackageableShapeCommand(domain, modelUri, classPosition, semanticResultSupplier));
   }

}
