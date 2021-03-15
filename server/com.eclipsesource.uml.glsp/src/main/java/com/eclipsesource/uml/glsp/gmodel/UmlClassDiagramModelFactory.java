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
package com.eclipsesource.uml.glsp.gmodel;

import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Relationship;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.unotation.Diagram;

public class UmlClassDiagramModelFactory extends GModelFactory {

   public UmlClassDiagramModelFactory(final UmlModelState modelState) {
      super(modelState);
   }

   @Override
   public GModelElement create(final EObject semanticElement) {
      GModelElement result = null;
      if (semanticElement instanceof Model) {
         result = create(semanticElement);
      } else if (semanticElement instanceof Class) {
         result = classifierNodeFactory.create((Class) semanticElement);
      } else if (semanticElement instanceof Relationship) {
         result = relationshipEdgeFactory.create((Relationship) semanticElement);
      } else if (semanticElement instanceof NamedElement) {
         result = labelFactory.create((NamedElement) semanticElement);
      }
      if (result == null) {
         throw createFailed(semanticElement);
      }
      return result;
   }

   @Override
   public GGraph create(final Diagram umlDiagram) {
      GGraph graph = getOrCreateRoot();

      if (umlDiagram.getSemanticElement().getResolvedElement() != null) {
         Model umlModel = (Model) umlDiagram.getSemanticElement().getResolvedElement();

         graph.setId(toId(umlModel));

         graph.getChildren().addAll(umlModel.getPackagedElements().stream()//
            .filter(Class.class::isInstance)//
            .map(Class.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));

         graph.getChildren().addAll(umlModel.getPackagedElements().stream() //
            .filter(Association.class::isInstance)//
            .map(Association.class::cast)//
            .map(this::create)//
            .collect(Collectors.toList()));
      }
      return graph;

   }

}
