package TheCopycat.patches;

import TheCopycat.interfaces.PostCombatActivateAction;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import javassist.CtBehavior;

import java.util.Iterator;

public class PostCombatActivateActionPatch {
	@SpirePatch2(clz = GameActionManager.class, method = "clearPostCombatActions")
	public static class RenderForMonsterCardPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(AbstractGameAction ___e) {
			if (___e instanceof PostCombatActivateAction) {
				___e.update();
			}
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(Iterator.class, "remove");
				return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}
