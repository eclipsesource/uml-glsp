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
package com.eclipsesource.uml.glsp.diagram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.glsp.graph.DefaultTypes;
import org.eclipse.glsp.graph.GraphPackage;
import org.eclipse.glsp.server.diagram.BaseDiagramConfiguration;
import org.eclipse.glsp.server.layout.ServerLayoutKind;
import org.eclipse.glsp.server.types.EdgeTypeHint;
import org.eclipse.glsp.server.types.ShapeTypeHint;

import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.google.common.collect.Lists;

public class UmlDiagramConfiguration extends BaseDiagramConfiguration {

   @Override
   public String getDiagramType() { return "umldiagram"; }

   @Override
   public List<EdgeTypeHint> getEdgeTypeHints() {
      return Lists.newArrayList(createDefaultEdgeTypeHint(Types.ASSOCIATION));
   }

   @Override
   public EdgeTypeHint createDefaultEdgeTypeHint(final String elementId) {
      List<String> allowed = Lists.newArrayList(Types.CLASS);
      return new EdgeTypeHint(elementId, true, true, true, allowed, allowed);
   }

   @Override
   public List<ShapeTypeHint> getShapeTypeHints() {
      List<ShapeTypeHint> hints = new ArrayList<>();
      hints.add(new ShapeTypeHint(DefaultTypes.GRAPH, false, false, false, false, List.of(Types.CLASS, Types.PACKAGE)));
      hints.add(new ShapeTypeHint(Types.CLASS, true, true, false, false, List.of(Types.PROPERTY)));
      hints.add(new ShapeTypeHint(Types.PROPERTY, false, true, false, true));
      ShapeTypeHint packageHint = new ShapeTypeHint(Types.PACKAGE, true, true, true, false);
      packageHint.setContainableElementTypeIds(Arrays.asList(Types.PACKAGE));
      hints.add(packageHint);
      return hints;
   }

   @Override
   public Map<String, EClass> getTypeMappings() {
      Map<String, EClass> mappings = DefaultTypes.getDefaultTypeMappings();

      mappings.put(Types.LABEL_NAME, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_TEXT, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_EDGE_NAME, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_EDGE_MULTIPLICITY, GraphPackage.Literals.GLABEL);
      mappings.put(Types.COMPARTMENT, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.COMPARTMENT_HEADER, GraphPackage.Literals.GCOMPARTMENT);

      // UML Class
      mappings.put(Types.ICON_CLASS, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.CLASS, GraphPackage.Literals.GNODE);
      // UML Property
      mappings.put(Types.PROPERTY, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.ICON_PROPERTY, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.LABEL_PROPERTY_NAME, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_PROPERTY_TYPE, GraphPackage.Literals.GLABEL);
      mappings.put(Types.LABEL_PROPERTY_MULTIPLICITY, GraphPackage.Literals.GLABEL);
      // UML Associations
      mappings.put(Types.ASSOCIATION, GraphPackage.Literals.GEDGE);
      // UML Package
      mappings.put(Types.PACKAGE, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.STRUCTURE, GraphPackage.Literals.GCOMPARTMENT);
      mappings.put(Types.LABEL_PACKAGE_NAME, GraphPackage.Literals.GLABEL);
      return mappings;
   }

   @Override
   public ServerLayoutKind getLayoutKind() { return ServerLayoutKind.MANUAL; }

}
