package TheCopycat.patches;

import TheCopycat.characters.Copycat;
import basemod.BaseMod;
import basemod.patches.com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar.ColorTabBarFix;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.screens.mainMenu.ColorTabBar;
import javassist.CtBehavior;

import java.util.ArrayList;
import java.util.List;

public class MonsterCardLibraryPatch {
	@SpirePatch(clz = ColorTabBarFix.Render.class, method = "Insert")
	public static class ColorTabBarPatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(ColorTabBar __instance, SpriteBatch sb, float y, ColorTabBar.CurrentTab curTab, @ByRef String[] ___tabName) {
			if (___tabName[0].equals(capitalizeWord(CharacterEnum.CardColorEnum.COPYCAT_MONSTER.toString()))) {
				___tabName[0] = Copycat.charStrings.NAMES[2];
			}
		}

		public static String capitalizeWord(String str) {
			if (str.isEmpty()) {
				return str;
			}
			return str.substring(0, 1).toUpperCase() + (str.length() > 1 ? str.substring(1).toLowerCase() : "");
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(FontHelper.class, "renderFontCentered");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}

	@SpirePatch(clz = BaseMod.class, method = "getCardColors")
	public static class GetCardColorsPatch {
		@SpirePostfixPatch
		public static List<AbstractCard.CardColor> Postfix(List<AbstractCard.CardColor> __return) {
			if (__return instanceof ArrayList) {
				ArrayList<AbstractCard.CardColor> array = (ArrayList<AbstractCard.CardColor>) __return;
				array.remove(CharacterEnum.CardColorEnum.COPYCAT_MONSTER);
				int index = array.indexOf(CharacterEnum.CardColorEnum.COPYCAT_BLUE);
				array.add(index + 1, CharacterEnum.CardColorEnum.COPYCAT_MONSTER);
				return array;
			} else {
				return __return;
			}
		}
	}
}