---
status: {proposed}
date: {2024-04-12}
deciders: {whole team}
---
# PROPOSED: Own recipe images could be stored in a different tier storage 

## Context and Problem Statement
The images for the recipes have a size bigger than 1.2 GB. To de-couple them from the backend resources, they should be stored at a reasonable storage tier in the cloud environment.

<!-- This is an optional element. Feel free to remove. -->
## Decision Drivers
To manage the costs for the storage, it should be evaluated how frequently the recipe images are accessed and which tiers are available in the different cloud environments.

## Considered Options
tbd

## Decision Outcome
tbd

### Consequences
* Good, because {positive consequence, e.g., improvement of one or more desired qualities, …}
* Bad, because {negative consequence, e.g., compromising one or more desired qualities, …}
* … <!-- numbers of consequences can vary -->

### Confirmation
{Describe how the implementation of/compliance with the ADR is confirmed. E.g., by a review or an ArchUnit test.
 Although we classify this element as optional, it is included in most ADRs.}
