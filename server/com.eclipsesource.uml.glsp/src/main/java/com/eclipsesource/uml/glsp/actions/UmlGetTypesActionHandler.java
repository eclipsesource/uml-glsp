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
package com.eclipsesource.uml.glsp.actions;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.eclipse.emfcloud.modelserver.client.Response;
import org.eclipse.emfcloud.modelserver.glsp.actions.handlers.EMSBasicActionHandler;
import org.eclipse.glsp.server.actions.Action;

import com.eclipsesource.uml.glsp.modelserver.UmlModelServerAccess;

public class UmlGetTypesActionHandler extends EMSBasicActionHandler<GetTypesAction, UmlModelServerAccess> {

   private static Logger LOGGER = Logger.getLogger(UmlGetTypesActionHandler.class.getSimpleName());

   @Override
   public List<Action> executeAction(final GetTypesAction action, final UmlModelServerAccess modelServerAccess) {
      try {
         Response<List<String>> response = modelServerAccess.getUmlTypes().get();
         List<String> types = response.body();
         Collections.sort(types);
         return List.of(new ReturnTypesAction(types));
      } catch (InterruptedException | ExecutionException e) {
         LOGGER.error("Error while fetching UML types from Model Server");
         e.printStackTrace();
      }
      return List.of();
   }

}
