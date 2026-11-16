package es.udc.ps.wikirym.app.presentation;

import androidx.annotation.StringRes;
import es.udc.ps.wikirym.R;
import es.udc.ps.wikirym.app.domain.EntryType;

// Texto de cada tipo; el enum del dominio no conoce los recursos de Android
public final class EntryTypeLabels {

    private EntryTypeLabels() {

    }

    @StringRes
    public static int getLabel(EntryType type) {

        if (type == null) {
            return R.string.entry_type_character;
        }

        switch (type) {
            case QUEST:
                return R.string.entry_type_quest;
            case ITEM:
                return R.string.entry_type_item;
            case PLACE:
                return R.string.entry_type_place;
            case CHARACTER:
            default:
                return R.string.entry_type_character;
        }
    }
}
