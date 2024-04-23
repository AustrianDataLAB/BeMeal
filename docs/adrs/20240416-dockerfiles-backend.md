---
status: { proposed }
date: { 2024-04-16 }
deciders: { whole team }
---

# Dockerfiles for Backend and Databases

## Context and Problem Statement

How should Dockerfiles for the backend and databases be configured to enhance security and manage data effectively, especially during upgrades?

## Decision Drivers

- Security of application environments.
- Data integrity and continuity during database upgrades.

## Considered Options

1. Backend Dockerfile configurations, including non-root user setups.
2. Strategies for managing database schemas and existing data during downtime (migrations).

## Decision Outcome

Chosen option: "Use non-root users in backend Dockerfiles and implement robust migration strategies for databases", because it increases security and reduces potential downtime impacts during upgrades.

## Consequences

- Good, because running as a non-root user minimizes security risks.
- Good, because planned migrations ensure data integrity and availability.
