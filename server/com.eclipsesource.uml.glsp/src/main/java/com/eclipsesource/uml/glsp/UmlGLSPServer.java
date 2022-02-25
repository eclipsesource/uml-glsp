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
package com.eclipsesource.uml.glsp;

import java.net.MalformedURLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.eclipse.emfcloud.modelserver.client.ModelServerClient;
import org.eclipse.emfcloud.modelserver.glsp.EMSGLSPServer;
import org.eclipse.glsp.server.protocol.DisposeClientSessionParameters;
import org.eclipse.glsp.server.types.GLSPServerException;
import org.eclipse.glsp.server.utils.ClientOptionsUtil;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.eclipsesource.uml.modelserver.UmlModelServerClient;
import com.eclipsesource.uml.modelserver.UmlNotationUtil;

public class UmlGLSPServer extends EMSGLSPServer {

   @Override
   protected ModelServerClient createModelServerClient(final String modelServerURL) throws MalformedURLException {
      return new UmlModelServerClient(modelServerURL);
   }

   @Override
   public CompletableFuture<Void> disposeClientSession(final DisposeClientSessionParameters params) {
      Optional<ModelServerClient> modelServerClient = modelServerClientProvider.get();
      if (modelServerClient.isPresent()) {
         String sourceURI = ClientOptionsUtil.getSourceUri(params.getArgs())
            .orElseThrow(() -> new GLSPServerException("No source URI given to dispose client session!"));
         modelServerClient.get()
            .unsubscribe(sourceURI.replace(UmlNotationUtil.NOTATION_EXTENSION, UMLResource.FILE_EXTENSION));
      }
      return super.disposeClientSession(params);
   }

}
