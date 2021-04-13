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
package com.eclipsesource.uml.modelserver.commands.notation;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.uml2.uml.Enumeration;

import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;
import com.eclipsesource.uml.modelserver.unotation.SemanticProxy;
import com.eclipsesource.uml.modelserver.unotation.Shape;
import com.eclipsesource.uml.modelserver.unotation.UnotationFactory;

public class AddEnumerationShapeCommand extends UmlNotationElementCommand {

   protected final GPoint shapePosition;
   protected String semanticProxyUri;
   protected Enumeration newEnumeration;

   public AddEnumerationShapeCommand(final EditingDomain domain, final URI modelUri, final GPoint position) {
      super(domain, modelUri);
      this.shapePosition = position;
      this.newEnumeration = null;
      this.semanticProxyUri = null;
   }

   public AddEnumerationShapeCommand(final EditingDomain domain, final URI modelUri, final GPoint position,
      final String semanticProxyUri) {
      this(domain, modelUri, position);
      this.semanticProxyUri = semanticProxyUri;
   }

   public AddEnumerationShapeCommand(final EditingDomain domain, final URI modelUri, final Enumeration newEnumeration,
      final GPoint position) {
      this(domain, modelUri, position);
      this.newEnumeration = newEnumeration;
   }

   @Override
   protected void doExecute() {
      Shape newShape = UnotationFactory.eINSTANCE.createShape();
      newShape.setPosition(this.shapePosition);

      SemanticProxy proxy = UnotationFactory.eINSTANCE.createSemanticProxy();
      if (this.semanticProxyUri != null && this.newEnumeration == null) {
         proxy.setUri(this.semanticProxyUri);
      } else {
         proxy.setUri(UmlNotationCommandUtil.getSemanticProxyUri(newEnumeration));
      }
      newShape.setSemanticElement(proxy);

      umlDiagram.getElements().add(newShape);
   }

}
