# Voting System

This project is a distributed voting system that uses IceGrid for deployment and communication between different components. The system includes a broker, workers, and database-query nodes.

## Table of Contents

1. [Introduction](#introduction)
2. [Repository Structure](#repository-structure)
3. [Requirements](#requirements)
4. [Instructions](#instructions)
   - [Deploy the System](#1-deploy-the-system)
   - [Start the Publisher](#2-start-the-publisher)
   - [Start the Clients](#3-start-the-clients)
   - [Run Experiments](#4-run-experiments)
5. [Component Descriptions](#component-descriptions)
6. [Testing](#testing)
7. [Contributing](#contributing)
8. [License](#license)

## Introduction

This project is a distributed voting system that uses IceGrid for deployment and communication between different components. The system includes a broker, workers, and database-query nodes.

## Repository Structure

```
.gitattributes       # Git configuration attributes
.gitignore           # Git ignore rules
.gradle/             # Gradle wrapper files
.idea/               # IDE configuration files
.project             # Project configuration file
.settings/           # Project settings
build/               # Build output directory
build.gradle         # Gradle build script
client/              # Client application code
Contract.ice         # Ice interface definition
database/            # Database-related code
docs/                # Documentation files
gradle/              # Gradle wrapper files
gradlew              # Unix shell script to run Gradle
gradlew.bat          # Windows batch script to run Gradle
icegrid/             # IceGrid configuration files
publisher/           # Publisher application code
readme.md            # Project README file
settings.gradle      # Gradle settings script
VotingSystem/        # Main application code
worker/              # Worker application code
```

### Folder Descriptions

- **client/**: Contains the client application code that connects to the broker to obtain proxies for available workers and the publisher. It subscribes to the publisher and sends requests to the workers to query citizen IDs.
- **database/**: Contains the code related to database operations and queries. It receives requests from workers and performs queries on the PostgreSQL database.
- **docs/**: Contains documentation files for the project.
- **icegrid/**: Contains configuration files for IceGrid, which is used for deploying the distributed system.
- **publisher/**: Contains the publisher application code that dynamically registers with the broker and waits for clients to subscribe. It coordinates experiments by reading citizen ID files and distributing queries among subscribed clients.
- **VotingSystem/**: Contains the main application code for the voting system.
- **worker/**: Contains the worker application code that receives requests from clients, calculates prime factors, and redirects the request to the database node for processing.

## Requirements

To run the scripts, you need the following:

1. Ice
2. Java
3. PuTTY

## Instructions

### 1. Deploy the System

First, configure the hosts where the components will be deployed in the `deploy.ps1` script. This script will send the IceGrid registry/broker to a specific host, deploy the workers to other specified hosts, and do the same for the database-query nodes. Essentially, it sets up the entire distributed deployment with IceGrid.

Run the deployment script:

```sh
./deploy.ps1
```

### 2. Start the Publisher

Next, start a publisher connected to the broker. Replace `<broker-host>` with the actual host of the broker:

```sh
java -jar path/to/publisher.jar <broker-host>
```

### 3. Start the Clients

Then, start the clients using the `init_clients.ps1` script:

```sh
./init_clients.ps1
```

### 4. Run Experiments

In the publisher console, you can read files containing citizen IDs to be queried and perform experiments.

## Component Descriptions

- **Client**: Connects to the broker to obtain proxies for available workers and the publisher. It subscribes to the publisher (forming a publisher-subscriber pattern) and sends requests to the workers to query citizen IDs.
- **Broker / IceGrid (Registry)**: Acts as the guide for the entire distribution, redirecting clients or any other devices to the required components.
- **Publisher**: Dynamically registers with the broker and waits for clients to subscribe. It coordinates experiments by reading citizen ID files and distributing queries among subscribed clients for concurrent processing.
- **Worker**: Receives requests from clients, calculates prime factors, and redirects the request to the database node for processing.
- **Database**: Receives requests from workers and performs queries on the PostgreSQL database. It returns the results to the client using a callback.

Throughout this process, time measurements are taken to evaluate performance.

## Testing

Unit tests are provided for various components of the system. You can run the tests using the following command:

```sh
./gradlew test
```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

This README provides a brief introduction to the project and clear instructions on how to set it up and run it. If there are any additional details or configurations required, make sure to include them in the respective sections.