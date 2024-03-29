/*******************************************************************************
 * Copyright (c) 2021-2022 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0, or the MIT License which is
 * available at https://opensource.org/licenses/MIT.
 *
 * SPDX-License-Identifier: EPL-2.0 OR MIT
 ******************************************************************************/
package com.eclipsesource.uml.glsp.model;

import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.client.v1.ModelServerClientV1;
import org.eclipse.emfcloud.modelserver.glsp.model.EMSModelSourceLoader;
import org.eclipse.glsp.server.features.core.model.RequestModelAction;
import org.eclipse.glsp.server.model.GModelState;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;

import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;
import com.eclipsesource.uml.modelserver.UmlModelServerClient;

public class UmlModelSourceLoader extends EMSModelSourceLoader {

   private static Logger LOGGER = Logger.getLogger(EMSModelSourceLoader.class);

   @Override
   public void loadSourceModel(final RequestModelAction action) {
      String sourceURI = getSourceURI(action.getOptions());
      if (sourceURI.isEmpty()) {
         LOGGER.error("No source URI given to load source models");
         return;
      }
      Optional<ModelServerClientV1> modelServerClient = modelServerClientProvider.get();
      if (modelServerClient.isEmpty()) {
         LOGGER.error("Connection to modelserver could not be initialized");
         return;
      }
      if (!(modelServerClient.get() instanceof UmlModelServerClient)) {
         LOGGER.error("ModelServerClient is not an instance of UmlModelServerClient!");
         return;
      }

      UmlModelServerAccess modelServerAccess = createModelServerAccess(sourceURI, modelServerClient.get());

      UmlModelState modelState = createModelState(gModelState);
      modelState.initialize(action.getOptions(), modelServerAccess);

      try {
         modelState.loadSourceModels();
      } catch (GLSPServerException e) {
         LOGGER.error("Error during source model loading");
         e.printStackTrace();
         return;
      }

      modelServerAccess.subscribe(createSubscriptionListener(modelState, actionDispatcher, submissionHandler));
   }

   @Override
   public UmlModelServerAccess createModelServerAccess(final String sourceURI,
      final ModelServerClientV1 modelServerClient) {
      if (!(modelServerClient instanceof UmlModelServerClient)) {
         LOGGER.error("ModelServerClient is not an instance of UmlModelServerClient!");
      }
      return new UmlModelServerAccess(sourceURI, ((UmlModelServerClient) modelServerClient));
   }

   @Override
   public UmlModelState createModelState(final GModelState modelState) {
      return UmlModelState.getModelState(modelState);
   }

   @Override
   protected String getSourceURI(final Map<String, String> clientOptions) {
      // We want to use the absolute sourceUri instead of the relative one from the
      // super class
      String sourceURI = ClientOptionsUtil.getSourceUri(clientOptions)
         .orElseThrow(() -> new GLSPServerException("No source URI given to load model!"));
      return sourceURI;
   }

}
