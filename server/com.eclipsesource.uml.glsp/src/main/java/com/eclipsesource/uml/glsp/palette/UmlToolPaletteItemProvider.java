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
package com.eclipsesource.uml.glsp.palette;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.glsp.server.actions.TriggerEdgeCreationAction;
import org.eclipse.glsp.server.actions.TriggerNodeCreationAction;
import org.eclipse.glsp.server.features.toolpalette.PaletteItem;
import org.eclipse.glsp.server.features.toolpalette.ToolPaletteItemProvider;

import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class UmlToolPaletteItemProvider implements ToolPaletteItemProvider {

   private static Logger LOGGER = Logger.getLogger(UmlToolPaletteItemProvider.class.getSimpleName());

   @Override
   public List<PaletteItem> getItems(final Map<String, String> args) {
      LOGGER.info("Create palette");
      return Lists.newArrayList(classifiers(), relations(), features());
   }

   private PaletteItem classifiers() {
      PaletteItem createClass = node(Types.CLASS, "Class", "umlclass");
      PaletteItem createEnumeration = node(Types.ENUMERATION, "Enumeration", "umlenumeration");

      List<PaletteItem> classifiers = Lists.newArrayList(createClass, createEnumeration);
      return PaletteItem.createPaletteGroup("uml.classifier", "Classifier", classifiers, "symbol-property");
   }

   private PaletteItem relations() {
      PaletteItem createAssociation = edge(Types.ASSOCIATION, "Association", "umlassociation");

      List<PaletteItem> edges = Lists.newArrayList(createAssociation);
      return PaletteItem.createPaletteGroup("uml.relation", "Relation", edges, "symbol-property");
   }

   private PaletteItem features() {
      PaletteItem createProperty = node(Types.PROPERTY, "Property", "umlproperty");
      PaletteItem createEnumerationliteral = node(Types.LABEL_ENUMERATION_LITERAL, "Enumeration Literal",
         "umlenumerationliteral");

      List<PaletteItem> features = Lists.newArrayList(createProperty, createEnumerationliteral);

      return PaletteItem.createPaletteGroup("uml.feature", "Feature", features, "symbol-property");
   }

   private PaletteItem node(final String elementTypeId, final String label, final String icon) {
      return new PaletteItem(elementTypeId, label, new TriggerNodeCreationAction(elementTypeId), icon);
   }

   private PaletteItem edge(final String elementTypeId, final String label, final String icon) {
      return new PaletteItem(elementTypeId, label, new TriggerEdgeCreationAction(elementTypeId), icon);
   }
}
