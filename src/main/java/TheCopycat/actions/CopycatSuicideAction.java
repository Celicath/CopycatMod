package TheCopycat.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CopycatSuicideAction extends AbstractGameAction {
	private final AbstractMonster m;

	public CopycatSuicideAction(AbstractMonster target) {
		this.duration = 0.0F;
		this.actionType = ActionType.DAMAGE;
		this.m = target;
	}

	public void update() {
		m.gold = 0;
		m.currentHealth = 0;
		m.die();
		m.healthBarUpdatedEvent();

		isDone = true;
	}
}
