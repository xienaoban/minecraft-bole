# Features

- [X] **`Buy Bole handbook from villagers`**
- [ ] **`Copy Bole handbook`**
- [X] **`Highlight entities around the player`**
  - [X] `Dyanamic view distance`
  - [X] `Costs experience`
- [X] **`Offer a spawn egg`**
- [X] **`Open the handbook screen when looking up`**
- [X] **`Open the player or mount screen when looking down`**
- [ ] **`Check beehive state`**
- Entity:
  - [X] `Bounding box`
  - [X] `Custom name`
  - [X] `Nether portal cooldown`
  - [X] `Set nether portal cooldown`
    - [X] `Players can only change their own cooldown`
  - [X] `Set custom name visible`
  - [X] `Shut up`
  - [X] `Set invulnerable`
  - [X] `Air`
  - LivingEntity:
    - [X] `Health`
    - [X] `Max health`
    - [X] `Is baby`
    - [X] `Status effects`
    - PlayerEntity
      - [X] `Experience`
    - MobEntity:
      - [X] `Attractive items` (goalSelector - TemptGoal)
      - [X] `Can be leashed` (canBeLeashedBy)
      - [X] `No AI`
      - PathAwareEntity:
        - PassiveEntity:
          - [X] `Set baby / Never grow up` (breedingAge)
          - AnimalEntity:
            - [X] `Breeding item` (isBreedingItem)
            - TameableEntity:
              - [X] `Is tamed`
              - [ ] `Set owner`
              - [X] `Block accidental injury`
              - [X] `Teleport to the owner on the sea and leaves`
              - TameableShoulderEntity:
                - [X] `Set cooldown of sitting on player`
                - ParrotEntity:
                  - [X] `Set variant`
              - CatEntity:
                - [X] `Set variant`
            - HorseBaseEntity:
              - AbstractDonkeyEntity
                - [X] `Chest size`
              - HorseEntity:
                - [X] `Running speed`
                - [X] `Jump height`
                - [X] `Set variant`
            - BeeEntity:
              - [X] `Beehive position`
              - [X] `Reset beehive position`
            - SheepEntity:
              - [X] `Force to eat grass`
            - GoatEntity:
              - [X] `Set screamer`
            - RabbitEntity:
              - [ ] `Set variant`
            - PandaEntity:
              - [X] `Set gene`
            - AxolotlEntity:
              - [X] `Set variant`
          - MerchantEntity:
            - [X] `Get inventory`
            - [X] `Set inventory`
            - VillagerEntity:
              - [X] `Highlight job site`
              - [X] `Reset level 0`
              - [X] `Force  to restock`
              - [X] `Change clothes`
            - WanderingTraderEntity:
              - [X] `Notify players when spawned`
              - [X] `Despawn time`
              - [X] `Add Despawn time`
        - WaterCreatureEntity
          - FishEntity
            - SchoolingFishEntity
              - TropicalFishEntity
                - [X] `Set variant`
        - HostileEntity:
          - AbstractPiglinEntity:
            - PiglinEntity:
              - [ ] `Force to dance`

# Screenshots

![Screenshot1](assets/screen1.png)

![Screenshot2](assets/screen2.png)

![Screenshot3](assets/screen3.png)
