------------------------------------------------------
Create 6.0.6
------------------------------------------------------

#### Gameplay Changes

- Wardens can now hear trains and train horns
- Contraption controls with a door as their target now affect all carriages of a train #8289

#### Optimizations

- Fix basins taking up a lot of tick time in certain setups
- Implement flw visuals for signals and train observers
- Optimize vault comparator updates
- Optimize vault inventory access

#### Bug Fixes

- Fix display links not properly being rotated with contraptions #8173
- Fix belt stack sizes getting reduced to 1 in ponders #8143
- Fix pipes not checking pipesPlaceFluidSourceBlocks when trying to waterlog blocks #8174
- Fix chain conveyor animation not respecting player's main hand #7419 #8032
- Fix crushing wheel efficiency being lower on powers of 2 #8218
- Fix crushing wheels voiding items due to their internal inventory being too small #3850
- Fix fluids incorrectly being marked as outputs in JEI #7848
- Fix requesting recipes with no item outputs causing a crash #8276
- Fix horizontal crushing wheels processing items twice in specific directions #7809
- Fix PackageEntity not respecting item invulnerabilities #8162
- Fix encased chutes not conducting redstone #8064
- Fix backtanks still being enchantable with mending and unbreaking
- Fix sequenced assembly input amounts not respecting number of loops #8306
- Fix crash when certain create blocks are part of worldgen #8268
- Fix incorrect inputs for buzzies bees compat recipes #8329
- Fix blaze burners not overriding animateTick #8140
- Fix being unable to change link mode while holding extendo grip in offhand #7877
- Fix item duplication on belts
- Fix incorrect re-packager behaviour when ordering multiple recipes from a stock ticker with non-uniform ingredients
- Fix train graph sync issues #8425
- Fix belts being consumed when not having enough shafts to split the belt #7334
- Fix restocker promises not expiring #7759 #8429
- Fix fluid pumps not working in ponders #8194 #8316
- Fix crash when holding shift when setting addresses #8442
- Fix backtank air depleting when in spectator mode #5927
- Fix backtank air overlay appearing in all fluids, including those that the player cannot drown in #8408
- Fix sliding doors not playing sounds when being opened #5488
- Fix consistency issues in analog lever ponder #8449
- Fix inability to shift-click items to fill the fourth stack in the last slot of a toolbox #8447
- Fix bound block of cardboard being unable to be silk touched #7513
- Fix Cargo idle condition not working as intended #7609
- Fix computer display source translation key #8373 #8374
- Switched concrete powder splashing recipes to be created at runtime to improve mod compat
- Fix all stews not appearing upright on belts
- Fix toolboxes causing crashes #8343
- Fix incorrect lang keys #8373 #8374 #8459
- Fix mob hat offsets for multiple mobs
- Fix crash when interacting with a trains schedule with cc: tweaked #8504 #8507
- Improved harvester interaction with blocks that have multiple properties #8187
- Fix ItemUseWorld not extending ServerLevel #7956
- Fix SignDisplayTarget not trimming text to the sign's max length #8065
- Fix self-driving cart advancement being really obtuse to obtain #4058 #8520
- Fix nbt data not being passed to ItemRequirement's
- Fix schematics requesting 2 filters for belt funnels #6984
- Fix train honks subtitle not being repeated #3566
- Fix 1x1 fluid tanks not connecting to whistles properly when placed with a schematicannon #7137
- Fix deployer being unable to pickup items dropped by blocks with Block.popResource #5226
- Fix belt funnels breaking upon contraption disassembly #6985 #8543
- Fix Redstone links not updating blocks neighbouring hard-powered blocks when broken #8618
- Fix /create debuginfo translation the graphics mode
- Fix cardboard not having the plates tag #8589
- Fix crash with invalid package filter addresses when using the glob syntax #8583
- Fix funnels on contraptions not picking up package entities #8151
- Fix new cardboard packages from addons being added to Create's creative tab
- Fix packages being voided when placed on chains #8649 #8652
- Fix being unable to pickup packages on chains #8649 #8652
- Fix unbreakable glue and unbreakable items not working properly in deployers (1.21.1) #8193
- Fix processing outputs not applying data components (1.21.1) #7945 #8105
- Fix certain equipment items not being enchantable properly (1.21.1) #7997
- Fix fluid networks causing crashes when removing blocks (1.21.1) #7604
- Fix mechanical arms causing crashes (1.21.1) #8060 #8106 #8200
- Fix redstone torches popping off contraptions (1.21.1) #7815
- Fixed deployer crashing when using nametag with space on nixie tubes (1.21.1) #8299
- Fix chromatic compound having a blank/empty texture (1.21.1) #7664
- Fix chain conveyor riding animation not showing up on chains longer then 20 blocks (1.21.1) #7551 #8233
- Add #c:teleporting_not_supported to contraption entities (1.21.1) #8407
- Fix lectern controller not saving the channels of the controller (1.21.1) #7826
- Fix toolbox not keeping filters when no items are inside the slot (1.21.1) #8438
- Fix potato cannon not playing the proper shooting animation (1.21.1) #7966 #8368
- Fix recipe IDs not showing in JEI (1.21.1) #8048 #8351
- Fix crash with linked controller using enchanted items as their channels (1.21.1) #8089
- Fix stock links not working properly with large inventories from mods like sophisticated storage (1.21.1) #8346 #8056
- Adjust hat offsets for armadillo and breeze (1.21.1) #7786
- Fix some ponder scenes (1.21.1) #8318
- Fix copycats not properly saving light level when leaving and opening a world (1.21.1) #4889
- Fix crash when using sandpaper (1.21.1) #8536 #8503
- Fix crash with spouts (1.21.1) #8531

