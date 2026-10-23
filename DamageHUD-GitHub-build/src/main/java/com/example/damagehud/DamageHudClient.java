package com.example.damagehud;

import net.fabricmc.api.ClientModInitializer;

public final class DamageHudClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Rendering is injected into the vanilla HUD by GuiMixin.
    }
}
