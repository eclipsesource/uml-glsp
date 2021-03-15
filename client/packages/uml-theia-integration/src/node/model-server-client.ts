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
import { ModelServerPaths, RequestBody, Response, ResponseBody } from "@eclipse-emfcloud/modelserver-theia";
import { DefaultModelServerClient } from "@eclipse-emfcloud/modelserver-theia/lib/node";
import { injectable } from "inversify";

import { UmlModelServerClient } from "../common/uml-model-server-client";

@injectable()
export class UmlModelServerClientImpl extends DefaultModelServerClient implements UmlModelServerClient {

    async create(modelName: string): Promise<Response<string>> {
        const newModelUri = `${modelName}/model/${modelName}.uml`;
        const response = await this.restClient.post(`${ModelServerPaths.MODEL_CRUD}?modeluri=${newModelUri}`,
            RequestBody.from(
                {
                    "data": {
                        "eClass": "http://www.eclipse.org/uml2/5.0.0/UML#//Model",
                        "name": "model"
                    }
                }
            ));
        return response.mapBody(ResponseBody.asString);
    }
}

