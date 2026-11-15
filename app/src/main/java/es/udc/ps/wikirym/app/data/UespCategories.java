package es.udc.ps.wikirym.app.data;

import es.udc.ps.wikirym.app.domain.EntryType;

// UESP wiki categories for every type of entry (checked in https://en.uesp.net/w/api.php)
public final class UespCategories {

    private static final String CATEGORY_PREFIX = "Category:";

    private static final String NPCS = "Skyrim-NPCs";
    private static final String QUESTS = "Skyrim-Quests";
    private static final String ITEMS = "Skyrim-Items";
    private static final String PLACES = "Skyrim-Places";

    // Most items (weapons, armor, artifacts...) are only in subcategories of Skyrim-Items,
    // and the incategory: filter of the search does not look into subcategories
    private static final String ITEMS_SEARCH = ITEMS + "|Skyrim-Items-Artifacts|Skyrim-Items-Unique_Items"
            + "|Skyrim-Armor|Skyrim-Jewelry|Skyrim-Ingredients|Skyrim-Weapons-Swords|Skyrim-Weapons-Greatswords"
            + "|Skyrim-Weapons-Daggers|Skyrim-Weapons-Bows|Skyrim-Weapons-War_Axes|Skyrim-Weapons-Battleaxes"
            + "|Skyrim-Weapons-Maces|Skyrim-Weapons-Warhammers|Skyrim-Weapons-Staves";

    private UespCategories() {

    }

    // Category to list with list=categorymembers, e.g. "Category:Skyrim-NPCs"
    public static String getCategory(EntryType type) {

        return CATEGORY_PREFIX + getName(type);
    }

    // Value of the incategory: filter of list=search, e.g. "Skyrim-NPCs"
    public static String getSearchCategories(EntryType type) {

        return type == EntryType.ITEM ? ITEMS_SEARCH : getName(type);
    }

    private static String getName(EntryType type) {

        switch (type) {
            case QUEST:
                return QUESTS;
            case ITEM:
                return ITEMS;
            case PLACE:
                return PLACES;
            case CHARACTER:
            default:
                return NPCS;
        }
    }
}
