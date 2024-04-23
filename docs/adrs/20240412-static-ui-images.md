---
status: {accepted}
date: {2024-04-12}
deciders: {whole team}
---
# Storage location of frontend's static UI images

## Context and Problem Statement

The frontend needs some static image resources which are used for the frontend design. The size of the images also increase the size of the builded docker image.

## Decision Drivers
Reducing the size of the builded frontend docker image.

## Considered Options
* Move the image resources somewhere else
* Sort out unused resources
* Leave the resources at it's current location

## Decision Outcome

Chosen option: "Sort out unused resources", because
it makes sense to keep the current location of the static frontend resources. When keeping the same location, the only useful decision is to sort out all resources that are and will not be used with the end of the project.

### Consequences
Good, because it reduces the size of the builded docker image.

### Confirmation
The ADR is confirmed when the static resources are sorted out shortly before the project ends.