package TheCopycat.patches;

import TheCopycat.friendlyminions.Replica;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import javassist.CtBehavior;

public class SummoningMirrorPatch {

	@SpirePatch2(clz = AbstractRoom.class, method = "update")
	public static class CreateRelicPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert() {
			for (AbstractRelic r : Replica.relicMap.keySet()) {
				r.atBattleStartPreDraw();
				AbstractDungeon.player.relics.add(r);
			}
		}
	}

	private static class Locator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractPlayer.class, "applyStartOfTurnRelics");
			return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	@SpirePatch2(clz = AbstractPlayer.class, method = "onVictory")
	public static class RemoveRelicPatch {
		@SpirePostfixPatch
		public static void Postfix() {
			Replica.resetRelics();
		}
	}

	@SpirePatch2(clz = AbstractRelic.class, method = "update")
	public static class UpdateRelicPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractRelic __instance, @ByRef float[] ___rotation) {
			Replica minion = Replica.relicMap.get(__instance);
			if (minion != null) {
				___rotation[0] = minion.relicRotation;
				__instance.currentX = __instance.targetX = minion.drawX;
				__instance.currentY = __instance.targetY = minion.drawY + 96.0f * Settings.scale;
			}
		}
	}
}