#### API Changes

- Allow other mods and addons to use ValueSettingsFormatter without needing to use translations prefixed with "create."
  #8322
- Refactored datagen so that addons have a clear API to follow. #7861
- Reworked ProcessingRecipe (Addon developers will need to adjust to these changes for their addon to work with 6.0.6) (1.21.1) #7945

------------------------------------------------------
Create 6.0.5
------------------------------------------------------

#### Bug Fixes

- Fixed duplication glitch with chain conveyors
- Fixed an exploit that could be used to spawn in arbitrary packages

------------------------------------------------------
Create 6.0.4
------------------------------------------------------

#### Bug Fixes

- Fixed crash when using mechanical arms with composters #7965
- Fixed chocolate and honey having the wrong map color #5409
- Fixed goggles not swapping with helmet when equipped #7977
- Fixed fluid pipe visuals rendering with incorrect uv scale
- Fixed Re-packager creating invalid item stacks when factory gauges were assigned larger stacks #7963
- Fixed nixie tubes not updating their text in ponder scenes #7978
- Clicking conveyors with chains or the wrench now takes priority over picking up packagers from them
- Fix Processing recipes ignoring item components (1.21) #7962

------------------------------------------------------
Create 6.0.3
------------------------------------------------------

#### Bug Fixes

- Fixed certain block entities not rendering on contraptions #7790 #7782 #7774
- Fixed sign text and color getting removed by display links #7250
- Changed sandpaper item rendering to use Gui transform
- Fixed crash when using or viewing sandpaper polishing recipes (1.21)
- Fixed crash with recent versions of ftb library
- Fixed belts interacting with horizontal crushing wheels inconsistently #7445
- Allow Re-Packager to split packages with multiple crafting recipes into single packages
- Fixed contraptions not able to break ice blocks #4415
- Increased rope pulley view distance #4836
- Fixed create datagen running in addon workspaces (#7862)
- Fixed Fluid recipes missing outputs, and crash when using create fluid buckets on basin (#7884)
- Fixed crash when placing empty linked controllers in lecterns #7876
- Fixed issues with elevator contact display link behavior
- Fixed double chest lighting on contraptions
- Added flywheel optimisations for fluids in windowed pipes
- Fixed bogey lighting not updating correctly while switching between styles
- Fixed cardboard sword not being enchantable #7736
- Fixed crash with redstone requester #7788
- Fixed being able to enchant cardboard swords with other enchantments when using anvils
- Fixed metal ladders being able to hang in wrong directions #7395
- Fixed player chain conveyor animation playing when game is paused #7390
- Fixed elevator floor description rendering being offset from the controls block

------------------------------------------------------
Create 6.0.2
------------------------------------------------------

#### Bug Fixes

- Fix warning getting logged when CC: Tweaked isn't installed
- Fix crash when using LecternDisplayTarget #7579 #7600
- Fix crash with fluids on contraptions
- Fixed Shopping lists disappearing when clicking a different table cloth #7548
- Fix mixin conflict with immersive portals
- Fix factory gauge display sources causing crashes #7645
- Fix Schematics sometimes rendering block entity elements when it shouldn't #7639
- Fix Chiseled Bookshelves having wrong the blockstates when placed with a schematic cannon #7642
- Fix horizontal crushing wheels not working properly #7445
- Fix rotating shader not correctly handling color
- Fix StockKeeperCategoryScreen filter buttons being clickable outside the window bounds #7668
- Fix diving armor being trimmable (1.21.1)
- Fix diving helmets not having aqua affinity #7433 (1.21.1)
- Fix crash when using deployers wielding weapons #7704 (1.21.1)
- Fix threshold switch UI causing a crash if opened too quickly after breaking the storage block it was looking at #7676
- Fix rotated steam engines still working #7616
- Fix button of contraption controls not rendering on contraptions #7701
- Fix error/crash with CurrentFloorDisplaySource #7700
- Fix the ability to redeem shopping lists at other shop networks #7657
- Add bowls, mushroom stew and suspicious stew to the upright on belt tag
- Fix shopping lists not working on servers
- Fix rotation keybind not supporting mouse buttons
- Fixed deployers not being able to harvest honeycomb with modded shears #4570
- Safety check for schedule pointer exceeding the total count #7492
- Fixed packager unpacking leading to item multiplication in special modded inventories #7426
- Fixed redstone links not updating neighbours when toggling from receiver to transmitter #7715
- Fix KineticStressDisplaySource not working properly #7659
- Fix sequenced assembly not correctly handling errors (1.21.1)
- The JEI search bar is now synchronised to the stock keeper search bar (configurable)
- Fixed JEI plugin modifying ingredient fluid amounts for visual purposes
- Fixed belts moving a sneaking player even when not wearing the full cardboard set #7691
- Packagers can now read multiple lines on signs for package addressing
- Factory Gauge request interval is now configurable
- Fixed train map integration crashing when there are derailed trains or trains in another dimension (1.21.1)
- Fixed cardboard armor rendering not respecting custom player scaling

#### API Changes

- Implement custom unpacking API
- Implement InventoryIdentifier API
- Lock Create's registrate instance behind a caller check, prevent other mods and addons from using it
- Implement CreateRegistrateRegistrationCallback

------------------------------------------------------
Create 6.0.1
------------------------------------------------------

#### Bug Fixes

- Fixed Shopping lists not updating when adding purchases (1.21) #7449 #7393
- Fixed Frogport ponder scene not animating correctly (1.21)
- Fixed broken address filter in second stock ticker ponder scene (1.21)
- Fixed crash when modifying pipe structures while they are transporting fluids (1.21) #7515
- Fixed shift-inserting into toolboxes causing items to be voided (1.21) #7519
- Fixed inconsistent component serialisation in backtank BE (1.21)
- Fixed table cloths not showing tooltips and enchantment effect when configured (1.21)
- Fixed shopping list tooltip expanding as its viewed (1.21) #7503
- Fixed crash when selling enchanted items on a table cloth (1.21) #7516 #7536
- Added a tooltip for the stock keeper address input
- Fixed crash when re-packaging a duplicated package fragment #7456
- Fixed crash when ctrl-click copying a gauge #7431
- Safety check for unexpected string modifications in address edit boxes #7409
- Fixed crash with fluid propagator
- Fixed a crash when using factory gauges
- Fixed debug info command not translating the graphics mode text
- Fixed cardboard sword not being able to damage arthropod mobs other than the spider
- Fixed a crash that occurred when placing a stock link on a re-packager
- Fixed an issue where wearing diving boots and sprinting would force you into the swim position and then out of it
  right away
- Fixed item group attribute filters crashing
- Fixed mixin conflict with immersive portals
- Fixed processing output not supporting itemstack components
- Fixed crash when shift-clicking items in the package filter UI #7497
- Fixed crash caused by the create menu button when loaded too early #7521

------------------------------------------------------
Create 6.0.0
------------------------------------------------------

_Now using Flywheel 1.0_

#### Additions

- Chain conveyor
- Item hatch
- Packager and Re-packager
- Cardboard packages
- Package frogport
- Package postbox
- Stock link
- Stock ticker
- Redstone requester
- Factory gauge
- Table cloths and covers
- Pulse timer
- Desk bell
- Pulp and the cardboard ingredient
- Cardboard armor set
- Cardboard sword
- Package filter
- Cardboard block
- Cherry and bamboo windows
- Industrial iron window
- Weathered iron block and windows

#### Art Changes

- Palette and model updates to all copper-based components
- Rope and hose pulley motion now uses a scrolling texture
- Increased vertical size of train and contraption controls to a full block
- Updates to display and redstone links
- Updates to metal sheet items
- Copper roof blocks now use connected textures
- Added missing shaft detail to the backtank armor
- Updates to various UI screens and components
- Bars and window item models are now consistent with vanilla

#### Gameplay Changes

- Redstone links are now andesite tier
- All links now use a new ingredient item, the transmitter
- New advancement chain for high logistics components
- New ponder scenes and category for high logistics components
- Tracks and Trains now have special integration with FTBChunks and Journeymap
- Depots can now be used as storage blocks on contraptions
- Brass tunnels now try to distribute an item more quickly when it first arrives
- Brass tunnels now always prefer filtered sides over non-filtered sides
- Added train schedule instructions for delivering or retrieving packages
- Basins no longer limit to 16 items per slot
- Mechanical crafters waste less time on empty animation frames
- In common cobblegen scenarios, stationary drills now skip breaking blocks and just insert the result items into open
  inventories directly below
- Held clipboards can now copy entries from other in-world clipboards
- Filters, Clipboards and Schedules can now be copyied in the crafting table
- Metal ladders no longer require a wall if another ladder block is above them
- Bells assembled to elevator contraptions now activate when arriving at a floor
- Sliding doors placed in front of contraption-mounted sliding doors now open and close automatically
- Fully outlined text on filter slots for better readability
- Added recipes where cardboard substitutes leather
- Play at most four steam engine sounds at once per side of a boiler
- Increased default max rope length to 384
- Implemented a system for generating certain recipes at runtime to improve mod compat
- Boiler gauge now disappears when blocks are clipping into it
- Added a keybind that opens a radial menu for rotating blocks with the wrench
- Wood cutting recipes in mechanical saws
- Added pressing recipes for coarse dirt and rooted dirt which both produce dirt paths (#7186)
- Updated JEI integration and added potion fluids to the JEI sidebar (#6934)
- Chain Drives can now be crafted from zinc nuggets
- Redstone lamps can now be picked up with the wrench
- New compatibility recipes for Immersive Engineering
- Added missing deploying recipes for copper oxidisation
- Framed and tiled glass panes can now be obtained via stonecutting
- Schematicannon on 'replace blocks with empty' now send block updates at the edges after printing
- The player hitbox used in contraption collision is now slightly shorter

#### Bug Fixes

- Deployers can no longer take a seat
- Fixed contraptions keeping pressure plates and tripwires activated (#7255)
- Steam engine placement assist now shows a normal shaft
- Fixed schedule screen not showing tooltips in the entry editor
- Fixed tracks creating signal block intersections despite being in different dimensions
- Fixed non-effect fans resetting processed belt items (#7298)
- Fixed mechanical saw considering scaffolding as leaves
- Fixed entity name display source not working for players on signs
- Fixed certain blocks messing up the order scheduled ticks (#7141)
- Fixed unbreakable superglue not being usable (#6253)
- Fixed update suppression (#7176)
- Fixed comparator output of depots ignoring the items' max stack size (#7179)
- Fixed deployers retaining the damage attribute of their last held weapon (#4870)
- Fixed an exploit allowing people to create clipboards that execute commands (#7218)
- Fixed redstone links not updating their redstone output when they've been taken out of receiver mode (#7226)
- Fixed rare crash related to sliding doors (#6184)
- Verify that schematics are gzip-encoded before trying to read from them (#6087)
- Fixed ConditionContext nbt in trains containing a large number of empty tags
- Fixed deployers not placing fish from fish buckets (#3705)
- Fixed gasses not being visible in basins and item drains (#7236)
- Set vault capacity config limit to 2048 slots
- Fixed InventorySorter able to take items from ghost/filter inventories
- Fixed typo in better end compat recipe
- FTB buttons no longer show in create screens
- Fixed mechanical arm interactions with jukeboxes (#5902)
- Fixed toolboxes not giving a comparator output signal (#6973)
- Fixed copper slabs and stairs being missing from the respective tags (#3080)
- Fixed Fix waterlogged bracketed kinetics dropping the bracket (Fabricators-of-Create#1552)
- Switched away from using streams in ContraptionCollider fixing a rare crash (#5043)
- Fixed pumps not placing fluids into flowing fluids of the same type (#5884)
- Fixed schematicannons not consuming the right number of group items (#6983)
- Fixed backtanks getting incompatible enchants via smithing tables (#6687)
- Fixed Lectern Controllers storing ItemStacks from nbt (#7143)
- Optimized spout recipe generation by avoiding filling non-empty items (#7274)
- Fixed crash when dying nixie tubes with dye depots dyes (#6694)
- Fix enchantments getting trimmed from non-filter items (#7216)
- Fixed sandpaper polishing recipes not working in sequenced assembly recipes (#7259)
- Fixed mechanical drills and saws using the friendly creatures sound source instead of the blocks sound source (#7038)
- Fixed backtank crashing on ctrl+pick block (#7284)
- Improved memory usage of drain category in JEI (#7277)
- Fixed getSize() throwing an error on newly loaded display link peripherals (#7059)
- Fixed inability to mill cactus when Quark is installed (#7215)
- Fixed rare spout crash and offset rendering (#7025)
- Fixed deploying food resulting in missing particles and not returning the correct items (#7288)
- Fixed trains not properly pathfinding to stations with an opposing signal just behind the destination
- Fixed stations voiding schedules when disassembling the train
- Fixed lighting on signal block indicators
- Fixed vaults and tanks rotated in place not updating their multiblock correctly
- Hose pulley now deletes lilypads and other surface foliage
- Fixed crushing wheels not applying looting to killed entities
- Updated contraption chunkban protections, corrected limits and made them much harder to hit

#### API Changes

- Versioning change: `major.minor.patch`, starting with `6.0.0`
- Ponder is now a separate library mod. It comes shipped with the create jar.
- Added `#create:chain_rideable` to mark items as valid for riding a chain with
- Added `#create:invalid_for_track_paving` for items
- Added `#create:sugar_cane_variants` to allow the mechanical saw to work with custom sugarcane variants (#7263)
- Added `#create:not_harvestable` to disallow blocks that the mechanical harvester would otherwise try to harvest
- New API for custom storage block behaviour on contraptions.
  For simple cases, create provides the `#create:simple_mounted_storage` and `#create:chest_mounted_storage` block tags.
- Added `#create:non_breakable` to mark blocks that cannot be broken by block-breaking kinetics
- Removed LangMerger and related classes
- Implemented an api to allow mods to register schematic requirements, partial safe nbt and contraption transforms
  without implementing interfaces (#4702)
- Add a method that developers can override to change the icon in goggle tooltips
- Refactored Item Attributes types, Fan processing types and Arm interaction points, all 3 now use registries
- Synced AllPortalTracks with Create Fabric
- Implemented DyeHelper api (#7265)
- Implemented api to add custom block train conductors (#7030)
- Convert Potato Cannon projectile types into a dynamic registry
    - Everything can be done with datapacks now, and there is no need to write a mod unless you need to add new
      Render Modes, Entity Hit Actions or Block Hit Actions
- Reworked the AttachedRegistry class into SimpleRegistry and added Provider functionality
- Exposed all custom registries as API
- Exposed a handful of previously internal classes to the API, and gave them some cleanup
    - BlockSpoutingBehaviour
    - MovementBehaviour
    - MovingInteractionBehaviour
    - DisplaySource
    - DisplayTarget
    - ContraptionMovementSetting
    - BoilerHeater
    - PortalTrackProvider
    - BlockMovementChecks
    - ContraptionType
    - MountedDispenseBehavior
    - BlockStressValues
    - OpenPipeEffectHandler
