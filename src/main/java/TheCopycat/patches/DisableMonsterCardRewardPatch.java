package TheCopycat.patches;

import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import javassist.CtBehavior;

public class DisableMonsterCardRewardPatch {
	@SpirePatch2(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardType.class, AbstractCard.CardRarity.class})
	@SpirePatch2(clz = CardLibrary.class, method = "getAnyColorCard", paramtypez = {AbstractCard.CardRarity.class})
	public static class RemoveFromAnyColor {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(CardGroup ___anyCard) {
			___anyCard.group.removeIf(c -> c.color == CharacterEnum.CardColorEnum.COPYCAT_MONSTER);
		}

		private static class Locator extends SpireInsertLocator {
			@Override
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher finalMatcher = new Matcher.MethodCallMatcher(CardGroup.class, "shuffle");
				return LineFinder.findInOrder(ctMethodToPatch, finalMatcher);
			}
		}
	}
}
