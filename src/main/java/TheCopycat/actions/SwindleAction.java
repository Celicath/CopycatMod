package TheCopycat.actions;

import TheCopycat.cards.monster.Scammed;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.MonsterHelper;

public class SwindleAction extends AbstractGameAction {
	private AbstractMonster targetMonster;
	public static EnemyMoveInfo scammedMove = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.ATTACK_DEFEND, 10, 1, false);

	public SwindleAction(AbstractMonster m) {
		actionType = ActionType.WAIT;
		duration = startDuration = Settings.ACTION_DUR_FAST;
		targetMonster = m;
	}

	@Override
	public void update() {
		if (duration == startDuration && targetMonster != null && targetMonster.getIntentBaseDmg() < 0) {
			targetMonster.moveName = Scammed.NAME;
			if (MonsterHelper.getTarget(targetMonster) != null) {
				scammedMove.intent = MonsterIntentEnum.ATTACK_MINION_DEFEND;
			} else {
				scammedMove.intent = AbstractMonster.Intent.ATTACK_DEFEND;
			}
			ReflectionHacks.setPrivate(targetMonster, AbstractMonster.class, "move", scammedMove);
			byte nextMove = targetMonster.nextMove;
			targetMonster.createIntent();
			targetMonster.nextMove = nextMove;
		}
		tickDuration();
	}
}
