package TheCopycat.patches;

import TheCopycat.cards.SlimeSlasher;
import TheCopycat.cards.monster.AbstractMonsterCard;
import TheCopycat.utils.GameLogicUtils;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.screens.runHistory.RunHistoryScreen;

public class CardLibraryPatch {
	@SpirePatch2(clz = CardLibrary.class, method = "getCopy", paramtypez = {String.class, int.class, int.class})
	public static class GetCopyPatch {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result) {
			if (__result instanceof SlimeSlasher) {
				((SlimeSlasher) __result).recalculateDamage();
			}
			return __result;
		}
	}

	@SpirePatch2(clz = RunHistoryScreen.class, method = "cardForName")
	public static class GetCardPatch {
		@SpirePostfixPatch
		public static AbstractCard Postfix(AbstractCard __result, String cardID) {
			if (__result == null && cardID.contains(GameLogicUtils.metricIdSeparator)) {
				__result = AbstractMonsterCard.createFromMetricID(cardID);
				if (__result == null) {
					return SlimeSlasher.createFromMetricID(cardID);
				}
			}
			return __result;
		}
	}

	@SpirePatch2(clz = CardLibrary.class, method = "getCardNameFromMetricID")
	public static class GetCardNameFromMetricIDPatch {
		@SpirePostfixPatch
		public static String Postfix(String __result, String metricID) {
			if (__result.equals(metricID) && metricID.contains(GameLogicUtils.metricIdSeparator)) {
				AbstractCard c = AbstractMonsterCard.createFromMetricID(metricID);
				if (c == null) {
					c = SlimeSlasher.createFromMetricID(metricID);
				}
				if (c != null) {
					return c.name;
				}
			}
			return __result;
		}
	}

	@SpirePatch2(clz = CardLibrary.class, method = "isACard")
	public static class IsACardPatch {
		@SpirePostfixPatch
		public static boolean Postfix(boolean __result, String metricID) {
			if (!__result) {
				if (metricID.contains(GameLogicUtils.metricIdSeparator)) {
					AbstractCard c = AbstractMonsterCard.createFromMetricID(metricID);
					if (c == null) {
						c = SlimeSlasher.createFromMetricID(metricID);
					}
					if (c != null) {
						return true;
					}
				}
			}
			return __result;
		}
	}
}
