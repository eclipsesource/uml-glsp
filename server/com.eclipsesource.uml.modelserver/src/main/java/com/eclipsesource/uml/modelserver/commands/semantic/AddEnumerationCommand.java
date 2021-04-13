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
package com.eclipsesource.uml.modelserver.commands.semantic;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddEnumerationCommand extends UmlSemanticElementCommand {

   protected final Enumeration newEnumeration;

   public AddEnumerationCommand(final EditingDomain domain, final URI modelUri,
      final Enumeration newEnumeration) {
      super(domain, modelUri);
      this.newEnumeration = newEnumeration;
   }

   public AddEnumerationCommand(final EditingDomain domain, final URI modelUri) {
      this(domain, modelUri, UMLFactory.eINSTANCE.createEnumeration());
   }

   @Override
   protected void doExecute() {
      newEnumeration.setName(UmlSemanticCommandUtil.getNewEnumerationName(umlModel));
      umlModel.getPackagedElements().add(newEnumeration);
   }

}
