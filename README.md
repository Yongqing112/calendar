# 📅 Calendar System - Event Management REST API

A modern Spring Boot REST API for managing calendar events with session-based authentication and PostgreSQL persistence. Built with Java 21 LTS.

## ✨ Features

### Core Functionality
- **Create Events** - Add new calendar events with custom details
- **Read Events** - Retrieve all events or fetch specific events by ID
- **Update Events** - Modify event titles, descriptions, times, and event types
- **Delete Events** - Remove events from the calendar
- **Session Management** - JDBC-based session storage for user persistence

### Technical Highlights
- ✅ RESTful API design with proper HTTP status codes
- ✅ CORS support for Angular frontend (`http://localhost:4200`)
- ✅ PostgreSQL database with automatic schema generation
- ✅ Jakarta EE / Hibernate JPA for ORM
- ✅ Built with Java 21 LTS

## 🛠️ Technology Stack

- **Java**: 21 LTS
- **Framework**: Spring Boot 3.5.3
- **Database**: PostgreSQL 15
- **Build Tool**: Maven
- **ORM**: Hibernate/Jakarta JPA
- **Session Storage**: Spring Session with JDBC

## 📋 Prerequisites

- **Java 21 LTS** (already configured in the project)
- **Docker** (for PostgreSQL container)
- **Maven** (included via Maven Wrapper)
- **Node.js** (for Angular frontend development)

## 🚀 Quick Start

### Step 1: Start PostgreSQL with Docker

Run the command to launch PostgreSQL:

```bash
docker run --name my-postgres \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=calendar \
  -p 5432:5432 \
  -d postgres:15
```
# 📅 Calendar System

A simple event-calendar REST API that lets authenticated users create, read, update, and delete calendar events.

## ✨ What this calendar does

- **Create events:** add events with title, description, start/end times, and event type.
- **Read events:** list all events or fetch a single event by ID.
- **Update events:** modify an event's details and time range.
- **Delete events:** remove events by ID.
- **Session support:** keeps user session state via JDBC-backed sessions.

For detailed setup and project run instructions, see PROJECT_SETUP.md
Connect to PostgreSQL to verify it's running:
