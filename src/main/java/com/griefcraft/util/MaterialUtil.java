package com.griefcraft.util;

import org.bukkit.Material;

public class MaterialUtil {

    /**
     * Best attempt effort to get the 1.13 Material associated with a 1.12 ID.
     */
    @SuppressWarnings("deprecation")
    public static Material getMaterialById(int id) {
        switch (id) {
            case 0:
                return Material.AIR;
            case 1:
                return Material.STONE;
            case 2:
                return Material.GRASS_BLOCK;
            case 3:
                return Material.DIRT;
            case 4:
                return Material.COBBLESTONE;
            case 5:
                return Material.OAK_PLANKS;
            case 6:
                return Material.OAK_SAPLING;
            case 7:
                return Material.BEDROCK;
            case 8:
                return Material.WATER;
            case 9:
                return Material.WATER;
            case 10:
                return Material.LAVA;
            case 11:
                return Material.LAVA;
            case 12:
                return Material.SAND;
            case 13:
                return Material.GRAVEL;
            case 14:
                return Material.GOLD_ORE;
            case 15:
                return Material.IRON_ORE;
            case 16:
                return Material.COAL_ORE;
            case 17:
                return Material.OAK_LOG;
            case 18:
                return Material.OAK_LEAVES;
            case 19:
                return Material.SPONGE;
            case 20:
                return Material.GLASS;
            case 21:
                return Material.LAPIS_ORE;
            case 22:
                return Material.LAPIS_BLOCK;
            case 23:
                return Material.DISPENSER;
            case 24:
                return Material.SANDSTONE;
            case 25:
                return Material.NOTE_BLOCK;
            case 26:
                return Material.RED_BED;
            case 27:
                return Material.POWERED_RAIL;
            case 28:
                return Material.DETECTOR_RAIL;
            case 29:
                return Material.STICKY_PISTON;
            case 30:
                return Material.COBWEB;
            case 31:
                return Material.DEAD_BUSH;
            case 32:
                return Material.DEAD_BUSH;
            case 33:
                return Material.PISTON;
            case 34:
                return Material.PISTON_HEAD;
            case 35:
                return Material.WHITE_WOOL;
            case 36:
                return Material.MOVING_PISTON;
            case 37:
                return Material.DANDELION;
            case 38:
                return Material.POPPY;
            case 39:
                return Material.BROWN_MUSHROOM;
            case 40:
                return Material.RED_MUSHROOM;
            case 41:
                return Material.GOLD_BLOCK;
            case 42:
                return Material.IRON_BLOCK;
            case 43:
                return Material.STONE_SLAB;
            case 44:
                return Material.STONE_SLAB;
            case 45:
                return Material.BRICKS;
            case 46:
                return Material.TNT;
            case 47:
                return Material.BOOKSHELF;
            case 48:
                return Material.MOSSY_COBBLESTONE;
            case 49:
                return Material.OBSIDIAN;
            case 50:
                return Material.TORCH;
            case 51:
                return Material.FIRE;
            case 52:
                return Material.SPAWNER;
            case 53:
                return Material.OAK_STAIRS;
            case 54:
                return Material.CHEST;
            case 55:
                return Material.REDSTONE_WIRE;
            case 56:
                return Material.DIAMOND_ORE;
            case 57:
                return Material.DIAMOND_BLOCK;
            case 58:
                return Material.CRAFTING_TABLE;
            case 59:
                return Material.WHEAT;
            case 60:
                return Material.FARMLAND;
            case 61:
                return Material.FURNACE;
            case 62:
                return Material.FURNACE;
            case 63:
                return Material.LEGACY_SIGN;
            case 64:
                return Material.OAK_DOOR;
            case 65:
                return Material.LADDER;
            case 66:
                return Material.RAIL;
            case 67:
                return Material.COBBLESTONE_STAIRS;
            case 68:
                return Material.LEGACY_WALL_SIGN;
            case 69:
                return Material.LEVER;
            case 70:
                return Material.STONE_PRESSURE_PLATE;
            case 71:
                return Material.IRON_DOOR;
            case 72:
                return Material.OAK_PRESSURE_PLATE;
            case 73:
                return Material.REDSTONE_ORE;
            case 74:
                return Material.REDSTONE_ORE;
            case 75:
                return Material.REDSTONE_TORCH;
            case 76:
                return Material.REDSTONE_TORCH;
            case 77:
                return Material.STONE_BUTTON;
            case 78:
                return Material.SNOW;
            case 79:
                return Material.ICE;
            case 80:
                return Material.SNOW_BLOCK;
            case 81:
                return Material.CACTUS;
            case 82:
                return Material.CLAY;
            case 83:
                return Material.SUGAR_CANE;
            case 84:
                return Material.JUKEBOX;
            case 85:
                return Material.OAK_FENCE;
            case 86:
                return Material.CARVED_PUMPKIN;
            case 87:
                return Material.NETHERRACK;
            case 88:
                return Material.SOUL_SAND;
            case 89:
                return Material.GLOWSTONE;
            case 90:
                return Material.NETHER_PORTAL;
            case 91:
                return Material.JACK_O_LANTERN;
            case 92:
                return Material.CAKE;
            case 93:
                return Material.REPEATER;
            case 94:
                return Material.REPEATER;
            case 95:
                return Material.WHITE_STAINED_GLASS;
            case 96:
                return Material.OAK_TRAPDOOR;
            case 97:
                return Material.INFESTED_STONE;
            case 98:
                return Material.STONE_BRICKS;
            case 99:
                return Material.BROWN_MUSHROOM_BLOCK;
            case 100:
                return Material.RED_MUSHROOM_BLOCK;
            case 101:
                return Material.IRON_BARS;
            case 102:
                return Material.GLASS_PANE;
            case 103:
                return Material.MELON;
            case 104:
                return Material.PUMPKIN_STEM;
            case 105:
                return Material.MELON_STEM;
            case 106:
                return Material.VINE;
            case 107:
                return Material.OAK_FENCE_GATE;
            case 108:
                return Material.BRICK_STAIRS;
            case 109:
                return Material.STONE_BRICK_STAIRS;
            case 110:
                return Material.MYCELIUM;
            case 111:
                return Material.LILY_PAD;
            case 112:
                return Material.NETHER_BRICKS;
            case 113:
                return Material.NETHER_BRICK_FENCE;
            case 114:
                return Material.NETHER_BRICK_STAIRS;
            case 115:
                return Material.NETHER_WART;
            case 116:
                return Material.ENCHANTING_TABLE;
            case 117:
                return Material.BREWING_STAND;
            case 118:
                return Material.CAULDRON;
            case 119:
                return Material.END_PORTAL;
            case 120:
                return Material.END_PORTAL_FRAME;
            case 121:
                return Material.END_STONE;
            case 122:
                return Material.DRAGON_EGG;
            case 123:
                return Material.REDSTONE_LAMP;
            case 124:
                return Material.REDSTONE_LAMP;
            case 125:
                return Material.OAK_SLAB;
            case 126:
                return Material.OAK_SLAB;
            case 127:
                return Material.COCOA;
            case 128:
                return Material.SANDSTONE_STAIRS;
            case 129:
                return Material.EMERALD_ORE;
            case 130:
                return Material.ENDER_CHEST;
            case 131:
                return Material.TRIPWIRE_HOOK;
            case 132:
                return Material.TRIPWIRE;
            case 133:
                return Material.EMERALD_BLOCK;
            case 134:
                return Material.SPRUCE_STAIRS;
            case 135:
                return Material.BIRCH_STAIRS;
            case 136:
                return Material.JUNGLE_STAIRS;
            case 137:
                return Material.COMMAND_BLOCK;
            case 138:
                return Material.BEACON;
            case 139:
                return Material.COBBLESTONE_WALL;
            case 140:
                return Material.FLOWER_POT;
            case 141:
                return Material.CARROTS;
            case 142:
                return Material.POTATOES;
            case 143:
                return Material.OAK_BUTTON;
            case 144:
                return Material.SKELETON_SKULL;
            case 145:
                return Material.ANVIL;
            case 146:
                return Material.TRAPPED_CHEST;
            case 147:
                return Material.LIGHT_WEIGHTED_PRESSURE_PLATE;
            case 148:
                return Material.HEAVY_WEIGHTED_PRESSURE_PLATE;
            case 149:
                return Material.COMPARATOR;
            case 150:
                return Material.COMPARATOR;
            case 151:
                return Material.DAYLIGHT_DETECTOR;
            case 152:
                return Material.REDSTONE_BLOCK;
            case 153:
                return Material.NETHER_QUARTZ_ORE;
            case 154:
                return Material.HOPPER;
            case 155:
                return Material.QUARTZ_BLOCK;
            case 156:
                return Material.QUARTZ_STAIRS;
            case 157:
                return Material.ACTIVATOR_RAIL;
            case 158:
                return Material.DROPPER;
            case 159:
                return Material.WHITE_TERRACOTTA;
            case 160:
                return Material.WHITE_STAINED_GLASS_PANE;
            case 161:
                return Material.ACACIA_LEAVES;
            case 162:
                return Material.ACACIA_LOG;
            case 163:
                return Material.ACACIA_STAIRS;
            case 164:
                return Material.DARK_OAK_STAIRS;
            case 165:
                return Material.SLIME_BLOCK;
            case 166:
                return Material.BARRIER;
            case 167:
                return Material.IRON_TRAPDOOR;
            case 168:
                return Material.PRISMARINE;
            case 169:
                return Material.SEA_LANTERN;
            case 170:
                return Material.HAY_BLOCK;
            case 171:
                return Material.WHITE_CARPET;
            case 172:
                return Material.TERRACOTTA;
            case 173:
                return Material.COAL_BLOCK;
            case 174:
                return Material.PACKED_ICE;
            case 175:
                return Material.SUNFLOWER;
            case 176:
                return Material.WHITE_BANNER;
            case 177:
                return Material.WHITE_WALL_BANNER;
            case 178:
                return Material.DAYLIGHT_DETECTOR;
            case 179:
                return Material.RED_SANDSTONE;
            case 180:
                return Material.RED_SANDSTONE_STAIRS;
            case 181:
                return Material.RED_SANDSTONE_SLAB;
            case 182:
                return Material.RED_SANDSTONE_SLAB;
            case 183:
                return Material.SPRUCE_FENCE_GATE;
            case 184:
                return Material.BIRCH_FENCE_GATE;
            case 185:
                return Material.JUNGLE_FENCE_GATE;
            case 186:
                return Material.DARK_OAK_FENCE_GATE;
            case 187:
                return Material.ACACIA_FENCE_GATE;
            case 188:
                return Material.SPRUCE_FENCE;
            case 189:
                return Material.BIRCH_FENCE;
            case 190:
                return Material.JUNGLE_FENCE;
            case 191:
                return Material.DARK_OAK_FENCE;
            case 192:
                return Material.ACACIA_FENCE;
            case 193:
                return Material.SPRUCE_DOOR;
            case 194:
                return Material.BIRCH_DOOR;
            case 195:
                return Material.JUNGLE_DOOR;
            case 196:
                return Material.ACACIA_DOOR;
            case 197:
                return Material.DARK_OAK_DOOR;
            case 198:
                return Material.END_ROD;
            case 199:
                return Material.CHORUS_PLANT;
            case 200:
                return Material.CHORUS_FLOWER;
            case 201:
                return Material.PURPUR_BLOCK;
            case 202:
                return Material.PURPUR_PILLAR;
            case 203:
                return Material.PURPUR_STAIRS;
            case 204:
                return Material.PURPUR_SLAB;
            case 205:
                return Material.PURPUR_SLAB;
            case 206:
                return Material.END_STONE_BRICKS;
            case 207:
                return Material.BEETROOTS;
            case 208:
                return Material.getMaterial("GRASS_PATH") == null ? Material.DIRT_PATH : Material.getMaterial("GRASS_PATH");
            case 209:
                return Material.END_GATEWAY;
            case 210:
                return Material.REPEATING_COMMAND_BLOCK;
            case 211:
                return Material.CHAIN_COMMAND_BLOCK;
            case 212:
                return Material.FROSTED_ICE;
            case 213:
                return Material.MAGMA_BLOCK;
            case 214:
                return Material.NETHER_WART_BLOCK;
            case 215:
                return Material.RED_NETHER_BRICKS;
            case 216:
                return Material.BONE_BLOCK;
            case 217:
                return Material.STRUCTURE_VOID;
            case 218:
                return Material.OBSERVER;
            case 219:
                return Material.WHITE_SHULKER_BOX;
            case 220:
                return Material.ORANGE_SHULKER_BOX;
            case 221:
                return Material.MAGENTA_SHULKER_BOX;
            case 222:
                return Material.LIGHT_BLUE_SHULKER_BOX;
            case 223:
                return Material.YELLOW_SHULKER_BOX;
            case 224:
                return Material.LIME_SHULKER_BOX;
            case 225:
                return Material.PINK_SHULKER_BOX;
            case 226:
                return Material.GRAY_SHULKER_BOX;
            case 227:
                return Material.LIGHT_GRAY_SHULKER_BOX;
            case 228:
                return Material.CYAN_SHULKER_BOX;
            case 229:
                return Material.PURPLE_SHULKER_BOX;
            case 230:
                return Material.BLUE_SHULKER_BOX;
            case 231:
                return Material.BROWN_SHULKER_BOX;
            case 232:
                return Material.GREEN_SHULKER_BOX;
            case 233:
                return Material.RED_SHULKER_BOX;
            case 234:
                return Material.BLACK_SHULKER_BOX;
            case 235:
                return Material.WHITE_GLAZED_TERRACOTTA;
            case 236:
                return Material.ORANGE_GLAZED_TERRACOTTA;
            case 237:
                return Material.MAGENTA_GLAZED_TERRACOTTA;
            case 238:
                return Material.LIGHT_BLUE_GLAZED_TERRACOTTA;
            case 239:
                return Material.YELLOW_GLAZED_TERRACOTTA;
            case 240:
                return Material.LIME_GLAZED_TERRACOTTA;
            case 241:
                return Material.PINK_GLAZED_TERRACOTTA;
            case 242:
                return Material.GRAY_GLAZED_TERRACOTTA;
            case 243:
                return Material.LIGHT_GRAY_GLAZED_TERRACOTTA;
            case 244:
                return Material.CYAN_GLAZED_TERRACOTTA;
            case 245:
                return Material.PURPLE_GLAZED_TERRACOTTA;
            case 246:
                return Material.BLUE_GLAZED_TERRACOTTA;
            case 247:
                return Material.BROWN_GLAZED_TERRACOTTA;
            case 248:
                return Material.GREEN_GLAZED_TERRACOTTA;
            case 249:
                return Material.RED_GLAZED_TERRACOTTA;
            case 250:
                return Material.BLACK_GLAZED_TERRACOTTA;
            case 251:
                return Material.WHITE_CONCRETE;
            case 252:
                return Material.WHITE_CONCRETE_POWDER;
            case 255:
                return Material.STRUCTURE_BLOCK;
            case 256:
                return Material.IRON_SHOVEL;
            case 257:
                return Material.IRON_PICKAXE;
            case 258:
                return Material.IRON_AXE;
            case 259:
                return Material.FLINT_AND_STEEL;
            case 260:
                return Material.APPLE;
            case 261:
                return Material.BOW;
            case 262:
                return Material.ARROW;
            case 263:
                return Material.COAL;
            case 264:
                return Material.DIAMOND;
            case 265:
                return Material.IRON_INGOT;
            case 266:
                return Material.GOLD_INGOT;
            case 267:
                return Material.IRON_SWORD;
            case 268:
                return Material.WOODEN_SWORD;
            case 269:
                return Material.WOODEN_SHOVEL;
            case 270:
                return Material.WOODEN_PICKAXE;
            case 271:
                return Material.WOODEN_AXE;
            case 272:
                return Material.STONE_SWORD;
            case 273:
                return Material.STONE_SHOVEL;
            case 274:
                return Material.STONE_PICKAXE;
            case 275:
                return Material.STONE_AXE;
            case 276:
                return Material.DIAMOND_SWORD;
            case 277:
                return Material.DIAMOND_SHOVEL;
            case 278:
                return Material.DIAMOND_PICKAXE;
            case 279:
                return Material.DIAMOND_AXE;
            case 280:
                return Material.STICK;
            case 281:
                return Material.BOWL;
            case 282:
                return Material.MUSHROOM_STEW;
            case 283:
                return Material.GOLDEN_SWORD;
            case 284:
                return Material.GOLDEN_SHOVEL;
            case 285:
                return Material.GOLDEN_PICKAXE;
            case 286:
                return Material.GOLDEN_AXE;
            case 287:
                return Material.STRING;
            case 288:
                return Material.FEATHER;
            case 289:
                return Material.GUNPOWDER;
            case 290:
                return Material.WOODEN_HOE;
            case 291:
                return Material.STONE_HOE;
            case 292:
                return Material.IRON_HOE;
            case 293:
                return Material.DIAMOND_HOE;
            case 294:
                return Material.GOLDEN_HOE;
            case 295:
                return Material.WHEAT_SEEDS;
            case 296:
                return Material.WHEAT;
            case 297:
                return Material.BREAD;
            case 298:
                return Material.LEATHER_HELMET;
            case 299:
                return Material.LEATHER_CHESTPLATE;
            case 300:
                return Material.LEATHER_LEGGINGS;
            case 301:
                return Material.LEATHER_BOOTS;
            case 302:
                return Material.CHAINMAIL_HELMET;
            case 303:
                return Material.CHAINMAIL_CHESTPLATE;
            case 304:
                return Material.CHAINMAIL_LEGGINGS;
            case 305:
                return Material.CHAINMAIL_BOOTS;
            case 306:
                return Material.IRON_HELMET;
            case 307:
                return Material.IRON_CHESTPLATE;
            case 308:
                return Material.IRON_LEGGINGS;
            case 309:
                return Material.IRON_BOOTS;
            case 310:
                return Material.DIAMOND_HELMET;
            case 311:
                return Material.DIAMOND_CHESTPLATE;
            case 312:
                return Material.DIAMOND_LEGGINGS;
            case 313:
                return Material.DIAMOND_BOOTS;
            case 314:
                return Material.GOLDEN_HELMET;
            case 315:
                return Material.GOLDEN_CHESTPLATE;
            case 316:
                return Material.GOLDEN_LEGGINGS;
            case 317:
                return Material.GOLDEN_BOOTS;
            case 318:
                return Material.FLINT;
            case 319:
                return Material.PORKCHOP;
            case 320:
                return Material.COOKED_PORKCHOP;
            case 321:
                return Material.PAINTING;
            case 322:
                return Material.GOLDEN_APPLE;
            case 323:
                return Material.LEGACY_SIGN;
            case 324:
                return Material.OAK_DOOR;
            case 325:
                return Material.BUCKET;
            case 326:
                return Material.WATER_BUCKET;
            case 327:
                return Material.LAVA_BUCKET;
            case 328:
                return Material.MINECART;
            case 329:
                return Material.SADDLE;
            case 330:
                return Material.IRON_DOOR;
            case 331:
                return Material.REDSTONE;
            case 332:
                return Material.SNOWBALL;
            case 333:
                return Material.OAK_BOAT;
            case 334:
                return Material.LEATHER;
            case 335:
                return Material.MILK_BUCKET;
            case 336:
                return Material.BRICK;
            case 337:
                return Material.CLAY_BALL;
            case 338:
                return Material.SUGAR_CANE;
            case 339:
                return Material.PAPER;
            case 340:
                return Material.BOOK;
            case 341:
                return Material.SLIME_BALL;
            case 342:
                return Material.CHEST_MINECART;
            case 343:
                return Material.FURNACE_MINECART;
            case 344:
                return Material.EGG;
            case 345:
                return Material.COMPASS;
            case 346:
                return Material.FISHING_ROD;
            case 347:
                return Material.CLOCK;
            case 348:
                return Material.GLOWSTONE_DUST;
            case 349:
                return Material.COD;
            case 350:
                return Material.COOKED_COD;
            case 351:
                return Material.INK_SAC;
            case 352:
                return Material.BONE;
            case 353:
                return Material.SUGAR;
            case 354:
                return Material.CAKE;
            case 355:
                return Material.WHITE_BED;
            case 356:
                return Material.REPEATER;
            case 357:
                return Material.COOKIE;
            case 358:
                return Material.MAP;
            case 359:
                return Material.SHEARS;
            case 360:
                return Material.MELON;
            case 361:
                return Material.PUMPKIN_SEEDS;
            case 362:
                return Material.MELON_SEEDS;
            case 363:
                return Material.BEEF;
            case 364:
                return Material.COOKED_BEEF;
            case 365:
                return Material.CHICKEN;
            case 366:
                return Material.COOKED_CHICKEN;
            case 367:
                return Material.ROTTEN_FLESH;
            case 368:
                return Material.ENDER_PEARL;
            case 369:
                return Material.BLAZE_ROD;
            case 370:
                return Material.GHAST_TEAR;
            case 371:
                return Material.GOLD_NUGGET;
            case 372:
                return Material.NETHER_WART;
            case 373:
                return Material.POTION;
            case 374:
                return Material.GLASS_BOTTLE;
            case 375:
                return Material.SPIDER_EYE;
            case 376:
                return Material.FERMENTED_SPIDER_EYE;
            case 377:
                return Material.BLAZE_POWDER;
            case 378:
                return Material.MAGMA_CREAM;
            case 379:
                return Material.BREWING_STAND;
            case 380:
                return Material.CAULDRON;
            case 381:
                return Material.ENDER_EYE;
            case 382:
                return Material.GLISTERING_MELON_SLICE;
            case 383:
                return Material.PIG_SPAWN_EGG;
            case 384:
                return Material.EXPERIENCE_BOTTLE;
            case 385:
                return Material.FIRE_CHARGE;
            case 386:
                return Material.WRITABLE_BOOK;
            case 387:
                return Material.WRITTEN_BOOK;
            case 388:
                return Material.EMERALD;
            case 389:
                return Material.ITEM_FRAME;
            case 390:
                return Material.FLOWER_POT;
            case 391:
                return Material.CARROT;
            case 392:
                return Material.POTATO;
            case 393:
                return Material.BAKED_POTATO;
            case 394:
                return Material.POISONOUS_POTATO;
            case 395:
                return Material.MAP;
            case 396:
                return Material.GOLDEN_CARROT;
            case 397:
                return Material.SKELETON_SKULL;
            case 398:
                return Material.CARROT_ON_A_STICK;
            case 399:
                return Material.NETHER_STAR;
            case 400:
                return Material.PUMPKIN_PIE;
            case 401:
                return Material.FIREWORK_ROCKET;
            case 402:
                return Material.FIREWORK_STAR;
            case 403:
                return Material.ENCHANTED_BOOK;
            case 404:
                return Material.COMPARATOR;
            case 405:
                return Material.NETHER_BRICK;
            case 406:
                return Material.QUARTZ;
            case 407:
                return Material.TNT_MINECART;
            case 408:
                return Material.HOPPER_MINECART;
            case 409:
                return Material.PRISMARINE_SHARD;
            case 410:
                return Material.PRISMARINE_CRYSTALS;
            case 411:
                return Material.RABBIT;
            case 412:
                return Material.COOKED_RABBIT;
            case 413:
                return Material.RABBIT_STEW;
            case 414:
                return Material.RABBIT_FOOT;
            case 415:
                return Material.RABBIT_HIDE;
            case 416:
                return Material.ARMOR_STAND;
            case 417:
                return Material.IRON_HORSE_ARMOR;
            case 418:
                return Material.GOLDEN_HORSE_ARMOR;
            case 419:
                return Material.DIAMOND_HORSE_ARMOR;
            case 420:
                return Material.LEAD;
            case 421:
                return Material.NAME_TAG;
            case 422:
                return Material.COMMAND_BLOCK_MINECART;
            case 423:
                return Material.MUTTON;
            case 424:
                return Material.COOKED_MUTTON;
            case 425:
                return Material.WHITE_BANNER;
            case 426:
                return Material.END_CRYSTAL;
            case 427:
                return Material.SPRUCE_DOOR;
            case 428:
                return Material.BIRCH_DOOR;
            case 429:
                return Material.JUNGLE_DOOR;
            case 430:
                return Material.ACACIA_DOOR;
            case 431:
                return Material.DARK_OAK_DOOR;
            case 432:
                return Material.CHORUS_FRUIT;
            case 433:
                return Material.POPPED_CHORUS_FRUIT;
            case 434:
                return Material.BEETROOT;
            case 435:
                return Material.BEETROOT_SEEDS;
            case 436:
                return Material.BEETROOT_SOUP;
            case 437:
                return Material.DRAGON_BREATH;
            case 438:
                return Material.SPLASH_POTION;
            case 439:
                return Material.SPECTRAL_ARROW;
            case 440:
                return Material.TIPPED_ARROW;
            case 441:
                return Material.LINGERING_POTION;
            case 442:
                return Material.SHIELD;
            case 443:
                return Material.ELYTRA;
            case 444:
                return Material.SPRUCE_BOAT;
            case 445:
                return Material.BIRCH_BOAT;
            case 446:
                return Material.JUNGLE_BOAT;
            case 447:
                return Material.ACACIA_BOAT;
            case 448:
                return Material.DARK_OAK_BOAT;
            case 449:
                return Material.TOTEM_OF_UNDYING;
            case 450:
                return Material.SHULKER_SHELL;
            case 452:
                return Material.IRON_NUGGET;
            case 453:
                return Material.KNOWLEDGE_BOOK;
            case 2256:
                return Material.MUSIC_DISC_13;
            case 2257:
                return Material.MUSIC_DISC_CAT;
            case 2258:
                return Material.MUSIC_DISC_BLOCKS;
            case 2259:
                return Material.MUSIC_DISC_CHIRP;
            case 2260:
                return Material.MUSIC_DISC_FAR;
            case 2261:
                return Material.MUSIC_DISC_MALL;
            case 2262:
                return Material.MUSIC_DISC_MELLOHI;
            case 2263:
                return Material.MUSIC_DISC_STAL;
            case 2264:
                return Material.MUSIC_DISC_STRAD;
            case 2265:
                return Material.MUSIC_DISC_WARD;
            case 2266:
                return Material.MUSIC_DISC_11;
            case 2267:
                return Material.MUSIC_DISC_WAIT;
            default:
                return null;
        }
    }

}
