package TheCopycat.patches;

import TheCopycat.powers.HandSize12Power;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.MaxHandSizePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;

public class BetterHandLayoutPatch {
	@SpirePatch2(clz = MaxHandSizePatch.RefreshHandLayout.class, method = "PositionCards")
	public static class RefreshHandLayoutPatch {
		static float[] X11 = new float[]{-3.0f, -2.5f, -1.9f, -1.3f, -0.7f, 0.0f, 0.7f, 1.3f, 1.9f, 2.5f, 3.0f};
		static float[] Y11 = new float[]{0.0f, 25.0f, 40.0f, 35.0f, 28.5f, 21.5f, 28.5f, 35.0f, 40.0f, 25.0f, 0.0f};
		static float[] X12 = new float[]{-3.1f, -2.65f, -2.1f, -1.5f, -0.9f, -0.3f, 0.3f, 0.9f, 1.5f, 2.1f, 2.65f, 3.1f};
		static float[] Y12 = new float[]{0.0f, 20.0f, 33.5f, 31.0f, 26.5f, 21.5f, 21.5f, 26.5f, 31.0f, 33.5f, 20.0f, 0.0f};

		@SpirePostfixPatch
		public static SpireReturn<Void> Postfix(CardGroup hand) {
			if (hand.size() <= 12 && (AbstractDungeon.player.chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT || AbstractDungeon.player.hasPower(HandSize12Power.POWER_ID))) {
				for (int i = 0; i < hand.size(); i++) {
					AbstractCard card = hand.group.get(i);
					card.setAngle(-50.0F / (hand.size() - 1) * (i - hand.size() / 2.0f + 0.5f));
					card.target_x = Settings.WIDTH / 2.0F + AbstractCard.IMG_WIDTH_S * (hand.size() == 11 ? X11[i] : X12[i]);
					card.target_y += Settings.scale * (hand.size() == 11 ? Y11[i] : Y12[i]);
					card.targetDrawScale = (81 - 3 * hand.size()) / 80.0f;
				}
				return SpireReturn.Return();
			} else {
				return SpireReturn.Continue();
			}
		}
	}
}
