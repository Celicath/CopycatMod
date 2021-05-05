package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.blights.Spiredex;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.saveAndContinue.SaveFile;

import java.util.ArrayList;

public class CardRewardPatch {
	@SpirePatch(clz = AbstractDungeon.class, method = "getRewardCards")
	public static class MonsterCardRewardPatch {
		@SpirePostfixPatch
		public static ArrayList<AbstractCard> Postfix(ArrayList<AbstractCard> __result, float ___cardUpgradedChance) {
			for (AbstractBlight b : AbstractDungeon.player.blights) {
				if (b instanceof Spiredex) {
					ArrayList<AbstractCard> monsterCards = new ArrayList<>(CaptureEnemyMovePatch.generatedCards.values());
					((Spiredex) b).replaceCardRewards(monsterCards, __result, ___cardUpgradedChance);
				}
			}
			return __result;
		}
	}

	@SpirePatch(clz = AbstractDungeon.class, method = "nextRoomTransition", paramtypez = {SaveFile.class})
	public static class RoomEnterPatch {
		@SpirePostfixPatch
		public static void Postfix(AbstractDungeon __instance, SaveFile saveFile) {
			CopycatModMain.clearMonsterCards();
			if (Spiredex.saveDataIndex > 0) {
				Spiredex.monsterCardRewardSaveData.clear();
				Spiredex.saveDataIndex = 0;
			}
		}
	}
}
