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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddClassElementCommand extends UmlSemanticElementCommand {

   protected final Class newClass;

   public AddClassElementCommand(final EditingDomain domain, final URI modelUri, final Class newClass) {
      super(domain, modelUri);
      this.newClass = newClass;
   }

   public AddClassElementCommand(final EditingDomain domain, final URI modelUri) {
      this(domain, modelUri, UMLFactory.eINSTANCE.createClass());
   }

   @Override
   protected void doExecute() {
      newClass.setName(UmlSemanticCommandUtil.getNewClassName(umlModel));
      umlModel.getPackagedElements().add(newClass);
   }

}
