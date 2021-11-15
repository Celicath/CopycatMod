package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.blights.Spiredex;
import TheCopycat.cards.SlimeSlasher;
import TheCopycat.cards.monster.AbstractMonsterCard;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.Exordium;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CardRewardPatch {
	@SpirePatch2(clz = AbstractDungeon.class, method = "initializeCardPools")
	public static class CardPoolPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon __instance) {
			if (!(__instance instanceof Exordium)) {
				AbstractDungeon.uncommonCardPool.group.removeIf(c -> c instanceof SlimeSlasher);
				AbstractDungeon.srcUncommonCardPool.group.removeIf(c -> c instanceof SlimeSlasher);
			}
		}
	}

	@SpirePatch2(clz = AbstractDungeon.class, method = "getRewardCards")
	public static class MonsterCardRewardPatch {
		@SpirePostfixPatch
		public static ArrayList<AbstractCard> Postfix(float ___cardUpgradedChance, ArrayList<AbstractCard> __result) {
			for (AbstractBlight b : AbstractDungeon.player.blights) {
				if (b instanceof Spiredex) {
					List<AbstractMonsterCard> monsterCards = CaptureEnemyMovePatch.generatedCards.values().stream().filter(
						c -> c.rarity == AbstractCard.CardRarity.COMMON ||
							c.rarity == AbstractCard.CardRarity.UNCOMMON ||
							c.rarity == AbstractCard.CardRarity.RARE).collect(Collectors.toList());
					((Spiredex) b).replaceCardRewards(monsterCards, __result, ___cardUpgradedChance);
				}
			}
			return __result;
		}
	}

	@SpirePatch2(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
	public static class RoomEnterPatch {
		@SpirePrefixPatch
		public static void Prefix() {
			CopycatModMain.clearMonsterCards();
			if (Spiredex.saveDataIndex > 0) {
				Spiredex.monsterCardRewardSaveData.clear();
				Spiredex.saveDataIndex = 0;
			}
		}
	}
}
