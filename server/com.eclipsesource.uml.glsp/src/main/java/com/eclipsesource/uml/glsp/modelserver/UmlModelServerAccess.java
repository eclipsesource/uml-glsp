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
package com.eclipsesource.uml.glsp.modelserver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.glsp.EMSModelServerAccess;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.glsp.server.types.ElementAndBounds;
import org.eclipse.glsp.server.types.ElementAndRoutingPoints;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.modelserver.UmlModelServerClient;
import com.eclipsesource.uml.modelserver.UmlNotationUtil;
import com.eclipsesource.uml.modelserver.commands.contributions.AddAssociationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddClassCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.AddPropertyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.ChangeBoundsCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.ChangeRoutingPointsCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveAssociationCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemoveClassCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.RemovePropertyCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetAssociationEndMultiplicityCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetAssociationEndNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetClassNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetPropertyBoundsCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetPropertyNameCommandContribution;
import com.eclipsesource.uml.modelserver.commands.contributions.SetPropertyTypeCommandContribution;
import com.eclipsesource.uml.modelserver.unotation.Edge;
import com.eclipsesource.uml.modelserver.unotation.Shape;
import com.google.common.base.Preconditions;

public class UmlModelServerAccess extends EMSModelServerAccess {

   private static Logger LOGGER = Logger.getLogger(UmlModelServerAccess.class);

   private final UmlModelServerClient modelServerClient;
   protected String notationFileExtension;

   public UmlModelServerAccess(final String sourceURI, final UmlModelServerClient modelServerClient) {
      super(sourceURI, modelServerClient, UMLResource.FILE_EXTENSION);
      Preconditions.checkNotNull(modelServerClient);
      this.modelServerClient = modelServerClient;
      this.notationFileExtension = UmlNotationUtil.NOTATION_EXTENSION;
   }

   @Override
   public EObject getSemanticModel() {
      try {
         return modelServerClient.get(getSemanticURI(), UMLResource.FILE_EXTENSION).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         LOGGER.error(e);
         throw new GLSPServerException("Error during model loading", e);
      }
   }

   public String getNotationURI() { return baseSourceUri.appendFileExtension(this.notationFileExtension).toString(); }

   public EObject getNotationModel() {
      try {
         return modelServerClient.get(getNotationURI(), FORMAT_XMI).thenApply(res -> res.body()).get();
      } catch (InterruptedException | ExecutionException e) {
         LOGGER.error(e);
         throw new GLSPServerException("Error during model loading", e);
      }
   }

   protected String getSemanticUriFragment(final NamedElement element) {
      return EcoreUtil.getURI(element).fragment();
   }

   /*
    * UML Types
    */
   public CompletableFuture<Response<List<String>>> getUmlTypes() {
      return this.modelServerClient.getUmlTypes(getSemanticURI());
   }

