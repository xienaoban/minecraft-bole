todo: test on server

# Features

- [ ] **`Buy Bole handbook from villagers`**
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
      - [ ] ~~`Speed` (goalSelector)~~
      - [X] `Attractive items` (goalSelector - TemptGoal)
      - [X] `Can be leashed` (canBeLeashedBy)
      - [ ] ~~`Loot table`~~
      - [ ] ~~`Hand item`~~
      - [ ] ~~`Grab hand item`~~
      - [X] `No AI`
      - PathAwareEntity:
        - PassiveEntity:
          - [X] `Set baby / Never grow up` (breedingAge)
          - AnimalEntity:
            - [X] `Breeding item` (isBreedingItem)
            - TameableEntity:
              - [X] `Is tamed`
              - [ ] `Set owner`
              - [X] `Invulnerable pets`
              - TameableShoulderEntity:
                - [X] `Set cooldown of sitting on player`
                - ParrotEntity:
                  - [X] `Set parrot variant`
              - CatEntity:
                - [X] `Set cat variant`
            - HorseBaseEntity:
              - AbstractDonkeyEntity
                - [X] `Chest size`
              - HorseEntity:
                - [X] `Running speed`
                - [X] `Jump height`
                - [X] `Set horse variant`
            - BeeEntity:
              - [X] `Beehive position`
              - [X] `Reset beehive position`
            - SheepEntity:
              - [X] `Force eat grass`
            - GoatEntity:
              - [X] `Set screamer`
            - ChickenEntity:
              - [ ] `No egg laying`
            - PandaEntity:
              - [X] `Set gene`
            - AxolotlEntity:
              - [X] `Set axolotl variant`
          - MerchantEntity:
            - [X] `Get inventory`
            - [ ] `Set inventory` (Open a new HandledScreen?)
            - VillagerEntity:
              - [X] `Highlight job site`
              - [X] `Reset level 0`
              - [X] `Force restock`
              - [X] `Change clothes`
            - WanderingTraderEntity:
              - [X] `Notify players when spawned`
              - [X] `Despawn time`
              - [X] `Add Despawn time`
        - HostileEntity:
          - Nothing here yet

# Screenshots

![Screenshot1](assets/screen1.png)

![Screenshot2](assets/screen2.png)

![Screenshot3](assets/screen3.png)
