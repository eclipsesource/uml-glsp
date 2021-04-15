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
package com.eclipsesource.uml.glsp.gmodel;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.glsp.graph.GCompartment;
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
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.EnumerationLiteral;
import org.eclipse.uml2.uml.Property;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.CSS;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.glsp.util.UmlIDUtil;
import com.eclipsesource.uml.modelserver.unotation.Shape;

public class ClassifierNodeFactory extends AbstractGModelFactory<Classifier, GNode> {

   private final LabelFactory labelFactory;

   public ClassifierNodeFactory(final UmlModelState modelState, final LabelFactory labelFactory) {
      super(modelState);
      this.labelFactory = labelFactory;
   }

   @Override
   public GNode create(final Classifier classifier) {
      if (classifier instanceof Class) {
         return createClassNode((Class) classifier);
      } else if (classifier instanceof Enumeration) {
         return createEnumerationNode((Enumeration) classifier);
      }
      return null;
   }

   protected void applyShapeData(final Classifier classifier, final GNodeBuilder builder) {
      modelState.getIndex().getNotation(classifier, Shape.class).ifPresent(shape -> {
         if (shape.getPosition() != null) {
            builder.position(GraphUtil.copy(shape.getPosition()));
         } else if (shape.getSize() != null) {
            builder.size(GraphUtil.copy(shape.getSize()));
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
      GCompartmentBuilder classHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
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
      GCompartmentBuilder classPropertiesBuilder = new GCompartmentBuilder(Types.COMP)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent))).layout(GConstants.Layout.VBOX);

      GLayoutOptions layoutOptions = new GLayoutOptions()
         .hAlign(GConstants.HAlign.LEFT)
         .resizeContainer(true);
      classPropertiesBuilder.layoutOptions(layoutOptions);

      List<GModelElement> propertiesLabels = properties.stream()
         .map(labelFactory::createPropertyLabel)
         .collect(Collectors.toList());
      classPropertiesBuilder.addAll(propertiesLabels);

      return classPropertiesBuilder.build();
   }

   protected GNode createEnumerationNode(final Enumeration umlEnumeration) {
      GNodeBuilder enumerationBuilder = new GNodeBuilder(Types.ENUMERATION)
         .id(toId(umlEnumeration))
         .layout(GConstants.Layout.VBOX)
         .addCssClass(CSS.NODE);

      applyShapeData(umlEnumeration, enumerationBuilder);

      GCompartment enumerationHeader = buildEnumerationHeader(umlEnumeration);
      enumerationBuilder.add(enumerationHeader);

      GCompartment enumLiteralscompartment = buildEnumLiteralsCompartment(umlEnumeration.getOwnedLiterals(),
         umlEnumeration);
      enumerationBuilder.add(enumLiteralscompartment);

      return enumerationBuilder.build();
   }

   protected GCompartment buildEnumerationHeader(final Enumeration umlEnumeration) {
      GCompartmentBuilder outerEnumHeaderBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.VBOX)
         .id(UmlIDUtil.createOuterHeaderId(toId(umlEnumeration)));

      GLabel headerTypeLabel = new GLabelBuilder(Types.LABEL_TEXT)
         .id(toId(umlEnumeration) + "_header_type")
         .text("<<" + Enumeration.class.getSimpleName() + ">>").build();
      outerEnumHeaderBuilder.add(headerTypeLabel);

      GCompartmentBuilder headerCompartmentBuilder = new GCompartmentBuilder(Types.COMP_HEADER)
         .layout(GConstants.Layout.HBOX)
         .id(UmlIDUtil.createHeaderId(toId(umlEnumeration)));

      GCompartment enumHeaderIcon = new GCompartmentBuilder(Types.ICON_ENUMERATION)
         .id(UmlIDUtil.createHeaderIconId(toId(umlEnumeration))).build();
      headerCompartmentBuilder.add(enumHeaderIcon);

      GLabel enumHeaderLabel = new GLabelBuilder(Types.LABEL_NAME)
         .id(UmlIDUtil.createHeaderLabelId(toId(umlEnumeration))).text(umlEnumeration.getName()).build();
      headerCompartmentBuilder.add(enumHeaderLabel);

      GCompartment enumHeaderCompartment = headerCompartmentBuilder.build();
      outerEnumHeaderBuilder.add(enumHeaderCompartment);

      return outerEnumHeaderBuilder.build();
   }

   protected GCompartment buildEnumLiteralsCompartment(final Collection<? extends EnumerationLiteral> enumLiterals,
      final Enumeration parent) {
      GCompartmentBuilder childrenCompartmentBuilder = new GCompartmentBuilder(Types.COMP)
         .id(UmlIDUtil.createChildCompartmentId(toId(parent)))
         .layout(GConstants.Layout.VBOX);

      GLayoutOptions layoutOptions = new GLayoutOptions()
         .hAlign(GConstants.HAlign.LEFT)
         .resizeContainer(true);

      childrenCompartmentBuilder.layoutOptions(layoutOptions);

      List<GModelElement> enumLiteralLabels = enumLiterals.stream()
         .map(literal -> this.labelFactory.createEnumLiteralLabel(literal))
         .collect(Collectors.toList());
      childrenCompartmentBuilder.addAll(enumLiteralLabels);

      return childrenCompartmentBuilder.build();
   }
}
