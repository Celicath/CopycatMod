package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.relics.Spiredex;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.red.Metallicize;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class CardRewardPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "initializeCardPools")
	public static class CardPoolPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon __instance) {
			if (AbstractDungeon.player.chosenClass == CharacterEnum.PlayerClassEnum.THE_COPYCAT) {
				ArrayList<AbstractCard> tmpPool = new ArrayList<>();
				AbstractDungeon.player.getCardPool(tmpPool);
				CardLibrary.addRedCards(tmpPool);
				CardLibrary.addGreenCards(tmpPool);
				CardLibrary.addBlueCards(tmpPool);
				CardLibrary.addPurpleCards(tmpPool);

				for (AbstractCard c : tmpPool) {
					if (c instanceof Metallicize) {
						continue;
					}
					switch (c.rarity) {
						case COMMON:
							AbstractDungeon.commonCardPool.addToTop(c);
							AbstractDungeon.srcCommonCardPool.addToTop(c);
							break;
						case UNCOMMON:
							AbstractDungeon.uncommonCardPool.addToTop(c);
							AbstractDungeon.srcUncommonCardPool.addToTop(c);
							break;
						case RARE:
							AbstractDungeon.rareCardPool.addToTop(c);
							AbstractDungeon.srcRareCardPool.addToTop(c);
							break;
					}
				}
			}
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
	public static class MonsterCardRewardPatch {
		@SpirePostfixPatch
		public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result, float ___cardUpgradedChance) {
			ArrayList<AbstractCard> monsterCards = new ArrayList<>(CaptureEnemyMovePatch.generatedCards.values());
			for (AbstractRelic r : AbstractDungeon.player.relics) {
				if (r instanceof Spiredex) {
					((Spiredex) r).addCardInReward(monsterCards, __result, ___cardUpgradedChance);
				}
			}
			return __result;
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
	public static class RoomEnterPatch {
		@SpirePostfixPatch
		public static void Prefix(AbstractDungeon __instance, SaveFile saveFile) {
			CopycatModMain.clearMonsterCards();
		}
	}
}
