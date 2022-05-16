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
import { DefaultTypes } from "@eclipse-glsp/protocol";

export namespace UmlTypes {

    export const ICON = "icon";
    export const LABEL_NAME = `${DefaultTypes.LABEL}:name`;
    export const LABEL_TEXT = `${DefaultTypes.LABEL}:text`;
    export const LABEL_EDGE_NAME = `${DefaultTypes.LABEL}:edge-name`;
    export const LABEL_EDGE_MULTIPLICITY = `${DefaultTypes.LABEL}:edge-multiplicity`;
    export const ICON_CLASS = `${ICON}:class`;
    export const CLASS = `${DefaultTypes.NODE}:class`;
    export const ASSOCIATION = `${DefaultTypes.EDGE}:association`;
    export const PROPERTY = `${DefaultTypes.COMPARTMENT}:property`;
    export const ICON_PROPERTY = `${ICON}:property`;
    export const LABEL_PROPERTY_NAME = `${DefaultTypes.LABEL}:property:name`;
    export const LABEL_PROPERTY_TYPE = `${DefaultTypes.LABEL}:property:type`;
    export const LABEL_PROPERTY_MULTIPLICITY = `${DefaultTypes.LABEL}:property:multiplicity`;
    export const PACKAGE = `${DefaultTypes.NODE}:package`;
    export const STRUCTURE = "struct";
    export const LABEL_PACKAGE_NAME = `${DefaultTypes.LABEL}:package:name`;

}
