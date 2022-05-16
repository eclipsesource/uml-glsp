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
package com.eclipsesource.uml.glsp.gmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GDimension;
import org.eclipse.glsp.graph.GLabel;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.CSS;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.glsp.util.UmlIDUtil;
import com.eclipsesource.uml.modelserver.unotation.Shape;

public class PackageableNodeFactory extends AbstractGModelFactory<PackageableElement, GNode> {

   private final CompartmentLabelFactory compartmentLabelFactory;

   private static final String V_GRAB = "vGrab";
   private static final String H_GRAB = "hGrab";
   private static final String H_ALIGN = "hAlign";

   public PackageableNodeFactory(final UmlModelState modelState,
      final CompartmentLabelFactory compartmentLabelFactory) {
      super(modelState);
      this.compartmentLabelFactory = compartmentLabelFactory;
   }

   @Override
   public GNode create(final PackageableElement classifier) {
      if (classifier instanceof Class) {
         return createClassNode((Class) classifier);
      } else if (classifier instanceof Package) {
         return createPackageNode((Package) classifier);
      }
      return null;
   }

   protected void applyShapeData(final PackageableElement element, final GNodeBuilder builder) {
      modelState.getIndex().getNotation(element, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            builder.position(GraphUtil.copy(shape.getPosition()));
         }
         if (shape.getSize() != null) {
            GDimension size = GraphUtil.copy(shape.getSize());
            builder.size(size);
            builder.layoutOptions(Map.of(
               GLayoutOptions.KEY_PREF_WIDTH, size.getWidth(),
               GLayoutOptions.KEY_PREF_HEIGHT, size.getHeight()));
         }
      });
   }

   protected GNode createClassNode(final Class umlClass) {
      GNodeBuilder classNodeBuilder = new GNodeBuilder(Types.CLASS)
         .id(toId(umlClass))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umlClass, classNodeBuilder);

      GCompartment classHeader = buildClassHeader(umlClass);
      classNodeBuilder.add(classHeader);

      GCompartment classPropertiesCompartment = buildClassPropertiesCompartment(umlClass.getAttributes(), umlClass);
      classNodeBuilder.add(classPropertiesCompartment);

      return classNodeBuilder.build();
   }

   protected GCompartment buildClassHeader(final Class umlClass) {
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMPARTMENT_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(umlClass)));

      GCompartment classHeaderIcon = new GCompartmentBuilder(Types.ICON_CLASS)
         .id(UmlIDUtil.createHeaderIconId(toId(umlClass))).build();
      classHeaderBuilder.add(classHeaderIcon);

      GLabel classHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(umlClass)))
         .text(umlClass.getName()).build();
      classHeaderBuilder.add(classHeaderLabel);

      return classHeaderBuilder.build();
   }

   protected GCompartment buildClassPropertiesCompartment(final Collection<? extends Property> properties,
      final Classifier parent) {
      GCompartmentBuilder classPropertiesBuilder = new GCompartmentBuilder(Types.COMPARTMENT)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent)))
         .layout(GConstants.Layout.VBOX);

      GLayoutOptions layoutOptions = new GLayoutOptions()
         .hAlign(GConstants.HAlign.LEFT)
         .resizeContainer(true);
      classPropertiesBuilder.layoutOptions(layoutOptions);

      List<GModelElement> propertiesElements = properties.stream()
         .map(compartmentLabelFactory::createPropertyLabel)
         .collect(Collectors.toList());
      classPropertiesBuilder.addAll(propertiesElements);

      return classPropertiesBuilder.build();
   }

   protected GNode createPackageNode(final Package umlPackage) {
      Map<String, Object> layoutOptions = new HashMap<>();
      layoutOptions.put(H_ALIGN, GConstants.HAlign.CENTER);
      layoutOptions.put(H_GRAB, false);
      layoutOptions.put(V_GRAB, false);

      GNodeBuilder packageNodeBuilder = new GNodeBuilder(Types.PACKAGE)
         .id(toId(umlPackage))
         .layout(GConstants.Layout.VBOX)
         .layoutOptions(layoutOptions)
         .addCssClass(CSS.PACKAGE);

      applyShapeData(umlPackage, packageNodeBuilder);

      GNode packageNode = packageNodeBuilder.build();

      // create compartment header
      GCompartment headerCompartment = createPackageHeader(umlPackage, packageNodeBuilder);
      packageNode.getChildren().add(headerCompartment);

      // create structure compartment
      GCompartment structureCompartment = createStructureCompartment(umlPackage);

      // add all nested packages into structure compartment
      List<GModelElement> childPackages = umlPackage.getPackagedElements().stream()
         .filter(Package.class::isInstance)
         .map(Package.class::cast)
         .map(this::createPackageNode)
         .collect(Collectors.toList());
      structureCompartment.getChildren().addAll(childPackages);

      packageNode.getChildren().add(structureCompartment);

      return packageNode;
   }

   private GCompartment createPackageHeader(final Package umlPackage, final GNodeBuilder packageNodeBuilder) {

      GLabel packageHeaderLabel = new GLabelBuilder(Types.LABEL_PACKAGE_NAME)
         .id(UmlIDUtil.createLabelNameId(toId(umlPackage)))
         .text(umlPackage.getName())
         .build();

      Map<String, Object> layoutOptions = new HashMap<>();
      GCompartment packageHeader = new GCompartmentBuilder(Types.COMPARTMENT_HEADER)
         .id(UmlIDUtil.createHeaderLabelId(toId(umlPackage)))
         .layout(GConstants.Layout.HBOX)
         .layoutOptions(layoutOptions)
         .add(packageHeaderLabel)
         .build();

      return packageHeader;
   }

   private GCompartment createStructureCompartment(final Package umlPackage) {
      Map<String, Object> layoutOptions = new HashMap<>();
      layoutOptions.put(H_ALIGN, GConstants.HAlign.LEFT);
      layoutOptions.put(H_GRAB, true);
      layoutOptions.put(V_GRAB, true);
      GCompartment structCompartment = new GCompartmentBuilder(Types.STRUCTURE)
         .id(toId(umlPackage) + "_struct")
         .layout(GConstants.Layout.FREEFORM)
         .layoutOptions(layoutOptions)
         .addCssClass("struct")
         .build();
      return structCompartment;
   }

}
