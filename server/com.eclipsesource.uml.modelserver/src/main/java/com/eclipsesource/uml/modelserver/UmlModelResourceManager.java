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
package com.eclipsesource.uml.modelserver;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emfcloud.modelserver.emf.common.RecordingModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.configuration.EPackageConfiguration;
import org.eclipse.emfcloud.modelserver.emf.configuration.ServerConfiguration;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import com.eclipsesource.uml.modelserver.unotation.Diagram;
import com.eclipsesource.uml.modelserver.unotation.SemanticProxy;
import com.eclipsesource.uml.modelserver.unotation.UnotationFactory;
import com.google.common.base.Strings;
import com.google.inject.Inject;

public class UmlModelResourceManager extends RecordingModelResourceManager {

   @Inject
   public UmlModelResourceManager(final Set<EPackageConfiguration> configurations, final AdapterFactory adapterFactory,
      final ServerConfiguration serverConfiguration) {
      super(configurations, adapterFactory, serverConfiguration);
   }

   @Override
   public String adaptModelUri(final String modelUri) {
      URI uri = URI.createURI(modelUri, true);
      if (uri.isRelative()) {
         if (serverConfiguration.getWorkspaceRootURI().isFile()) {
            return uri.resolve(serverConfiguration.getWorkspaceRootURI()).toString();
         }
         return URI.createFileURI(modelUri).toString();
      }
      // Create file URI from path if modelUri is already absolute path (file:/ or full path file:///)
      // to ensure consistent usage of org.eclipse.emf.common.util.URI
      if (uri.hasDevice() && !Strings.isNullOrEmpty(uri.device())) {
         return URI.createFileURI(uri.device() + uri.path()).toString();
      }
      // In case of Windows: we cannot skip the scheme (e.g. C:)
      // therefore we check if scheme is no file: scheme and then use the whole uri as fileURI
      if (URI.validScheme(uri.scheme()) && !uri.isFile()) {
         return URI.createFileURI(uri.toString()).toString();
      }
      return URI.createFileURI(uri.path()).toString();
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
            URI absolutePath = createURI(file.getAbsolutePath());
            if (absolutePath.fileExtension().equals(UMLResource.FILE_EXTENSION)) {
               ResourceSet resourceSet = new ResourceSetImpl();
               resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
                  UMLResource.Factory.INSTANCE);
               UMLResourcesUtil.init(resourceSet);
               resourceSets.put(absolutePath, resourceSet);
               loadResourceLibraries(resourceSet);
            }
            loadResource(absolutePath.toString(),
               false /* do not remove unloadable resources on workspace startup */);
         }
      }
   }

   protected void loadResourceLibraries(final ResourceSet resourceSet) {
      try {
         resourceSet.getResource(URI.createURI(UMLResource.UML_PRIMITIVE_TYPES_LIBRARY_URI), true)
            .load(Collections.EMPTY_MAP);
         resourceSet.getResource(URI.createURI(UMLResource.ECORE_PRIMITIVE_TYPES_LIBRARY_URI), true)
            .load(Collections.EMPTY_MAP);
         resourceSet.getResource(URI.createURI(UMLResource.ECORE_PROFILE_URI), true)
            .load(Collections.EMPTY_MAP);
      } catch (IOException e) {
         LOG.debug("Could not load resource libraries for resourceSet with URI: " + resourceSet.toString());
         e.printStackTrace();
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

   @Override
   public void addResource(final String modeluri, final EObject model) throws IOException {
      ResourceSet resourceSet = new ResourceSetImpl();
      resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION,
         UMLResource.Factory.INSTANCE);
      UMLResourcesUtil.init(resourceSet);
      resourceSets.put(createURI(modeluri), resourceSet);
      loadResourceLibraries(resourceSet);

      final Resource umlResource = resourceSet.createResource(createURI(modeluri));
      resourceSet.getResources().add(umlResource);
      umlResource.getContents().add(model);
      umlResource.save(null);

      final Resource umlNotationResource = resourceSet
         .createResource(
            createURI(modeluri).trimFileExtension().appendFileExtension(UmlNotationUtil.NOTATION_EXTENSION));
      resourceSet.getResources().add(umlNotationResource);
      umlNotationResource.getContents().add(createNewClassDiagram((Model) model));
      umlNotationResource.save(null);

      createEditingDomain(resourceSet);
   }

   protected Diagram createNewClassDiagram(final Model model) {
      Diagram newDiagram = UnotationFactory.eINSTANCE.createDiagram();
      SemanticProxy semanticProxy = UnotationFactory.eINSTANCE.createSemanticProxy();
      semanticProxy.setUri(EcoreUtil.getURI(model).fragment());
      newDiagram.setSemanticElement(semanticProxy);
      newDiagram.setType(UmlNotationUtil.CLASS_REPRESENTATION);
      return newDiagram;
   }

}
