package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.VariableDamageCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class HoverMonsterPatch {
	@SpirePatch(clz = AbstractPlayer.class, method = "renderHand")
	public static class HoverPatch {
		public static AbstractCard prevHoveredCard = null;
		public static AbstractMonster prevHoveredMonster = null;

		@SpirePostfixPatch
		public static void Postfix(AbstractPlayer __instance, SpriteBatch sb, AbstractMonster ___hoveredMonster) {
			if (prevHoveredCard != __instance.hoveredCard) {
				if (prevHoveredCard instanceof VariableDamageCard) {
					((VariableDamageCard) prevHoveredCard).onUnhoverMonster();
				}
			}
			if (__instance.hoveredCard != null) {
				if (__instance.hoveredCard instanceof VariableDamageCard && ___hoveredMonster != prevHoveredMonster) {
					if (___hoveredMonster != null) {
						((VariableDamageCard) __instance.hoveredCard).onHoverMonster(___hoveredMonster);
					} else {
						((VariableDamageCard) __instance.hoveredCard).onUnhoverMonster();
					}
				}
				if (___hoveredMonster != null && __instance.hoveredCard.hasTag(CharacterEnum.CustomTags.COPYCAT_MIMIC)) {
					AbstractCard c = CopycatModMain.getEnemyLastMoveCard(___hoveredMonster);
					c.drawScale = 0.75f;
					c.current_x = InputHelper.mX + 50.0F * Settings.scale;
					c.current_y = InputHelper.mY + 180.0F * Settings.scale;
					c.render(sb);
				}
			}

			prevHoveredCard = __instance.hoveredCard;
			prevHoveredMonster = ___hoveredMonster;
		}
	}
}
