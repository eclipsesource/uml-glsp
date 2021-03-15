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
import { MenuContribution, MenuModelRegistry } from "@theia/core";
import {
    CommonMenus,
    OpenerService,
    QuickOpenItem,
    QuickOpenMode,
    QuickOpenOptions,
    QuickOpenService
} from "@theia/core/lib/browser";
import { Command, CommandContribution, CommandRegistry, CommandService } from "@theia/core/lib/common/command";
import { QuickOpenModel } from "@theia/core/lib/common/quick-open-model";
import URI from "@theia/core/lib/common/uri";
import { FileSystem } from "@theia/filesystem/lib/common/filesystem";
import { FileNavigatorCommands, NavigatorContextMenu } from "@theia/navigator/lib/browser/navigator-contribution";
import { WorkspaceService } from "@theia/workspace/lib/browser";
import { inject, injectable } from "inversify";

import { UmlModelServerClient } from "../common/uml-model-server-client";

export const NEW_UML_CLASS_DIAGRAM_COMMAND: Command = {
    id: "file.newUmlClassDiagram",
    category: "File",
    label: "New UML Class Diagram",
    iconClass: "umlmodelfile"
};

@injectable()
export class UmlModelContribution implements CommandContribution, MenuContribution {

    @inject(FileSystem) protected readonly fileSystem: FileSystem;
    @inject(CommandService) protected readonly commandService: CommandService;
    @inject(OpenerService) protected readonly openerService: OpenerService;
    @inject(QuickOpenService) protected readonly quickOpenService: QuickOpenService;
    @inject(WorkspaceService) protected readonly workspaceService: WorkspaceService;
    @inject(UmlModelServerClient) protected readonly modelServerClient: UmlModelServerClient;

    registerCommands(registry: CommandRegistry): void {
        registry.registerCommand(NEW_UML_CLASS_DIAGRAM_COMMAND, {
            execute: () => {
                let workspaceUri: URI = new URI();
                if (this.workspaceService.tryGetRoots().length) {
                    workspaceUri = new URI(this.workspaceService.tryGetRoots()[0].uri);

                    this.showInput("Name", "Name of UML Class Diagram", nameOfUmlModel => {
                        this.createUmlClassDiagram(nameOfUmlModel, workspaceUri);
                    });
                }
            }
        });
    }

    protected showInput(hint: string, prefix: string, onEnter: (result: string) => void): void {
        const quickOpenModel: QuickOpenModel = {
            onType(lookFor: string, acceptor: (items: QuickOpenItem[]) => void): void {
                const dynamicItems: QuickOpenItem[] = [];
                const suffix = "Press 'Enter' to confirm or 'Escape' to cancel.";

                dynamicItems.push(new SingleStringInputOpenItem(
                    `${prefix}: '${lookFor}' ${suffix}`,
                    () => onEnter(lookFor),
                    (mode: QuickOpenMode) => mode === QuickOpenMode.OPEN,
                    () => false
                ));

                acceptor(dynamicItems);
            }
        };
        this.quickOpenService.open(quickOpenModel, this.getOptions(hint, false));
    }

    protected createUmlClassDiagram(classDiagramName: string, workspaceUri: URI): void {
        if (classDiagramName) {
            this.modelServerClient.create(classDiagramName).then(() => {
                this.quickOpenService.hide();
                const modelUri = new URI(workspaceUri.path.toString() + `/${classDiagramName}/model/${classDiagramName}.uml`);
                this.commandService.executeCommand(FileNavigatorCommands.REFRESH_NAVIGATOR.id);
                this.openerService.getOpener(modelUri).then(openHandler => {
                    openHandler.open(modelUri);
                    this.commandService.executeCommand(FileNavigatorCommands.REVEAL_IN_NAVIGATOR.id);
                });
            });
        }
    }

    registerMenus(menus: MenuModelRegistry): void {
        menus.registerMenuAction(CommonMenus.FILE_NEW, {
            commandId: NEW_UML_CLASS_DIAGRAM_COMMAND.id,
            label: NEW_UML_CLASS_DIAGRAM_COMMAND.label,
            icon: NEW_UML_CLASS_DIAGRAM_COMMAND.iconClass,
            order: "0"
        });

        menus.registerMenuAction(NavigatorContextMenu.NAVIGATION, {
            commandId: NEW_UML_CLASS_DIAGRAM_COMMAND.id,
            label: NEW_UML_CLASS_DIAGRAM_COMMAND.label,
            icon: NEW_UML_CLASS_DIAGRAM_COMMAND.iconClass,
            order: "0"
        });

    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    private getOptions(placeholder: string, fuzzyMatchLabel = true, onClose: (canceled: boolean) => void = () => { }): QuickOpenOptions {
        return QuickOpenOptions.resolve({
            placeholder,
            fuzzyMatchLabel,
            fuzzySort: false,
            onClose
        });
    }

}

class SingleStringInputOpenItem extends QuickOpenItem {

    constructor(
        private readonly label: string,
        // eslint-disable-next-line @typescript-eslint/no-empty-function
        private readonly execute: (item: QuickOpenItem) => void = () => { },
        private readonly canRun: (mode: QuickOpenMode) => boolean = mode => mode === QuickOpenMode.OPEN,
        private readonly canClose: (mode: QuickOpenMode) => boolean = mode => true) {

        super();
    }

    getLabel(): string {
        return this.label;
    }

    run(mode: QuickOpenMode): boolean {
        if (!this.canRun(mode)) {
            return false;
        }
        this.execute(this);
        return this.canClose(mode);
    }

}
