package es.udc.ps.wikirym.app.data;

import es.udc.ps.wikirym.app.domain.EntryType;

// Categorías de la wiki de UESP para cada tipo de entrada
public final class UespCategories {

    private static final String CATEGORY_PREFIX = "Category:";

    private static final String NPCS = "Skyrim-NPCs";
    private static final String QUESTS = "Skyrim-Quests";
    private static final String ITEMS = "Skyrim-Items";
    private static final String PLACES = "Skyrim-Places";

    // Casi todos los objetos están en subcategorías, e incategory: no entra en ellas
    private static final String ITEMS_SEARCH = ITEMS + "|Skyrim-Items-Artifacts|Skyrim-Items-Unique_Items"
            + "|Skyrim-Armor|Skyrim-Jewelry|Skyrim-Ingredients|Skyrim-Weapons-Swords|Skyrim-Weapons-Greatswords"
            + "|Skyrim-Weapons-Daggers|Skyrim-Weapons-Bows|Skyrim-Weapons-War_Axes|Skyrim-Weapons-Battleaxes"
            + "|Skyrim-Weapons-Maces|Skyrim-Weapons-Warhammers|Skyrim-Weapons-Staves";

    private UespCategories() {

    }

    // Para list=categorymembers, p. ej. "Category:Skyrim-NPCs"
    public static String getCategory(EntryType type) {

        return CATEGORY_PREFIX + getName(type);
    }

    // Para el filtro incategory: de list=search, p. ej. "Skyrim-NPCs"
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
