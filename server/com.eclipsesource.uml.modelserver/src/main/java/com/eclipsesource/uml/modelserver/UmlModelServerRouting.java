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

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

import org.eclipse.emfcloud.modelserver.common.ModelServerPathParametersV1;
import org.eclipse.emfcloud.modelserver.common.ModelServerPathsV1;
import org.eclipse.emfcloud.modelserver.common.codecs.EncodingException;
import org.eclipse.emfcloud.modelserver.emf.common.JsonResponse;
import org.eclipse.emfcloud.modelserver.emf.common.ModelController;
import org.eclipse.emfcloud.modelserver.emf.common.ModelResourceManager;
import org.eclipse.emfcloud.modelserver.emf.common.ModelServerRoutingV1;
import org.eclipse.emfcloud.modelserver.emf.common.SchemaController;
import org.eclipse.emfcloud.modelserver.emf.common.ServerController;
import org.eclipse.emfcloud.modelserver.emf.common.SessionController;
import org.eclipse.emfcloud.modelserver.emf.common.codecs.JsonCodec;

import com.google.inject.Inject;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class UmlModelServerRouting extends ModelServerRoutingV1 {

   @Inject
   public UmlModelServerRouting(final Javalin javalin, final ModelResourceManager resourceManager,
      final ModelController modelController,
      final SchemaController schemaController, final ServerController serverController,
      final SessionController sessionController) {
      super(javalin, resourceManager, modelController, schemaController, serverController, sessionController);
   }

   protected void getUmlTypes(final Context ctx) {
      getResolvedFileUri(ctx, ModelServerPathParametersV1.MODEL_URI).ifPresent(
         param -> {
            try {
               ctx.json(JsonResponse
                  .success(JsonCodec.encode(((UmlModelResourceManager) resourceManager).getUmlTypes(param))));
            } catch (EncodingException e) {
               // FIXME investigate why cannot be accessed
               // encodingError(ctx, e);
            }
         });
      // FIXME investigate why cannot be accessed
      // () -> missingParameter(ctx, ModelServerPathParametersV1.MODEL_URI));
   }

   @Override
   public void bindRoutes() {
      javalin.routes(this::endpoints);
   }

   private void endpoints() {
      path(ModelServerPathsV1.BASE_PATH, this::apiEndpoints);
   }

   private void apiEndpoints() {
      get(UmlModelServerPaths.UML_TYPES, this::getUmlTypes);
   }

}
