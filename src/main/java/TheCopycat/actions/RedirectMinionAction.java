package TheCopycat.actions;

import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.monsters.AbstractFriendlyMonster;

import java.util.ArrayList;

public class RedirectMinionAction extends AbstractGameAction {
	public RedirectMinionAction(AbstractCreature target) {
		duration = startDuration = Settings.ACTION_DUR_FAST;
		this.target = target;
	}

	@Override
	public void update() {
		if (duration == startDuration) {
			ArrayList<AbstractMonster> minionList = BetterFriendlyMinionsUtils.getMinionList();
			if (minionList.isEmpty()) {
				isDone = true;
			} else {
				for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
					if (m.isDeadOrEscaped()) continue;
					if (target instanceof AbstractPlayer) {
						// TODO: do something for Dragon
						BetterFriendlyMinionsUtils.switchTarget(m, null, false);
					} else if (target instanceof AbstractFriendlyMonster && !target.isDead) {
						BetterFriendlyMinionsUtils.switchTarget(m, (AbstractFriendlyMonster) target, false);
					}
				}
			}
		}
		tickDuration();
	}
}
