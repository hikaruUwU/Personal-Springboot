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


**Gateway** **OpenResty (Lua)**

A high-performance web platform based on Nginx, used for reverse proxy, load balancing, and API routing.

**Mocker** **Json-Mocker**

A native mocker program running on 0.0.0.0:**8080**. It reads mock responses from **./data/reponse.data** and supports hot data updates instantly.

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

2.Json-Mocker(native/java)

placing your response.json relative to json-mocker.exe/json-mocker.jar.

example
```
"status/health": {
    "service_status": "UP",
    "database_connection": "OK",
    "timestamp": 1731333000
  }
```
will take effect on 127.0.0.1:8080/api/status/health

run mocker\json-mocker.exe to expose a Http EndPoint to **0.0.0.0:8080**

or run command
```
java -jar .\json-mocker.jar
```

3. Backend(Springboot)

Sync maven and run
```
./mvnw spring-boot:run
```

More configuration please visit application.properties.

Verify the backend is running on a non-conflicting port (e.g., 8081).

4.Gateway(powered byOpenResty)

Deployment: Deploy the configuration files from the **./openresty/conf directory** to your OpenResty/Nginx environment.

it is pre-configured with some configurations:

Static HTML hosting
```
    server {
        listen 80;
        location / {
            root html;
            index index.html index.htm;
        }
        ...
}
```

Rate limiting
```
limit_req_zone  $binary_remote_addr  zone=mylimit:5m  rate=100r/s;
```

Reserving proxy

```
 upstream backend_service {
        server 127.0.0.1:8080;
        keepalive 32;
        ...
    }
```

```
location /api/ {
   proxy_pass http://backend_service/;
        ...
}
```

Using command
```
nginx -t
```
to test if Nginx has successfully parsed your configuration.
