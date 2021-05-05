package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.blights.Spiredex;
import TheCopycat.cards.monster.AbstractMonsterCard;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.TutorialStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.ui.FtueTip;

import java.io.IOException;

public class TutorialPatch {
	private static final TutorialStrings tutorialStrings = CardCrawlGame.languagePack.getTutorialString(CopycatModMain.makeID("Rewards"));
	public static final String[] TEXT = tutorialStrings.TEXT;
	public static final String[] LABEL = tutorialStrings.LABEL;

	static boolean loadTutorialConfig() {
		try {
			SpireConfig config = new SpireConfig(CopycatModMain.MOD_ID, "ConfigData");
			config.load();
			return config.getBool("RewardTutorial");
		} catch (IOException ignore) {
			saveTutorialConfig(false);
			return false;
		}
	}

	static void saveTutorialConfig(boolean seen) {
		try {
			SpireConfig config = new SpireConfig(CopycatModMain.MOD_ID, "ConfigData");
			config.setBool("RewardTutorial", seen);
			config.save();
		} catch (IOException ignore) {
		}
	}

	@SpirePatch(clz = RewardItem.class, method = "claimReward")
	public static class RewardPatch {
		@SpirePostfixPatch
		public static void Postfix(RewardItem __instance) {
			if (__instance.type == RewardItem.RewardType.CARD) {
				for (AbstractBlight b : AbstractDungeon.player.blights) {
					if (b instanceof Spiredex && !Spiredex.monsterCardRewardSaveData.isEmpty()) {
						b.flash();
					}
				}
				boolean seenTutorial = loadTutorialConfig();

				if (!seenTutorial) {
					if (!__instance.cards.isEmpty()) {
						AbstractCard card = __instance.cards.get(0);
						for (AbstractCard c : __instance.cards) {
							if (c instanceof AbstractMonsterCard) {
								card = c;
								break;
							}
						}
						AbstractDungeon.ftue = new FtueTip(LABEL[0], TEXT[0], Settings.WIDTH / 2.0F - 500.0F * Settings.scale, Settings.HEIGHT / 2.0F, card);
						saveTutorialConfig(true);
					}
				}
			}
		}
	}
}
