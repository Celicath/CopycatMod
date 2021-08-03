package TheCopycat.patches;

import TheCopycat.interfaces.OnDiscardPower;
import TheCopycat.interfaces.OnEvokePower;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import javassist.CtBehavior;

public class CopycatFormPatch {
	@SpirePatch2(clz = GameActionManager.class, method = "incrementDiscard")
	public static class DiscardPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert() {
			for (AbstractPower p : AbstractDungeon.player.powers) {
				if (p instanceof OnDiscardPower) {
					((OnDiscardPower) p).onDiscard();
				}
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "updateCardsOnDiscard");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "evokeOrb")
	@SpirePatch2(clz = AbstractPlayer.class, method = "evokeNewestOrb")
	@SpirePatch2(clz = AbstractPlayer.class, method = "evokeWithoutLosingOrb")
	public static class EvokePatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert() {
			for (AbstractPower p : AbstractDungeon.player.powers) {
				if (p instanceof OnEvokePower) {
					((OnEvokePower) p).onEvoke();
				}
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractOrb.class, "onEvoke");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}
