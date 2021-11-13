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
    - MobEntity:
      - [ ] `Speed` (goalSelector)
      - [X] `Attractive Food` (goalSelector - TemptGoal)
      - [ ] ~~`Loot Table`~~
      - [ ] `Hand Item`
      - [ ] `Grab Hand Item`
      - PathAwareEntity:
        - PassiveEntity:
          - [X] `Set Baby / Never Grow Up` (breedingAge)
          - AnimalEntity:
            - [X] `Breeding Item` (isBreedingItem)
            - HorseBaseEntity:
              - HorseEntity:
                - [ ] `Running Speed`
                - [ ] `Jump Height`
            - BeeEntity:
              - [ ] `Beehive Position`
            - SheepEntity:
              - [ ] `Force Eat Grass`
          - MerchantEntity:
            - VillagerEntity:
              - [ ] `Reset Job Site`
              - [ ] `Reset Level 0`
              - [ ] `Set Inventory`
              - [ ] `Force Restock`
              - [ ] `Change Clothes`
        - HostileEntity: