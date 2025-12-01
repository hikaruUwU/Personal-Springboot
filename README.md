## 💾 Personal-Basement Scaffold

This is a personal full-stack project base template integrating modern front-end and back-end technologies with a high-performance gateway. 

This scaffold provides pre-configured basic code and core dependencies to help developers quickly set up a project featuring **Spring Boot (Java)** for the backend, **Vite (Frontend)** for building, and **OpenResty (High-Performance Gateway)** for routing, and an easy-use **JSON Mocker** for data simulation.

### ⚙️ Technology Stack Overview

**Spring Boot (Java)** 

This is the Spring Boot backend template, pre-configured with core dependencies and utility code.

* **Required JDK:** **Java 21 or Higher**.
* **Core Dependencies (via Maven):**
    * **`web`**: For building **RESTful APIs**，tranditional servlet.
    * **`lombok`**: To reduce boilerplate code (e.g., getters/setters).
    * **`spring-boot-validation`**: For **validating** request data.
    * **`mysql-connector-j`**&**`hikari-CP`**: MySQL database driver.
    * **`mybatis-flex`**: Simple ORM for database operations.
    * **`AOP`**: AOP.
 
* ** Project Provided Tools & Feactures **
    * **`HTTPS Certification`**: personal-generated cert.
    * **`@InjectVia (String key,Class<? extends ResourceFinder<?>> resource,InjectType via())`**: inject object to your parameter when called, supporting REPOSITORY (by marking POJO PK with @InjectKey)，APPLICATION_CONTEXT,SESSION.
    * **`@Monitor (timer,argument,result,level,range)`**: Mark to log your method execution status, supporting managing level,timer,argument,result statistics and scope of effect.
    * **`@RequiredSession`**: Place on RestController Method to block requests without session.
    * **`@EnableAPIScanner`**: Plaace on Springboot Bean class to enable scanning, and explose /api (${api.exposure.path}) to show rest endpoints.
    * **`SQLLog4j2Config`**: Prefer to show SQL execution plan or not.
    * **`RequestChain`**: An regular global common InheritableThreadLocal.
    * **`@Valid`**: Providing Spring-validation.

**Frontend** **Vite / TypeScript**

The frontend project is set up using Vite for a fast development experience and TypeScript for code robustness, including basic codes.

* **Build Tool:** **Vite** is used for its blazing-fast cold start and instant Hot Module Replacement (HMR).
* **Language:** **TypeScript**
* **Core Dependencies (via npm):**
    * **`element-plus`**: A popular component library for building the **User Interface (UI)**.
    * **`vue-router`**: The official library for **managing routing** and navigation within the SPA.
    * **`pinia`**: The recommended lightweight store for **State Management** (replacing Vuex).
    * **`gprogress`**: A utility for displaying **page loading progress** bars.
    * **`vue-i18n`**: Provides robust capabilities for **Internationalization** (multi-language support).
    * **`axios`**: A widely used library for making **HTTP requests** (API calls).
    * **`scss`**: The preprocessor used for **styling** the application.


🚀 Usage Guide

This guide outlines the steps to start all components of the Personal-Basement scaffold: the Mocker, the Backend, the Frontend, and the Gateway.

 Pre-requisites
Ensure you have JDK 21+ and Node.js (LTS)(Author using v22.20.0) installed.

1.Frontend (Vue3.js + TS + Vite)

```
cd .\vite\
npm install
npm run dev // for development
```

 Using
```
npm run build
```
to compile vue to html, css, js to gateway hosting directory.

relative config is in vite.config.ts
```
build:{
        emptyOutDir: true,
        outDir: '../openresty/html'
    }
```

2. Backend(Springboot)

```
./mvnw spring-boot:run
```

Configuration visit application.properties.

Verify the backend is running on a non-conflicting port (e.g., 8081).

3. Hot update Deployment ( development enviroment )

Install git.

Switch user to projectRunnerOnlyUser for security and install middlewares & dependecies.

Create a bare repository and set user's shell to git shell.

Move post-receive to your test env linux and modify ${DEPLOY_DIR}.

Manual git --work-tree=${DEPLOY_DIR} checkout -f once, chown -R runner:runner(git:git / projectOnlyUser) to currentWorkDIR.

Move project.service to /etc/systemd/system and modify WorkingDirectory & Environment to register it as a system service.(this project using NVM).

Git add remote project@dev.machine:repo.

Now every git pull to dev.machine will trigger hot updating.
