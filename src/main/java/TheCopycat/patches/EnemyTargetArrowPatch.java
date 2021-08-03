package TheCopycat.patches;

import TheCopycat.friendlyminions.AbstractCopycatMinion;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import TheCopycat.utils.CopycatTargetArrow;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;

public class EnemyTargetArrowPatch {
	private static float arrowTime = 0.0f;
	private static float alpha = 0.0f;
	private static AbstractMonster hoveredMonster = null;

	private static final float alphaSpeed = 3.0f;

	@SpirePatch(clz = MonsterGroup.class, method = "render")
	public static class MonsterGroupRenderPatch {
		@SpirePostfixPatch
		public static void Postfix(MonsterGroup __instance, SpriteBatch sb) {
			if (__instance == BetterFriendlyMinionsUtils.getMonsterGroup()) {
				return;
			}
			if (AbstractDungeon.player.hoveredCard != null) {
				arrowTime = 0.0f;
				alpha = 0.0f;
				hoveredMonster = null;
				return;
			}
			AbstractMonster theirHovered = __instance.hoveredMonster;
			if (theirHovered != null && theirHovered.isDeadOrEscaped()) {
				theirHovered = null;
			}
			if (hoveredMonster != theirHovered) {
				if (theirHovered == null) {
					alpha -= Gdx.graphics.getDeltaTime() * alphaSpeed;
					if (alpha <= 0.0f) {
						alpha = 0.0f;
						hoveredMonster = null;
						arrowTime = 0.0f;
					}
				} else {
					hoveredMonster = theirHovered;
					alpha = 0.0f;
					arrowTime = 0.0f;
				}
			} else if (hoveredMonster != null) {
				alpha += Gdx.graphics.getDeltaTime() * alphaSpeed / 4;
				if (alpha > 0.7f) {
					alpha = 0.7f;
				}
			}

			if (hoveredMonster != null) {
				if (alpha > 0.1f) {
					AbstractCreature target = BetterFriendlyMinionsUtils.getTarget(hoveredMonster);
					if (target instanceof AbstractCopycatMinion) {
						CopycatTargetArrow.drawTargetArrow(
								sb, hoveredMonster.hb, target.hb, CopycatTargetArrow.CONTROL_HEIGHT * Settings.scale, arrowTime, alpha - 0.1f,
								AbstractCopycatMinion.arrowColors[((AbstractCopycatMinion) target).index]);
					}
				}
				arrowTime += Gdx.graphics.getDeltaTime();
			}
		}
	}
}
