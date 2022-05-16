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
package com.eclipsesource.uml.glsp.gmodel;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.builder.impl.GGraphBuilder;
import org.eclipse.glsp.server.types.GLSPServerException;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.unotation.Diagram;

public abstract class DiagramFactory extends AbstractGModelFactory<EObject, GModelElement> {

   protected final PackageableNodeFactory classifierNodeFactory;
   protected final CompartmentLabelFactory labelFactory;
   protected final RelationshipEdgeFactory relationshipEdgeFactory;

   public DiagramFactory(final UmlModelState modelState) {
      super(modelState);
      labelFactory = new CompartmentLabelFactory(modelState);
      relationshipEdgeFactory = new RelationshipEdgeFactory(modelState);
      classifierNodeFactory = new PackageableNodeFactory(modelState, labelFactory);
      getOrCreateRoot();
   }

   @Override
   public GModelElement create(final EObject semanticElement) {
      // no-op as we focus on create(final Diagram umlDiagram)
      return null;
   }

   public abstract GGraph create(final Diagram umlDiagram);

   public GGraph create() {
      return create(modelState.getNotationModel());
   }

   public static GLSPServerException createFailed(final EObject semanticElement) {
      return new GLSPServerException("Error during model initialization!", new Throwable(
         "No matching GModelElement found for the semanticElement of type: " + semanticElement.getClass()));
   }

   protected GGraph getOrCreateRoot() {
      GModelRoot existingRoot = modelState.getRoot();
      if (existingRoot != null && existingRoot instanceof GGraph) {
         GGraph graph = (GGraph) existingRoot;
         graph.getChildren().clear();
         return graph;
      }
      return createRoot(modelState);
   }

   public GGraph createRoot(final UmlModelState modelState) {
      GGraph graph = new GGraphBuilder().build();
      modelState.setRoot(graph);
      return graph;
   }
}
