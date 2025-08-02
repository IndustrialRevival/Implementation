package org.irmc.industrialrevival.implementation.services;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.irmc.industrialrevival.core.managers.LanguageManager;

import java.util.List;
import java.util.function.Consumer;

public class LanguageTextService {
    private static final List<LanguageManager> languageManagers;

    static {
        languageManagers = new ObjectArrayList<>();
    }

    public static void register(LanguageManager languageManager) {
        languageManagers.add(languageManager);
    }

    public static void translate(Consumer<LanguageManager> consumer) {
        for (LanguageManager languageManager : languageManagers) {
            consumer.accept(languageManager);
        }
    }
}
