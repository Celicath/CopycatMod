package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.cards.BeatUp;
import TheCopycat.interfaces.HoverMonsterCard;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HoverMonsterPatch {
	@SpirePatch(clz = AbstractPlayer.class, method = "renderHand")
	public static class HoverPatch {
		private static final int DAMAGE_BG_WIDTH = 160;
		private static final int DAMAGE_BG_HEIGHT = 60;
		public static AbstractCard prevHoveredCard = null;
		public static AbstractMonster prevHoveredMonster = null;
		private static Texture damageBgTexture = null;

		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer __instance, SpriteBatch sb, AbstractMonster ___hoveredMonster) {
			if (prevHoveredCard != __instance.hoveredCard) {
				if (prevHoveredCard instanceof HoverMonsterCard) {
					((HoverMonsterCard) prevHoveredCard).onUnhoverMonster();
				}
			}
			if (__instance.hoveredCard != null) {
				if (__instance.hoveredCard instanceof HoverMonsterCard && ___hoveredMonster != prevHoveredMonster) {
					if (___hoveredMonster != null) {
						((HoverMonsterCard) __instance.hoveredCard).onHoverMonster(___hoveredMonster);
					} else {
						((HoverMonsterCard) __instance.hoveredCard).onUnhoverMonster();
					}
				}
				if (___hoveredMonster != null && __instance.hoveredCard.hasTag(CharacterEnum.CustomTags.COPYCAT_MIMIC)) {
					AbstractCard c = CopycatModMain.getEnemyLastMoveCard(___hoveredMonster);
					c.drawScale = 0.75f;
					c.current_x = InputHelper.mX + 50.0F * Settings.scale;
					c.current_y = InputHelper.mY + 180.0F * Settings.scale;
					c.render(sb);
				}
				if (__instance.hoveredCard instanceof BeatUp && ___hoveredMonster != null) {
					BeatUp bu = ((BeatUp) __instance.hoveredCard);
					bu.damageMap.forEach((c, damage) -> {
						if (damageBgTexture == null) {
							damageBgTexture = ImageMaster.loadImage(CopycatModMain.makePath("ui/DamageBG.png"));
						}
						sb.draw(
							damageBgTexture,
							c.hb.cX - DAMAGE_BG_WIDTH / 2.0f,
							c.hb.cY - DAMAGE_BG_HEIGHT / 2.0f,
							DAMAGE_BG_WIDTH / 2.0f,
							DAMAGE_BG_HEIGHT / 2.0f,
							DAMAGE_BG_WIDTH,
							DAMAGE_BG_HEIGHT,
							Settings.scale, Settings.scale, 0.0F, 0, 0, DAMAGE_BG_WIDTH, DAMAGE_BG_HEIGHT, false, false);

						FontHelper.renderFontCentered(
							sb,
							FontHelper.panelNameFont,
							BeatUp.EXTENDED_DESCRIPTION[2] + (
								damage > bu.baseDamage ? "[#7fff00]" + damage + "[]" :
									damage < bu.baseDamage ? "[#ff6563]" + damage + "[]" :
										Integer.toString(damage)
							) + BeatUp.EXTENDED_DESCRIPTION[3],
							c.hb.cX,
							c.hb.cY,
							Color.WHITE.cpy());
					});
				}
			}

			prevHoveredCard = __instance.hoveredCard;
			prevHoveredMonster = ___hoveredMonster;
		}
	}
}
