package TheCopycat.patches;

import TheCopycat.CopycatModMain;
import TheCopycat.blights.Spiredex;
import TheCopycat.characters.Copycat;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch2;
import com.evacipated.cardcrawl.modthespire.lib.SpirePostfixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePrefixPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireReturn;
import com.megacrit.cardcrawl.blights.AbstractBlight;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.BlightHelper;
import com.megacrit.cardcrawl.neow.NeowEvent;

public class BlightPatch {

	@SpirePatch2(clz = BlightHelper.class, method = "getBlight")
	public static class BlightIDPatch {
		@SpirePrefixPatch
		public static SpireReturn<AbstractBlight> Prefix(String id) {
			if (id.equals(Spiredex.ID)) {
				return SpireReturn.Return(new Spiredex());
			}
			return SpireReturn.Continue();
		}
	}

	@SpirePatch2(clz = NeowEvent.class, method = "update")
	public static class HandCardSelectPatch {
		@SpirePostfixPatch
		public static void Prefix(boolean ___setPhaseToEvent) {
			if (!___setPhaseToEvent && !AbstractDungeon.player.hasBlight(Spiredex.ID)) {
				Spiredex dex = null;
				if (CardCrawlGame.trial != null && CardCrawlGame.trial.dailyModIDs().contains(CopycatModMain.makeID("MonsterOnly"))) {
					dex = new Spiredex(9);
				} else if (AbstractDungeon.player instanceof Copycat) {
					dex = new Spiredex(1);
				}
				if (dex != null) {
					AbstractDungeon.getCurrRoom().spawnBlightAndObtain(100.0f * Settings.scale, Settings.HEIGHT - 200.0f * Settings.scale, dex);
				}
			}
		}
	}
}
