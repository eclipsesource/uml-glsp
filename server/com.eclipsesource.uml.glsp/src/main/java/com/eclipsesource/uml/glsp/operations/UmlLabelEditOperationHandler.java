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

import org.eclipse.emfcloud.modelserver.glsp.operations.handlers.EMSBasicOperationHandler;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.server.features.directediting.ApplyLabelEditOperation;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;

import com.eclipsesource.uml.glsp.model.UmlModelIndex;
import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.glsp.util.UmlIDUtil;

public class UmlLabelEditOperationHandler
   extends EMSBasicOperationHandler<ApplyLabelEditOperation, UmlModelServerAccess> {

   protected UmlModelState getUmlModelState() { return (UmlModelState) getEMSModelState(); }

   @Override
   public void executeOperation(final ApplyLabelEditOperation editLabelOperation,
      final UmlModelServerAccess modelAccess) {

      UmlModelState modelState = getUmlModelState();
      UmlModelIndex modelIndex = modelState.getIndex();

      String inputText = editLabelOperation.getText().trim();
      String graphicalElementId = editLabelOperation.getLabelId();

      GModelElement label = getOrThrow(modelIndex.findElementByClass(graphicalElementId, GModelElement.class),
         GModelElement.class, "Element not found.");

      switch (label.getType()) {
         case Types.LABEL_NAME:
            String containerElementId = UmlIDUtil.getElementIdFromHeaderLabel(graphicalElementId);
            PackageableElement semanticElement = getOrThrow(modelIndex.getSemantic(containerElementId),
               PackageableElement.class, "No valid container with id " + graphicalElementId + " found");
            if (semanticElement instanceof Class) {
               modelAccess.setClassName(modelState, (Class) semanticElement, inputText)
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not rename Class to: " + inputText);
                     }
                  });
            } else if (semanticElement instanceof Enumeration) {
               modelAccess.setEnumerationName(modelState, (Enumeration) semanticElement, inputText)
                  .thenAccept(response -> {
                     if (!response.body()) {
                        throw new GLSPServerException("Could not rename Enumeration to: " + inputText);
                     }
                  });
            }
            break;

         case Types.LABEL_PROPERTY_NAME:
            containerElementId = UmlIDUtil.getElementIdFromPropertyLabelName(graphicalElementId);
            Property property = getOrThrow(modelIndex.getSemantic(containerElementId),
               Property.class, "No valid container with id " + graphicalElementId + " found");

            modelAccess.setPropertyName(modelState, property, inputText)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not change Property type to: " + inputText);
                  }
               });

            break;

         case Types.LABEL_PROPERTY_TYPE:
            containerElementId = UmlIDUtil.getElementIdFromPropertyLabelType(graphicalElementId);
            property = getOrThrow(modelIndex.getSemantic(containerElementId),
               Property.class, "No valid container with id " + graphicalElementId + " found");

            modelAccess.setPropertyType(modelState, property, inputText)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not change Property type to: " + inputText);
                  }
               });

            break;

         case Types.LABEL_PROPERTY_MULTIPLICITY:
            containerElementId = UmlIDUtil.getElementIdFromPropertyLabelMultiplicity(graphicalElementId);
            property = getOrThrow(modelIndex.getSemantic(containerElementId),
               Property.class, "No valid container with id " + graphicalElementId + " found");

            modelAccess.setPropertyBounds(modelState, property, inputText)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not change Property bounds to: " + inputText);
                  }
               });

            break;

         case Types.LABEL_EDGE_NAME:
            containerElementId = UmlIDUtil.getElementIdFromLabelName(graphicalElementId);
            Property associationEnd = getOrThrow(modelIndex.getSemantic(containerElementId),
               Property.class, "No valid container with id " + graphicalElementId + " found");

            modelAccess.setAssociationEndName(modelState, associationEnd, inputText)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not change Association End Name to: " + inputText);
                  }
               });
            break;

         case Types.LABEL_EDGE_MULTIPLICITY:
            containerElementId = UmlIDUtil.getElementIdFromLabelMultiplicity(graphicalElementId);
            associationEnd = getOrThrow(modelIndex.getSemantic(containerElementId),
               Property.class, "No valid container with id " + graphicalElementId + " found");

            modelAccess.setAssociationEndMultiplicity(modelState, associationEnd, inputText)
               .thenAccept(response -> {
                  if (!response.body()) {
                     throw new GLSPServerException("Could not change Association End Multiplicity to: " + inputText);
                  }
               });
            break;
      }

   }

   @Override
   public String getLabel() { return "Apply label"; }

}
