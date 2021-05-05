package TheCopycat.actions;

import TheCopycat.CopycatModMain;
import TheCopycat.crossovers.BetterFriendlyMinions;
import TheCopycat.friendlyminions.AbstractCopycatMinion;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

public class SummonCopycatMinionAction extends AbstractGameAction {
	private static final UIStrings uiStrings = CardCrawlGame.languagePack.getUIString(CopycatModMain.makeID("Minion"));
	public static final String[] TEXT = uiStrings.TEXT;

	AbstractCopycatMinion m;

	public SummonCopycatMinionAction(AbstractCopycatMinion m) {
		actionType = ActionType.POWER;
		duration = Settings.ACTION_DUR_FAST;
		this.m = m;
	}

	public void update() {
		if (!BetterFriendlyMinions.summonCopycatMinion(m)) {
			AbstractDungeon.effectsQueue.add(new ThoughtBubble(AbstractDungeon.player.dialogX, AbstractDungeon.player.dialogY, 3.0F, TEXT[0], true));
		}
		isDone = true;
	}
}
