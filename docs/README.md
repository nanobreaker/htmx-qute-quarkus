# Preview

<div style="margin-top: 2rem" align="center">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="logo/demo-dark.png">
      <img alt="demo" src="logo/demo-light.png">
    </picture>
</div>

## Motivation

I have always disliked the way most web applications are built. My gut feeling told me that something was wrong, but because I lacked
experience and did not fully understand how the web had evolved, I could not clearly explain where things had gone off track.

Frameworks like Angular, React, Vue, and others often feel bloated to me. They seem to suffer from a fundamental problem at the root of
their design, which is why they constantly try to reinvent themselves. In my opinion, the culprit is that many modern frameworks ignore what
the browser already gives us out of the box. Combined with REST and hypermedia, the web should be simple. Instead, we have made it
unnecessarily complicated.

At some point, I discovered htmx, a hypermedia library created by Carson Gross. It described a way of building web applications that deeply
resonated with me, and I felt that this was the right direction. I bought the book, and by the end of that journey, I was convinced that
much of the modern web had taken the wrong path. For many applications, the true way to build slim and efficient web interfaces is to
embrace hypermedia.

This project is my playground for exploring how to build a hypermedia-driven web application.

# Overview

My personal attempt to implement a todo application and learn new technologies along the way. This project sheds light on how to build a
slim and efficient hypermedia-driven web application backed by the reactive Quarkus framework.

Hexagon architecture is used as a fundament, I really like the way how it allows me to split application in distinct layers and keep system
decoupled. Additionally, it very easy to scale and maintain.

For the UI I chose a TUI-like interface implemented with the help of a [webtui](https://github.com/webtui/webtui) library.

## Project Structure

```
.
├── adapters
│   ├── inbound
│   │   └── rest-adapter
│   └── outbound
│       └── jpa-adapter
├── app
├── boot
├── core
├── docs
├── e2e
├── framework
└── library
```

### Adapters

Adapters are implementations of the port interfaces defined in the core layer. Like ports, there are two kinds of adapters: inbound and
outbound. Inbound adapters describe how external systems communicate with the domain, while outbound adapters describe how the domain
interacts with external systems.

#### rest-adapter

An inbound REST adapter that provides HTTP endpoints for interacting with the todo application. It uses htmx and Qute templates to render
the UI and handle user interactions.

#### jpa-adapter

An outbound adapter that provides JPA repositories for interacting with the database.

### App

The application and business logic layer. It defines handlers for inbound calls and relies on outbound adapters to interact with the
database.

### Boot

The bootstrapping layer is responsible for starting the Quarkus application. It also contains application configuration.

### Core

The core layer that defines domain models and interfaces, also known as ports.

### Docs

Project documentation.

### E2E

Integration tests.

### Framework

A custom framework implementation is used to reduce the amount of boilerplate code.

### Library

A simple helper library that contains my custom implementations, such as Option and Tuple, inspired by Rust.

## Usage

You can create, list, update, delete todos using the command line interface. To list available commands you can simply run `help`, to
explore further just run `todo help`, `todo create help` and so on. Additionally, I implemented user and calendar commands that give you
information about your user and a small calendar that reflects todos with timelines on it.

To select CLI from anywhere just press `Shift+:`.

### Login

![login](images/login.png)

### Command Line Interface

![commands](images/cli.png)

### Todos

![todos](images/todos.png)

### User

![users](images/user.png)

### Calendar

![calendar](images/calendar.png)

# Getting Started

## Prerequisites

* maven 3.8.6+
* java 25+
* docker

## Installation

Since I don't provide any binary distribution, you need to clone the repository and build the app yourself.

```shell
git clone git@github.com:nanobreaker/htmx-qute-quarkus.git
```

## Testing

Running the tests requires Docker, because the integration tests start database and identity provider containers.

```shell
mvn verify
```

## Building

```shell
mvn clean install
```

## Running

For local development, you can rely on Quarkus dev mode. It automatically starts all required Docker containers and runs the application.

```shell
./mvnw quarkus:dev
```

To run the jar binary, you need to set up the database and identity provider first, then start the application.

```shell
java -jar boot/target/quarkus-app/quarkus-run.jar
```

# Licensing

The code in this project is licensed under MIT license. Check [LICENSE](LICENSE) for further details.