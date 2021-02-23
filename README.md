# uml-glsp

For more information, please visit the [EMF.cloud Website](https://www.eclipse.org/emfcloud/). If you have questions, contact us on our [spectrum chat](https://spectrum.chat/emfcloud/) and have a look at our [communication and support options](https://www.eclipse.org/emfcloud/contact/).

Uml GLSP provides a web-based editor for UML Models (including Diagrams), integrated with Eclipse Theia. It contains two components: one [GLSP](https://github.com/eclipse-glsp/glsp) language server (Server-side, written in Java), and one GLSP client extension to actually present the diagrams (Using [Sprotty](https://github.com/eclipse/sprotty-theia)). 

Uml GLSP can display an existing UML model as class diagram. The diagram layout will be persisted in an .unotation file next to the .uml file. The diagram editor also supports creation of new elements (Classes, Properties and Associations), as well as partial support for editing existing elements (Renaming, deleting...).

## Prerequisites

### Java
You need Java 11 to build the uml-glsp editor.

## Getting started

Clone the uml-glsp editor:

    git clone https://github.com/eclipsesource/uml-glsp.git

Build server and client with the following build-script. This script also copies the needed server artifacts to the client.

    cd uml-glsp
    ./build.sh


Run
  * Start the client as it is described in the [client README](client/README.md).
  * The copied backend artifacts are automatically launched on startup.
  * If you want to start the backends manually:
    * Execute the Java main classes: `com.eclipsesource.uml.glsp.UmlGLSPServerLauncher` and/or `com.eclipsesource.uml.glsp.modelserver.UmlModelServerLauncher`
    * For this to work with the client, you have to set the regarding `isRunning` flag of the UmlGlspLaunchOptions to `true`. The UmlGlspLaunchOptions are located in `client/uml-theia-integration/src/node/backend-extension.ts`. The modelserver backend only starts the jar if the ping is unsuccessful, therefore no action is required.


## Building and deploying via Docker
The client repo contains a [Dockerfile](client/README.md), that builds the entire client application. The image listens on 0.0.0.0:3000 for incoming requests from a browser.

For installing docker locally please consult [docker's installation description](https://docs.docker.com/install/) for your OS.

**The glsp-server needs to be built locally before you build the image**

**Building**
`docker build -t <imagename>:<tagname> .` 

**Running**
`docker run -it -p 3000:3000 --rm <imagename>:<tagname>`

After that you should be able to connect with your browser at localhost:3000.	
