package TheCopycat.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import javassist.CtBehavior;

public class MonsterRenderPatch {
	@SpirePatch2(clz = AbstractMonster.class, method = "render")
	public static class RenderForMonsterCardPatch {
		public static boolean shouldPatch = false;

		@SpireInsertPatch(locator = Locator.class)
		public static SpireReturn<Void> Insert() {
			if (shouldPatch) {
				return SpireReturn.Return();
			} else {
				return SpireReturn.Continue();
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.FieldAccessMatcher(MonsterGroup.class, "hoveredMonster");
				return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}
