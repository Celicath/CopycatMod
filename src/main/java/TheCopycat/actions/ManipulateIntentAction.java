package TheCopycat.actions;

import TheCopycat.cards.monster.AbstractMonsterCard;
import TheCopycat.utils.MonsterCardMoveInfo;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.enums.MonsterIntentEnum;
import kobting.friendlyminions.helpers.MonsterHelper;

public class ManipulateIntentAction extends AbstractGameAction {
	private final AbstractMonster targetMonster;
	private AbstractMonsterCard monsterCard;
	boolean createCopy;

	public ManipulateIntentAction(AbstractMonster m, AbstractMonsterCard monsterCard, boolean createCopy) {
		actionType = ActionType.WAIT;
		duration = startDuration = Settings.ACTION_DUR_FAST;
		targetMonster = m;
		this.monsterCard = monsterCard;
		this.createCopy = createCopy;
	}

	@Override
	public void update() {
		if (duration == startDuration && targetMonster != null) {
			if (createCopy) {
				monsterCard = ((AbstractMonsterCard) (monsterCard.makeStatEquivalentCopy()));
			}
			MonsterCardMoveInfo move = monsterCard.createMoveInfo(false);
			targetMonster.moveName = monsterCard.name;
			if (MonsterHelper.getTarget(targetMonster) != null) {
				switch (move.intent) {
					case ATTACK:
						move.intent = MonsterIntentEnum.ATTACK_MINION;
						break;
					case ATTACK_BUFF:
						move.intent = MonsterIntentEnum.ATTACK_MINION_BUFF;
						break;
					case ATTACK_DEBUFF:
						move.intent = MonsterIntentEnum.ATTACK_MINION_DEBUFF;
						break;
					case ATTACK_DEFEND:
						move.intent = MonsterIntentEnum.ATTACK_MINION_DEFEND;
						break;
				}
			}
			ReflectionHacks.setPrivate(targetMonster, AbstractMonster.class, "move", move);
			byte nextMove = targetMonster.nextMove;
			targetMonster.createIntent();
			targetMonster.nextMove = nextMove;
		}
		tickDuration();
	}
}
