# Nightfall: The Hollow

A slow-burn Forge 1.20.1 horror mod. The goal is not to keep a fear meter full, but to make the ordinary world feel subtly wrong—and give one persistent presence room to become frightening.

## The experience

- **The first days are quiet.** Small anomalies are rolled independently per player and per in-game day. After the grace period, sometimes a distant figure is staged just outside your view in a dark patch; if no safe place is available, cave ambience sounds from behind you. There are no chat pop-ups explaining the scare. Waking from sleep has its own rare audio anomaly.
- **The Hollow is a watcher, not a buffed zombie.** It is rare and silent while observing. After the configured early-game grace period, it can appear naturally or as a distant anomaly sighting in dark Overworld locations.
- **It has three behaviors:** it watches from a distance; it creeps closer when you are not looking; and if you corner or attack it, it hunts for a short burst. If you stare at it long enough, it vanishes. A hit can briefly blind you.
- **The Echo Bell is a countermeasure.** Craft it from copper, iron, amethyst, and an Echo Shard. Right-click to damage, stagger, and drive nearby Hollows back into their watching behavior. It has a 45-second cooldown.
- **Abandoned sites** give exploration a purpose. Rare roadside waystations and raised watchposts contain survival supplies, occasional Echo Shards, and **Field Notes** with a small unfolding story. They generate in new Overworld chunks; use `/locate structure nightfall:abandoned_site` when testing.
- **The Hollow Spawn Egg** and Field Notes are also available in the Nightfall creative tab for testing and custom maps.

There is no Dread bar. Tension comes from the timing and behavior of the anomalies, the Hollow's encounter states, and what you find while exploring. This version focuses on one stalking entity and a small set of liminal sites rather than a large roster of dimensions and monsters.

## Install

1. Install **Minecraft Forge 47.x for Minecraft 1.20.1**.
2. Put `nightfall-1.0.0.jar` in your Minecraft `mods` folder.
3. Launch the Forge profile. New worlds stay quiet at first; explore at night and listen carefully.

## Build from source

Use Java 17, then run from the repository root:

```sh
./gradlew build
```

The mod jar will be in `build/libs/`. To launch the development client, run `./gradlew runClient`.

## Configuration

Forge creates `config/nightfall-common.toml`:

- `anomaliesEnabled` — enables rare distant sightings and ambient sound cues.
- `firstHollowDay` — earliest Overworld day for distant Hollow sightings and natural spawns (default `5`).
- `dailyAnomalyChance` — percent chance of one distant watcher sighting in darkness, or an ambient cue if no safe sighting location is found, per player per day after day 1 (default `14`).
- `sleepAnomalyChance` — percent chance of a quiet sound cue behind the player when waking (default `5`).

For both chances, a higher percentage means more frequent events. Natural spawn frequency is intentionally very low and is defined by a Forge biome modifier; datapacks can adjust it.
