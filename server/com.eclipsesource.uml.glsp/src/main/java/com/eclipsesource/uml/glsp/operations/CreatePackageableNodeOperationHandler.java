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
package com.eclipsesource.uml.glsp.operations;

import static org.eclipse.glsp.server.types.GLSPServerException.getOrThrow;

import java.util.List;
import java.util.Optional;

import org.eclipse.emfcloud.modelserver.glsp.operations.handlers.EMSBasicCreateOperationHandler;
import org.eclipse.glsp.graph.GBoundsAware;
import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.glsp.server.operations.CreateNodeOperation;
import org.eclipse.glsp.server.operations.Operation;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.GeometryUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Package;

import com.eclipsesource.uml.glsp.model.UmlModelIndex;
import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class CreatePackageableNodeOperationHandler
   extends EMSBasicCreateOperationHandler<CreateNodeOperation, UmlModelServerAccess> {

   public CreatePackageableNodeOperationHandler() {
      super(handledElementTypeIds);
   }

   private static List<String> handledElementTypeIds = Lists.newArrayList(Types.CLASS, Types.PACKAGE);

   @Override
   public boolean handles(final Operation execAction) {
      if (execAction instanceof CreateNodeOperation) {
         CreateNodeOperation action = (CreateNodeOperation) execAction;
         return handledElementTypeIds.contains(action.getElementTypeId());
      }
      return false;
   }

   protected UmlModelState getUmlModelState() { return (UmlModelState) getEMSModelState(); }

   @Override
   public void executeOperation(final CreateNodeOperation operation, final UmlModelServerAccess modelAccess) {

      UmlModelState modelState = getUmlModelState();
      UmlModelIndex modelIndex = modelState.getIndex();

      switch (operation.getElementTypeId()) {
         case Types.CLASS: {
            modelAccess.addClass(getUmlModelState(), operation.getLocation())
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not execute create operation on new Class node");
                  }
               });
            break;
         }
         case Types.PACKAGE: {
            NamedElement parentContainer = getOrThrow(
               modelIndex.getSemantic(operation.getContainerId(), NamedElement.class),
               "No semantic container object found for source element with id " + operation.getContainerId());

            if (parentContainer instanceof Model) {
               modelAccess.addPackage(getUmlModelState(), operation.getLocation(), Model.class.cast(parentContainer))
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new Package node");
                     }
                  });
            } else if (parentContainer instanceof Package) {
               // If the container is a Package node, find its structure compartment to compute the relative position
               Optional<GModelElement> container = modelIndex.get(operation.getContainerId());
               Optional<GModelElement> structCompartment = container.filter(GNode.class::isInstance)
                  .map(GNode.class::cast)
                  .flatMap(this::getStructureCompartment);
               Optional<GPoint> relativeLocation = getRelativeLocation(operation, operation.getLocation(),
                  structCompartment);
               modelAccess.addPackage(getUmlModelState(), relativeLocation, Package.class.cast(parentContainer))
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not execute create operation on new Package node");
                     }
                  });
            }
            break;
         }
      }
   }

   protected Optional<GCompartment> getStructureCompartment(final GNode packageable) {
      return packageable.getChildren().stream().filter(GCompartment.class::isInstance).map(GCompartment.class::cast)
         .filter(comp -> Types.STRUCTURE.equals(comp.getType())).findFirst();
   }

   /**
    * Retrieve the location at which the new node should be created, in the container
    * coordinates space.
    *
    * @param operation
    * @param absoluteLocation
    * @param container
    * @return
    *         A point relative to the container element, where the new node should be created
    */
   protected Optional<GPoint> getRelativeLocation(final CreateNodeOperation operation,
      final Optional<GPoint> absoluteLocation, final Optional<GModelElement> container) {
      if (absoluteLocation.isPresent() && container.isPresent()) {
         // When creating elements on a parent node (other than the root Graph),
         // prevent the node from using negative coordinates
         boolean allowNegativeCoordinates = container.get() instanceof GGraph;
         GModelElement modelElement = container.get();
         if (modelElement instanceof GBoundsAware) {
            try {
               GPoint relativePosition = GeometryUtil.absoluteToRelative(absoluteLocation.get(),
                  (GBoundsAware) modelElement);
               GPoint relativeLocation = allowNegativeCoordinates
                  ? relativePosition
                  : GraphUtil.point(Math.max(0, relativePosition.getX()), Math.max(0, relativePosition.getY()));
               return Optional.of(relativeLocation);
            } catch (IllegalArgumentException ex) {
               return absoluteLocation;
            }
         }
      }
      return Optional.empty();
   }

   @Override
   public String getLabel() { return "Create uml classifier"; }

}
