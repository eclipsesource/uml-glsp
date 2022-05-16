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
import { moveFeature, popupFeature, SChildElement, selectFeature } from "@eclipse-glsp/client";
import {
    boundsFeature,
    Deletable,
    deletableFeature,
    EditableLabel,
    editLabelFeature,
    fadeFeature,
    Hoverable,
    hoverFeedbackFeature,
    isEditableLabel,
    layoutableChildFeature,
    layoutContainerFeature,
    Nameable,
    nameFeature,
    RectangularNode,
    SCompartment,
    Selectable,
    SLabel,
    SShapeElement,
    WithEditableLabel,
    withEditLabelFeature
} from "sprotty/lib";
import { UmlTypes } from "./utils";

export class LabeledNode extends RectangularNode implements WithEditableLabel, Nameable {

    get editableLabel(): (SChildElement & EditableLabel) | undefined {
        const headerComp = this.children.find(element => element.type === "comp:header");
        if (headerComp) {
            const label = headerComp.children.find(element => element.type === "label:heading");
            if (label && isEditableLabel(label)) {
                return label;
            }
        }
        return undefined;
    }

    get name(): string {
        if (this.editableLabel) {
            return this.editableLabel.text;
        }
        return this.id;
    }
    hasFeature(feature: symbol): boolean {
        return super.hasFeature(feature) || feature === nameFeature || feature === withEditLabelFeature;
    }
}

export class IconLabelCompartment extends SCompartment implements Selectable, Deletable, Hoverable {
    selected = false;
    hoverFeedback = false;

    hasFeature(feature: symbol): boolean {
        return super.hasFeature(feature) || feature === selectFeature || feature === deletableFeature || feature === hoverFeedbackFeature;
    }
}

export class SEditableLabel extends SLabel implements EditableLabel {
    hasFeature(feature: symbol): boolean {
        return feature === editLabelFeature || super.hasFeature(feature);
    }
}

export class Icon extends SShapeElement {
    iconImageName: string;

    hasFeature(feature: symbol): boolean {
        return feature === boundsFeature || feature === layoutContainerFeature || feature === layoutableChildFeature || feature === fadeFeature;
    }
}

export class IconClass extends Icon {
    iconImageName = "Class.svg";
}

export class IconProperty extends Icon {
    iconImageName = "Property.svg";
}

export class PackageNode extends RectangularNode implements Nameable, WithEditableLabel {
    static override readonly DEFAULT_FEATURES = [
        deletableFeature,
        selectFeature,
        boundsFeature,
        moveFeature,
        layoutContainerFeature,
        fadeFeature,
        hoverFeedbackFeature,
        popupFeature,
        nameFeature,
        withEditLabelFeature
    ];

    name = "";

    get editableLabel(): (SChildElement & EditableLabel) | undefined {
        const label = this.children.find(element => element.type === UmlTypes.LABEL_PACKAGE_NAME);
        if (label && isEditableLabel(label)) {
            return label;
        }
        return undefined;
    }
}
