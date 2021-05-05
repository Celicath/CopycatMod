package TheCopycat.patches;

import TheCopycat.cards.GlowingCard;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.screens.CardRewardScreen;
import com.megacrit.cardcrawl.screens.select.GridCardSelectScreen;
import com.megacrit.cardcrawl.screens.select.HandCardSelectScreen;
import com.megacrit.cardcrawl.ui.buttons.SingingBowlButton;
import javassist.CtBehavior;

import java.util.ArrayList;

public class GlowingCardPatch {

	static void activateGlowingCard(AbstractCard c) {
		if (c instanceof GlowingCard) {
			if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom() != null && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
				((GlowingCard) c).activateInCombat();
			} else {
				((GlowingCard) c).activateOutOfCombat();
			}
		}
	}

	@SpirePatch(clz = HandCardSelectScreen.class, method = "update")
	public static class HandCardSelectPatch {
		@SpireInsertPatch(locator = CloseLocator.class)
		public static void Insert(HandCardSelectScreen __instance, CardGroup ___selectedCards) {
			for (AbstractCard c : ___selectedCards.group) {
				activateGlowingCard(c);
			}
		}
	}

	@SpirePatch(clz = GridCardSelectScreen.class, method = "update")
	public static class GridCardSelectPatch {
		@SpireInsertPatch(locator = CloseLocator.class)
		public static void Insert(GridCardSelectScreen __instance, ArrayList<AbstractCard> ___selectedCards) {
			for (AbstractCard c : ___selectedCards) {
				activateGlowingCard(c);
			}
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "update")
	public static class CardRewardScreenPatch1 {
		@SpireInsertPatch(locator = TakeRewardLocator.class)
		public static void Insert(CardRewardScreen __instance, AbstractCard ___touchCard) {
			activateGlowingCard(___touchCard);
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "cardSelectUpdate")
	public static class CardRewardScreenPatch2 {
		@SpireInsertPatch(locator = TakeRewardLocator.class)
		public static void Insert(CardRewardScreen __instance, AbstractCard ___hoveredCard) {
			activateGlowingCard(___hoveredCard);
		}
	}

	@SpirePatch(clz = CardRewardScreen.class, method = "completeVoting")
	public static class CardRewardScreenPatch3 {
		@SpireInsertPatch(locator = TakeRewardLocator.class)
		public static void Insert(CardRewardScreen __instance, int option, SingingBowlButton ___bowlButton) {
			if (option > 0) {
				if (!___bowlButton.isHidden() && option != 1) {
					activateGlowingCard(__instance.rewardGroup.get(option - 2));
				} else if (option < __instance.rewardGroup.size() + 1) {
					activateGlowingCard(__instance.rewardGroup.get(option - 1));
				}
			}
		}
	}

	private static class CloseLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(AbstractDungeon.class, "closeCurrentScreen");
			return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
		}
	}

	private static class TakeRewardLocator extends SpireInsertLocator {
		@Override
		public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(CardRewardScreen.class, "takeReward");
			return LineFinder.findAllInOrder(ctMethodToPatch, finalMatcher);
		}
	}
}
