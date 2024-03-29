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
package com.eclipsesource.uml.modelserver;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.emf.common.RecordingModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.watchers.ModelWatchersManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.emfcloud.modelserver.emf.util.JsonPatchHelper;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.eclipsesource.uml.modelserver.unotation.Diagram;
import com.eclipsesource.uml.modelserver.unotation.SemanticProxy;
import com.eclipsesource.uml.modelserver.unotation.UnotationFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class UmlModelResourceManager extends RecordingModelResourceManager {

   @Inject
   public UmlModelResourceManager(final Set<EPackageConfiguration> configurations, final AdapterFactory adapterFactory,
      final ServerConfiguration serverConfiguration, final ModelWatchersManager watchersManager,
      final Provider<JsonPatchHelper> jsonPatchHelper) {
      super(configurations, adapterFactory, serverConfiguration, watchersManager, jsonPatchHelper);
   }

   @Override
   protected void loadSourceResources(final String directoryPath) {
      if (directoryPath == null || directoryPath.isEmpty()) {
         return;
      }
      File directory = new File(directoryPath);
      for (File file : directory.listFiles()) {
         if (isSourceDirectory(file)) {
            loadSourceResources(file.getAbsolutePath());
         } else if (file.isFile()) {
            URI modelURI = createURI(file.getAbsolutePath());
            if (modelURI.fileExtension().equals(UMLResource.FILE_EXTENSION)) {
               resourceSets.put(modelURI, resourceSetFactory.createResourceSet(modelURI));
            }
            loadResource(modelURI.toString());
         }
      }
   }

   @Override
   public ResourceSet getResourceSet(final String modeluri) {
      if (createURI(modeluri).fileExtension().equals(UmlNotationUtil.NOTATION_EXTENSION)) {
         URI semanticUri = createURI(modeluri).trimFileExtension().appendFileExtension(UMLResource.FILE_EXTENSION);
         return resourceSets.get(semanticUri);
      }
      return resourceSets.get(createURI(modeluri));
   }

   @Override
   public boolean save(final String modeluri) {
      boolean result = false;
      EList<Resource> res = getResourceSet(modeluri).getResources();
      for (Resource resource : res) {
         // Do only save file resources, no library resources
         if (resource.getURI().isFile()) {
            result = saveResource(resource);
         }
      }
      if (result) {
         getEditingDomain(getResourceSet(modeluri)).saveIsDone();
      }
      return result;
   }

   public Set<String> getUmlTypes(final String modeluri) {
      ResourceSet resourceSet = getResourceSet(modeluri);
      Set<String> listOfClassifiers = new HashSet<>();
      TreeIterator<Notifier> resourceSetContent = resourceSet.getAllContents();
      while (resourceSetContent.hasNext()) {
         Notifier res = resourceSetContent.next();
         if (res instanceof DataType || res instanceof org.eclipse.uml2.uml.Class) {
            listOfClassifiers.add(((NamedElement) res).getName());
         }
      }
      return listOfClassifiers;
   }

   public boolean addUmlResources(final String modeluri, final String diagramType) {
      URI umlModelUri = createURI(modeluri);
      ResourceSet resourceSet = resourceSetFactory.createResourceSet(umlModelUri);
      resourceSets.put(umlModelUri, resourceSet);

      final Model umlModel = createNewModel(umlModelUri);

      try {
         final Resource umlResource = resourceSet.createResource(umlModelUri);
         resourceSet.getResources().add(umlResource);
         umlResource.getContents().add(umlModel);
         umlResource.save(null);

         final Resource umlNotationResource = resourceSet
            .createResource(umlModelUri.trimFileExtension().appendFileExtension(UmlNotationUtil.NOTATION_EXTENSION));
         resourceSet.getResources().add(umlNotationResource);
         umlNotationResource.getContents().add(createNewDiagram(umlModel, diagramType));
         umlNotationResource.save(null);
         createEditingDomain(resourceSet);

      } catch (IOException e) {
         return false;
      }

      return true;
   }

   protected Model createNewModel(final URI modelUri) {
      Model newModel = UMLFactory.eINSTANCE.createModel();
      String modelName = modelUri.lastSegment().split("." + modelUri.fileExtension())[0];
      newModel.setName(modelName);
      return newModel;
   }

   protected Diagram createNewDiagram(final Model model, final String diagramType) {
      Diagram newDiagram = UnotationFactory.eINSTANCE.createDiagram();
      SemanticProxy semanticProxy = UnotationFactory.eINSTANCE.createSemanticProxy();
      semanticProxy.setUri(EcoreUtil.getURI(model).fragment());
      newDiagram.setSemanticElement(semanticProxy);
      newDiagram.setDiagramType(UmlNotationUtil.getRepresentation(diagramType));
      return newDiagram;
   }

}
