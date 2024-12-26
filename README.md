# Falcon Mod - Box PvP Server Automation
### For Minecraft 1.20.X

A Minecraft Fabric mod designed to enhance and automate inventory and shop operations for Box PvP servers. Falcon Mod streamlines repetitive tasks while adhering to server rules, focusing on legitimate automation of manual actions.

## Compatibility
- Minecraft: 1.20.X (1.20, 1.20.1, 1.20.2)
- Fabric API: 0.83.0+1.20
- Fabric Loader: 0.16.2+

## Key Features

- **Fast Shopping System**
  - Quick `/shop` command navigation
  - Bulk item purchasing automation
  - Smart gear/tools/items buying presets
  - Automatic inventory management during purchases

- **Inventory Management**
  - Fast item compression/decompression
  - Automatic shulker box packing
  - Smart inventory saving and recovery system
  - PV (Personal Vault) integration

- **Smart Memory System**
  - Save entire inventory states
  - Automatic shulker box organization
  - Quick inventory state recovery
  - PV location memory and management

- **Trade Optimization**
  - Complex trade calculations
  - Automatic resource requirement calculation
  - Efficient multi-step trade planning
  - Fast button clicking and navigation

## Important Note

This mod is designed to automate legitimate player actions only. It:
- Does NOT include any illegal features or hacks
- Only automates normal mouse movements and clicks
- Respects server rules and mechanics
- Focuses on efficiency, not exploitation

## How It Works

### Shopping Automation
- Fast navigation through shop menus
- Bulk purchase automation with inventory management
- Automatic item compression and decompression
- Smart distribution to inventory/shulker boxes

### Inventory Management
- One-click inventory saving to shulker boxes
- Automatic storage in designated PV slots
- Complete inventory state recovery
- Smart item organization and management

### Memory System
- Saves inventory layouts and contents
- Records item positions and configurations
- Enables quick recovery of saved states
- Maintains organization across sessions

## Interface Pages

### Main Menu
- Farm Material - Farming resources management
- Buy Gear - Equipment purchasing interface
- Buy Items - General item shopping
- Inventory - Inventory management tools
- Join Discord - Community access
- Back - Return to previous screen

### Buy Gear
- Armor: Zero
- Elytra: Zero
- Sword: Zero
- Pickaxe: Zero
- Axe: Zero
- Bow: Zero
- Shears: Zero

### Buy Items
- Arrow of Harming Zero
- Cobweb Zero
- Potion of Strength Zero
- Totem of Undying Zero
- Enchanted Golden Apple Zero
- Firework Zero
- Obsidian Zero

### Inventory 
- Falcon Statistics - View mod usage stats
- Save Inventory - Store current inventory state
- Recover Inventory - Restore saved inventory
- Send Inventory - Transfer items
- Complete Inventory - Full inventory operations
- Back - Return to main menu

## Installation

1. Install the [Fabric Loader](https://fabricmc.net/use/) if you haven't already
2. Download the latest release of this mod
3. Place the downloaded .jar file in your Minecraft mods folder
4. Launch Minecraft with the Fabric profile

## Development Setup

1. Clone this repository
2. Open a terminal in the project directory
3. Run the following commands based on your IDE:
   ```bash
   # For IntelliJ IDEA
   ./gradlew genIdeaRuns
   
   # For Eclipse
   ./gradlew genEclipseRuns
   ```

## Building

To build the project:

```bash
./gradlew build
```

The built jar file will be in `build/libs/`.

## Project Structure

```
src/main/java/net/omar/tutorial/
├── Data/
│   ├── Market.java          # Market data and operations
│   └── Indexes.java         # Index management system
├── GUI/
│   ├── FalconStatsScreen.java     # Statistics display
│   ├── FarmScreen.java            # Farming interface
│   ├── GearScreen.java            # Equipment management
│   ├── InventoryScreen.java       # Inventory interface
│   ├── ItemsScreen.java           # Item management
│   ├── MainScreen.java            # Main menu
│   ├── RestrictedScreen.java      # Access control interface
│   ├── SimpleButtonScreen.java    # Basic button interface
│   └── TakeItemsScreen.java       # Item collection interface
├── Handlers/
│   ├── ChatMessageHandler.java    # Chat message processing
│   ├── KeyBindingHandler.java     # Key binding management
│   └── KeyPressingHandler.java    # Key press processing
├── Managers/
│   ├── Clicking.java             # Mouse click handling
│   ├── Debugging.java            # Debug functionality
│   ├── Farming.java              # Farming mechanics
│   ├── Inventorying.java         # Inventory management
│   ├── Naming.java               # Name management
│   ├── Olding.java               # Legacy system handling
│   ├── Saving.java               # Save system
│   ├── Screening.java            # Screen management
│   ├── Shulkering.java           # Shulker box handling
│   ├── Slotting.java             # Slot management
│   ├── Statting.java             # Statistics handling
│   ├── Trading.java              # Trading system
│   └── Validating.java           # Validation system
├── Recovery/
│   └── Shulkery.java             # Shulker box recovery system
├── Vaults/
│   ├── MyInventory.java          # Personal inventory management
│   ├── MyPV.java                 # Personal vault handling
│   ├── MyShulker.java            # Shulker box management
│   └── VaultsStateManager.java    # Vault state coordination
└── classes/
    ├── Trader.java               # Trading functionality
    └── Shopper.java              # Shopping functionality
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Credits

Built with [Fabric](https://fabricmc.net/) for Minecraft 1.20.X 