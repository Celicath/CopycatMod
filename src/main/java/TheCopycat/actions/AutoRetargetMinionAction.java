package TheCopycat.actions;

import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.patches.CopycatMinionPatch;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.helpers.MonsterHelper;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public class AutoRetargetMinionAction extends AbstractGameAction {
	public AutoRetargetMinionAction() {
		duration = startDuration = Settings.ACTION_DUR_FAST;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			boolean redirect = false;
			ArrayList<AbstractMonster> minionList = BetterFriendlyMinionsUtils.getMinionList();
			for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
				if (m.isDeadOrEscaped() || m.intentAlphaTarget == 0.0f) continue;

				AbstractFriendlyMonster target = MonsterHelper.getTarget(m);

				if (target != null && target.isDead) {
					if (target instanceof SubstituteMinion) {
						BetterFriendlyMinionsUtils.switchTarget(m, null, false);
					} else {
						if (!minionList.isEmpty()) {
							float chance = CopycatMinionPatch.calcMinionTargetChance();
							if (BetterFriendlyMinionsUtils.enemyTargetRng.randomBoolean(chance)) {
								BetterFriendlyMinionsUtils.switchTarget(m, (AbstractFriendlyMonster) minionList.get(BetterFriendlyMinionsUtils.enemyTargetRng.random(0, minionList.size() - 1)), false);
							} else {
								BetterFriendlyMinionsUtils.switchTarget(m, null, false);
							}
						} else {
							BetterFriendlyMinionsUtils.switchTarget(m, null, false);
						}
					}
					redirect = true;
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
