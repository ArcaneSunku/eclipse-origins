package git.eclipse.core.game;

/**
 * <p>A semi-reflection of the modConstants files modules in both the server and client side of EO2.0(VB)</p>
 */
public class Constants {

    public static final int MAX_PACKET_SIZE = (int)5e6;

    // General Constants
    public static final long MAX_PLAYERS = 70;
    public static final long MAX_ITEMS = 255;
    public static final long MAX_NPCS = 255;
    public static final long MAX_ANIMATIONS = 255;
    public static final long MAX_INV = 35;
    public static final long MAX_MAP_ITEMS = 255;
    public static final long MAX_MAP_NPCS = 30;
    public static final long MAX_SHOPS = 50;
    public static final long MAX_PLAYER_SPELLS = 35;
    public static final long MAX_SPELLS = 255;
    public static final long MAX_TRADES = 30;
    public static final long MAX_RESOURCES = 100;
    public static final long MAX_LEVELS = 100;
    public static final long MAX_BANK = 99;
    public static final long MAX_HOTBAR = 12;
    public static final long MAX_PARTYS = 35;
    public static final long MAX_PARTY_MEMBERS = 4;

    // Boolean Constants
    public static final byte NO = 0;
    public static final byte YES = 1;

    // String Constants
    public static final byte NAME_LENGTH = 20;
    public static final byte ACCOUNT_LENGTH = 12;

    // Sex Constants
    public static final byte SEX_MALE = 0;
    public static final byte SEX_FEMALE = 1;
    public static final byte SEX_OTHER = 2;

    // Map Constants
    public static final long MAX_MAPS = 100;
    public static final byte MAX_MAPX = 14;
    public static final byte MAX_MAPY = 11;

    // Map Morals
    public static final byte MAP_MORAL_NONE = 0;
    public static final byte MAP_MORAL_SAFE = 1;

    // Tile Constants
    public static final byte TILE_TYPE_WALKABLE = 0;
    public static final byte TILE_TYPE_BLOCKED = 1;
    public static final byte TILE_TYPE_WARP = 2;
    public static final byte TILE_TYPE_ITEM = 3;
    public static final byte TILE_TYPE_NPCAVOID = 4;
    public static final byte TILE_TYPE_KEY = 5;
    public static final byte TILE_TYPE_KEYOPEN = 6;
    public static final byte TILE_TYPE_RESOURCE = 7;
    public static final byte TILE_TYPE_DOOR = 8;
    public static final byte TILE_TYPE_NPCSPAWN = 9;
    public static final byte TILE_TYPE_SHOP = 10;
    public static final byte TILE_TYPE_BANK = 11;
    public static final byte TILE_TYPE_HEAL = 12;
    public static final byte TILE_TYPE_TRAP = 13;
    public static final byte TILE_TYPE_SLIDE = 14;

    // Item Constants
    public static final byte ITEM_TYPE_NONE = 0;
    public static final byte ITEM_TYPE_WEAPON = 1;
    public static final byte ITEM_TYPE_ARMOR = 2;
    public static final byte ITEM_TYPE_HELMET = 3;
    public static final byte ITEM_TYPE_SHIELD = 4;
    public static final byte ITEM_TYPE_CONSUME = 5;
    public static final byte ITEM_TYPE_KEY = 6;
    public static final byte ITEM_TYPE_CURRENCY = 7;
    public static final byte ITEM_TYPE_SPELL = 8;

}
