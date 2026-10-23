package com.example.damagehud.mixin;

import com.example.damagehud.DamageMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void damagehud$render(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;
        HitResult hit = mc.hitResult;
        if (!(hit instanceof EntityHitResult entityHit)) return;
        if (!(entityHit.getEntity() instanceof Player target) || target == mc.player) return;

        DamageMath.Result result = DamageMath.estimate(mc.player, target);
        String text = "§c❤ " + String.format(java.util.Locale.ROOT, "%.1f", target.getHealth())
                + "  §7| §e-" + String.format(java.util.Locale.ROOT, "%.1f", result.normal()) + " HP"
                + (result.critical() ? " §6(crit " + String.format(java.util.Locale.ROOT, "%.1f", result.criticalDamage()) + ")" : "");
        int width = mc.font.width(text.replaceAll("§.", ""));
        int x = (graphics.guiWidth() - width) / 2;
        int y = graphics.guiHeight() / 2 + 12;
        graphics.fill(x - 4, y - 3, x + width + 4, y + mc.font.lineHeight + 3, 0x99000000);
        graphics.drawString(mc.font, text, x, y, 0xFFFFFF, false);
    }
}
