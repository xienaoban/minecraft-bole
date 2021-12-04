Toto List:
- Entity:
  - [ ] ~~`UUID`~~
  - [X] `Bounding Box`
  - [X] `Custom Name`
  - [X] `Nether Portal Cooldown`
  - [X] `Set Nether Portal Cooldown`
  - [X] `Set Custom Name Visible`
  - [X] `Shut Up` (isSilent)
  - LivingEntity:
    - [X] `Health`
    - [X] `Max Health`
    - [X] `Is Baby`
    - [X] `Status Effects`
    - MobEntity:
      - [ ] `Speed` (goalSelector)
      - [X] `Attractive Items` (goalSelector - TemptGoal)
      - [X] `Can Be Leashed` (canBeLeashedBy)
      - [ ] ~~`Loot Table`~~
      - [ ] ~~`Hand Item`~~
      - [ ] ~~`Grab Hand Item`~~
      - PathAwareEntity:
        - PassiveEntity:
          - [X] `Set Baby / Never Grow Up` (breedingAge)
          - AnimalEntity:
            - [X] `Breeding Item` (isBreedingItem)
            - TameableEntity:
              - [ ] `Set Owner`
            - HorseBaseEntity:
              - AbstractDonkeyEntity
                - [X] `Chest Size`
              - HorseEntity:
                - [X] `Running Speed`
                - [X] `Jump Height`
            - BeeEntity:
              - [ ] `Beehive Position`
            - SheepEntity:
              - [X] `Force Eat Grass`
          - MerchantEntity:
            - [X] `Get Inventory`
            - [ ] `Set Inventory`
            - VillagerEntity:
              - [ ] `Highlight Job Site`
              - [ ] `Reset Job Site`
              - [ ] `Reset Level 0`
              - [X] `Force Restock`
              - [X] `Change Clothes`
        - HostileEntity: