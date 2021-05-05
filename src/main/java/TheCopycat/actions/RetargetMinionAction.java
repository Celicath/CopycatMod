package TheCopycat.actions;

import TheCopycat.crossovers.BetterFriendlyMinions;
import TheCopycat.patches.CopycatMinionPatch;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public class RetargetMinionAction extends AbstractGameAction {
	public RetargetMinionAction() {
		duration = startDuration = Settings.ACTION_DUR_FAST;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			boolean redirect = false;
			ArrayList<AbstractMonster> minionList = BetterFriendlyMinions.getMinionList();
			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				AbstractFriendlyMonster target = MonsterHelper.getTarget(m);

				if (target != null && (target.isDead || !minionList.contains(target)) && !minionList.isEmpty()) {
					float chance = CopycatMinionPatch.calcMinionTargetChance();
					if (BetterFriendlyMinions.enemyTargetRng.randomBoolean(chance)) {
						BetterFriendlyMinions.switchTarget(m, (AbstractFriendlyMonster) minionList.get(BetterFriendlyMinions.enemyTargetRng.random(0, minionList.size() - 1)));
						redirect = true;
					}
				}
			}
			if (!redirect) {
				isDone = true;
				return;
			}
		}
		tickDuration();
	}
}
