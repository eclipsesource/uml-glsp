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
import java.util.stream.Collectors;

import org.eclipse.glsp.graph.GCompartment;
import org.eclipse.glsp.graph.GNode;
import org.eclipse.glsp.graph.builder.impl.GCompartmentBuilder;
import org.eclipse.glsp.graph.builder.impl.GLabelBuilder;
import org.eclipse.glsp.graph.builder.impl.GLayoutOptions;
import org.eclipse.glsp.graph.builder.impl.GNodeBuilder;
import org.eclipse.glsp.graph.util.GConstants;
import org.eclipse.glsp.graph.util.GraphUtil;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;

import com.eclipsesource.uml.glsp.model.UmlModelState;
import com.eclipsesource.uml.glsp.util.UmlConfig.CSS;
import com.eclipsesource.uml.glsp.util.UmlConfig.Types;
import com.eclipsesource.uml.modelserver.unotation.Shape;

public class ClassifierNodeFactory extends AbstractGModelFactory<Classifier, GNode> {

   private final GModelFactory parentFactory;

   public ClassifierNodeFactory(final UmlModelState modelState, final GModelFactory parentFactory) {
      super(modelState);
      this.parentFactory = parentFactory;
   }

   @Override
   public GNode create(final Classifier classifier) {
      if (classifier instanceof Class) {
         return create((Class) classifier);
      }
      return null;
   }

   protected GNode create(final Class umlClass) {
      GNodeBuilder b = new GNodeBuilder(Types.CLASS) //
         .id(toId(umlClass)) //
         .layout(GConstants.Layout.VBOX) //
         .addCssClass(CSS.NODE) //
         .add(buildHeader(umlClass))//
         .add(createLabeledChildrenCompartment(umlClass.getAttributes(), umlClass));

      applyShapeData(umlClass, b);
      return b.build();
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

   protected GCompartment buildHeader(final Classifier classifier) {
      return new GCompartmentBuilder(Types.COMP_HEADER) //
         .layout("hbox") //
         .id(toId(classifier) + "_header").add(new GCompartmentBuilder(getType(classifier)) //
            .id(toId(classifier) + "_header_icon").build()) //
         .add(new GLabelBuilder(Types.LABEL_NAME) //
            .id(toId(classifier) + "_header_label").text(classifier.getName()) //
            .build()) //
         .build();
   }

   protected GCompartment createLabeledChildrenCompartment(final Collection<? extends Property> children,
      final Classifier parent) {
      return new GCompartmentBuilder(Types.COMP) //
         .id(toId(parent) + "_childCompartment").layout(GConstants.Layout.VBOX) //
         .layoutOptions(new GLayoutOptions() //
            .hAlign(GConstants.HAlign.LEFT) //
            .resizeContainer(true)) //
         .addAll(children.stream() //
            .map(parentFactory::create) //
            .collect(Collectors.toList()))
         .build();
   }

   protected static String getType(final Classifier classifier) {
      if (classifier instanceof Class) {
         return Types.ICON_CLASS;
      }
      return "Classifier not found";
   }
}
