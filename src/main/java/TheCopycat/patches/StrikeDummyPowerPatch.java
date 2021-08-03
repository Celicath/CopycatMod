package TheCopycat.patches;

import TheCopycat.interfaces.MinionAuraEffect;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

public class StrikeDummyPowerPatch {
	@SpirePatch2(clz = AbstractCard.class, method = "applyPowers")
	@SpirePatch2(clz = AbstractCard.class, method = "calculateCardDamage")
	public static class MinionAuraPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(AbstractCard __instance, @ByRef float[] ___tmp) {
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				for (AbstractPower p : m.powers) {
					if (p instanceof MinionAuraEffect) {
						___tmp[0] = ((MinionAuraEffect) p).atDamageModify(___tmp[0], __instance);
					}
				}
			}
		}

		@SpireInsertPatch(locator = Locator2.class)
		public static void Insert(AbstractCard __instance, float[] ___tmp, int ___i) {
			for (AbstractMonster m : BetterFriendlyMinionsUtils.getMinionList()) {
				for (AbstractPower p : m.powers) {
					if (p instanceof MinionAuraEffect) {
						___tmp[___i] = ((MinionAuraEffect) p).atDamageModify(___tmp[___i], __instance);
					}
				}
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "stance");
				return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[0]};
			}
		}

		private static class Locator2 extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(AbstractPlayer.class, "stance");
				return new int[]{LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher)[1]};
			}
		}
	}
}
