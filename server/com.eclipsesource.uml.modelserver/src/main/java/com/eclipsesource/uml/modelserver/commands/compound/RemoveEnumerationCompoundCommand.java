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
package com.eclipsesource.uml.modelserver.commands.compound;

import java.util.Collection;

import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil.UsageCrossReferencer;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Enumeration;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;

import com.eclipsesource.uml.modelserver.commands.notation.RemoveEnumerationShapeCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.RemoveEnumerationCommand;
import com.eclipsesource.uml.modelserver.commands.semantic.SetPropertyTypeCommand;
import com.eclipsesource.uml.modelserver.commands.util.UmlSemanticCommandUtil;

public class RemoveEnumerationCompoundCommand extends CompoundCommand {

   public RemoveEnumerationCompoundCommand(final EditingDomain domain, final URI modelUri,
      final String semanticUriFragment) {
      this.append(new RemoveEnumerationCommand(domain, modelUri, semanticUriFragment));
      this.append(new RemoveEnumerationShapeCommand(domain, modelUri, semanticUriFragment));

      Model umlModel = UmlSemanticCommandUtil.getModel(modelUri, domain);
      Enumeration enumerationToRemove = UmlSemanticCommandUtil.getElement(umlModel, semanticUriFragment,
         Enumeration.class);

      Collection<Setting> usagesEnumeration = UsageCrossReferencer.find(enumerationToRemove, umlModel.eResource());
      for (Setting setting : usagesEnumeration) {
         EObject eObject = setting.getEObject();
         if (isPropertyTypeUsage(setting, eObject, enumerationToRemove)) {
            String propertyUriFragment = UmlSemanticCommandUtil.getSemanticUriFragment((Property) eObject);
            this.append(new SetPropertyTypeCommand(domain, modelUri, propertyUriFragment, null));
         }
      }
   }

   protected boolean isPropertyTypeUsage(final Setting setting, final EObject eObject,
      final Enumeration enumerationToRemove) {
      return eObject instanceof Property
         && eObject.eContainer() instanceof Class
         && setting.getEStructuralFeature().equals(UMLPackage.Literals.TYPED_ELEMENT__TYPE)
         && enumerationToRemove.equals(((Property) eObject).getType());
   }

}
