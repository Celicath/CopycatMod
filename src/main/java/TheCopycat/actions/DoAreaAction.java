package TheCopycat.actions;

import TheCopycat.CopycatModMain;
import TheCopycat.crossovers.DTModCrossover;
import TheCopycat.interfaces.AreaAction;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;

import java.util.ArrayList;

public class DoAreaAction extends AbstractGameAction {
	AreaAction action;

	public DoAreaAction(AreaAction action) {
		this.action = action;
	}

	public void update() {
		ArrayList<AbstractMonster> group = (AbstractDungeon.player instanceof AbstractPlayerWithMinions) ?
				((AbstractPlayerWithMinions) AbstractDungeon.player).minions.monsters :
				PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters;

		for (AbstractMonster mo : group) {
			if (mo != null) {
				addToTop(action.action(mo));
			}
		}

		if (CopycatModMain.isDragonTamerLoaded) {
			AbstractPlayer d = DTModCrossover.getLivingDragon();
			if (d != null) {
				addToTop(action.action(d));
			}
		}

		addToTop(action.action(AbstractDungeon.player));
		isDone = true;
	}
}
