package slimeknights.tconstruct.library.modifiers.hook.ranged;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;

/**
 * Hook for when a tool that is also a projectile successfully hits.
 * Unlike {@link ProjectileHitModifierHook}, we know the tool that caused the action.
 * Unlike {@link slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook}, we have a projectile involved.
 */
public interface LauncherHitModifierHook {
  /**
   * Called when a tool projectile successfully hits an entity.
   * @param tool          Tool linked to the projectile
   * @param modifier      Modifier entry
   * @param projectile    Projectile that hit
   * @param attacker      Entity using the tool
   * @param target        Target of the hit
   * @param livingTarget  If target is living, the target casted to living
   * @param damageDealt   Amount of damage dealt
   */
  void onToolProjectileHit(IToolStackView tool, ModifierEntry modifier, Projectile projectile, LivingEntity attacker, Entity target, @Nullable LivingEntity livingTarget, float damageDealt);

  /** Merger that runs each hook one after another */
  record AllMerger(Collection<LauncherHitModifierHook> modules) implements LauncherHitModifierHook {
    @Override
    public void onToolProjectileHit(IToolStackView tool, ModifierEntry modifier, Projectile projectile, LivingEntity attacker, Entity target, @Nullable LivingEntity livingTarget, float damageDealt) {
      for (LauncherHitModifierHook module : modules) {
        module.onToolProjectileHit(tool, modifier, projectile, attacker, target, livingTarget, damageDealt);
      }
    }
  }
}