   /*
    * UML Class
    */
   public CompletableFuture<Response<Boolean>> addClass(final UmlModelState modelState,
      final Optional<GPoint> newPosition) {

      CCompoundCommand addClassCompoundCommand = AddClassCommandContribution
         .create(newPosition.orElse(GraphUtil.point(0, 0)));
      return this.edit(addClassCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeClass(final UmlModelState modelState,
      final Class classToRemove) {

      String semanticProxyUri = getSemanticUriFragment(classToRemove);
      CCompoundCommand compoundCommand = RemoveClassCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setClassName(final UmlModelState modelState,
      final Class classToRename, final String newName) {

      CCommand setClassNameCommand = SetClassNameCommandContribution.create(getSemanticUriFragment(classToRename),
         newName);
      return this.edit(setClassNameCommand);
   }

   /*
    * UML Property
    */
   public CompletableFuture<Response<Boolean>> addProperty(final UmlModelState modelState,
      final Class parentClass) {

      CCommand addPropertyCommand = AddPropertyCommandContribution.create(getSemanticUriFragment(parentClass));
      return this.edit(addPropertyCommand);
   }

   public CompletableFuture<Response<Boolean>> removeProperty(final UmlModelState modelState,
      final Property propertyToRemove) {

      Class parentClass = (Class) propertyToRemove.eContainer();
      CCommand removePropertyCommand = RemovePropertyCommandContribution
         .create(getSemanticUriFragment(parentClass), getSemanticUriFragment(propertyToRemove));
      return this.edit(removePropertyCommand);
   }

   public CompletableFuture<Response<Boolean>> setPropertyName(final UmlModelState modelState,
      final Property propertyToRename, final String newName) {

      CCommand setPropertyNameCommand = SetPropertyNameCommandContribution
         .create(getSemanticUriFragment(propertyToRename), newName);
      return this.edit(setPropertyNameCommand);
   }

   public CompletableFuture<Response<Boolean>> setPropertyType(final UmlModelState modelState,
      final Property propertyToRename, final String newType) {

      CCommand setPropertyNameCommand = SetPropertyTypeCommandContribution
         .create(getSemanticUriFragment(propertyToRename), newType);
      return this.edit(setPropertyNameCommand);
   }

   public CompletableFuture<Response<Boolean>> setPropertyBounds(final UmlModelState modelState,
      final Property propertyToRename, final String newBounds) {

      CCommand setPropertyNameCommand = SetPropertyBoundsCommandContribution
         .create(getSemanticUriFragment(propertyToRename), newBounds);
      return this.edit(setPropertyNameCommand);
   }

   /*
    * UML Association
    */
   public CompletableFuture<Response<Boolean>> addAssociation(final UmlModelState modelState,
      final Class sourceClass, final Class targetClass) {

      CCompoundCommand addAssociationCompoundCommand = AddAssociationCommandContribution
         .create(getSemanticUriFragment(sourceClass), getSemanticUriFragment(targetClass));
      return this.edit(addAssociationCompoundCommand);
   }

   public CompletableFuture<Response<Boolean>> removeAssociation(final UmlModelState modelState,
      final Association associationToRemove) {

      String semanticProxyUri = getSemanticUriFragment(associationToRemove);
      CCompoundCommand compoundCommand = RemoveAssociationCommandContribution.create(semanticProxyUri);
      return this.edit(compoundCommand);
   }

   public CompletableFuture<Response<Boolean>> setAssociationEndName(final UmlModelState modelState,
      final Property associationEnd, final String newName) {

      CCommand setClassNameCommand = SetAssociationEndNameCommandContribution.create(
         getSemanticUriFragment(associationEnd), newName);
      return this.edit(setClassNameCommand);
   }

   public CompletableFuture<Response<Boolean>> setAssociationEndMultiplicity(final UmlModelState modelState,
      final Property associationEnd, final String newBounds) {

      CCommand setClassNameCommand = SetAssociationEndMultiplicityCommandContribution.create(
         getSemanticUriFragment(associationEnd), newBounds);
      return this.edit(setClassNameCommand);
   }

   /*
    * Change Bounds
    */
   public CompletableFuture<Response<Boolean>> changeBounds(final Map<Shape, ElementAndBounds> changeBoundsMap) {
      CCompoundCommand compoundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      compoundCommand.setType(ChangeBoundsCommandContribution.TYPE);
      changeBoundsMap.forEach((shape, elementAndBounds) -> {
         CCommand changeBoundsCommand = ChangeBoundsCommandContribution.create(shape.getSemanticElement().getUri(),
            elementAndBounds.getNewPosition(), elementAndBounds.getNewSize());
         compoundCommand.getCommands().add(changeBoundsCommand);
      });
      return this.edit(compoundCommand);
   }

   /*
    * Change Routing Points
    */
   public CompletableFuture<Response<Boolean>> changeRoutingPoints(
      final Map<Edge, ElementAndRoutingPoints> changeBendPointsMap) {
      CCompoundCommand compoundCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      compoundCommand.setType(ChangeRoutingPointsCommandContribution.TYPE);

      changeBendPointsMap.forEach((edge, elementAndRoutingPoints) -> {
         CCommand changeRoutingPointsCommand = ChangeRoutingPointsCommandContribution.create(
            edge.getSemanticElement().getUri(), elementAndRoutingPoints.getNewRoutingPoints());
         compoundCommand.getCommands().add(changeRoutingPointsCommand);
      });
      return this.edit(compoundCommand);
   }

}
