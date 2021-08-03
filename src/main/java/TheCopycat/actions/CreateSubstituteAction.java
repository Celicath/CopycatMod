package TheCopycat.actions;

import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.helpers.MonsterHelper;

public class CreateSubstituteAction extends AbstractGameAction {
	public CreateSubstituteAction(int hp) {
		this.amount = hp;
	}

	public void update() {
		SubstituteMinion.instance.summon(amount, amount);
		for (AbstractMonster m : AbstractDungeon.getCurrRoom().monsters.monsters) {
			if (!m.isDeadOrEscaped() && MonsterHelper.getTarget(m) == null) {
				BetterFriendlyMinionsUtils.switchTarget(m, SubstituteMinion.instance, false);
			}
		}
		isDone = true;
	}
}
