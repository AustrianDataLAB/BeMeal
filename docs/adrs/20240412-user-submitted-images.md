---
status: {accepted}
date: {2024-04-12}
deciders: {whole team}
---
# Store user-submitted recipe images in a dedicated bucket 

## Context and Problem Statement
Users submit images of their meal creations to the BeMeal application. Those images must be stored to ensure the functionality of the challenge voting system.

## Decision Drivers
* The user submitted images should be stored separately from the recipe images
* The storage location of the user-submitted images is expected to have higher read and write activities

## Considered Options
* Create an image container (bucket) where the images are stored
* Leave as it is: the user-submitted images are stored in the backend environment

## Decision Outcome
Chosen option: "Create an image container (bucket) where the images are stored", because
this results in a more clean architecture. 

### Consequences
* Good, because the backend handles the storage process but acts not as storage location
* Good, because the user-submitted images are stored separately from all other resources

<!-- This is an optional element. Feel free to remove. -->
### Confirmation
The ADR is confirmed when the user-submitted recipe images are stored in a dedicated bucket.