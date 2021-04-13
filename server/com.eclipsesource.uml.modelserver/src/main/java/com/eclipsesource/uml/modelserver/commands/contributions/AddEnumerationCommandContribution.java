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
package com.eclipsesource.uml.modelserver.commands.contributions;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emfcloud.modelserver.command.CCommand;
import org.eclipse.emfcloud.modelserver.command.CCommandFactory;
import org.eclipse.emfcloud.modelserver.command.CCompoundCommand;
import org.eclipse.emfcloud.modelserver.common.codecs.DecodingException;
import org.eclipse.glsp.graph.GPoint;

import com.eclipsesource.uml.modelserver.commands.compound.AddEnumerationCompoundCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlNotationCommandUtil;

public class AddEnumerationCommandContribution extends UmlCompoundCommandContribution {

   public static final String TYPE = "addEnumerationContributuion";

   public static CCompoundCommand create(final GPoint position) {
      CCompoundCommand addEnumerationCommand = CCommandFactory.eINSTANCE.createCompoundCommand();
      addEnumerationCommand.setType(TYPE);
      addEnumerationCommand.getProperties().put(UmlNotationCommandContribution.POSITION_X,
         String.valueOf(position.getX()));
      addEnumerationCommand.getProperties().put(UmlNotationCommandContribution.POSITION_Y,
         String.valueOf(position.getY()));
      return addEnumerationCommand;
   }

   @Override
   protected CompoundCommand toServer(final URI modelUri, final EditingDomain domain, final CCommand command)
      throws DecodingException {

      GPoint enumerationPosition = UmlNotationCommandUtil.getGPoint(
         command.getProperties().get(UmlNotationCommandContribution.POSITION_X),
         command.getProperties().get(UmlNotationCommandContribution.POSITION_Y));

      return new AddEnumerationCompoundCommand(domain, modelUri, enumerationPosition);
   }

}
