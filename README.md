# clientServerPDL2021

## Overview

An overview

## Install and run

### With maven

Install dependencies

```bash
mvn clean install
```

Run tomcat server

```bash
mvn --projects backend spring-boot:run
```

### Without maven install

Install dependencies

```bash
chmod +x ./mvnw
./mvnw clean install
```

Run tomcat server

```bash
./mvnw --projects backend spring-boot:run
```

### For development

Run frontend server

> If `node` and `npm` is install on your system replace `npm` by `./node/npm`.

```bash
npm run serve
```
