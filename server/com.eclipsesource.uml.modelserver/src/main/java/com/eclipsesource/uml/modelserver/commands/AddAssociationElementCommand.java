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
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.UMLFactory;

import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class AddAssociationElementCommand extends UmlSemanticElementCommand {

   protected final Association newAssociation;
   protected final Class sourceClass;
   protected final Class targetClass;

   public AddAssociationElementCommand(final EditingDomain domain, final URI modelUri,
      final String sourceClassUriFragment, final String targetClassUriFragment) {
      this(domain, modelUri, UMLFactory.eINSTANCE.createAssociation(), sourceClassUriFragment, targetClassUriFragment);
   }

   public AddAssociationElementCommand(final EditingDomain domain, final URI modelUri,
      final Association newAssociation, final String sourceClassUriFragment, final String targetClassUriFragment) {
      super(domain, modelUri);
      this.newAssociation = newAssociation;
      this.sourceClass = UmlSemanticCommandUtil.getElement(umlModel, sourceClassUriFragment, Class.class);
      this.targetClass = UmlSemanticCommandUtil.getElement(umlModel, targetClassUriFragment, Class.class);
   }

   @Override
   protected void doExecute() {
      newAssociation.createOwnedEnd(UmlSemanticCommandUtil.getNewAssociationEndName(sourceClass), sourceClass);
      newAssociation.createOwnedEnd(UmlSemanticCommandUtil.getNewAssociationEndName(targetClass), targetClass);
      umlModel.getPackagedElements().add(newAssociation);
   }

}
