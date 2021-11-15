package TheCopycat.utils;

import TheCopycat.cards.monster.AbstractMonsterCard;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

public class MonsterCardMoveInfo extends EnemyMoveInfo {
	public AbstractMonsterCard attachedCard;

	public MonsterCardMoveInfo(AbstractMonster.Intent intent, int intentBaseDmg, int multiplier, boolean isMultiDamage, AbstractMonsterCard card) {
		super((byte) -1, intent, intentBaseDmg, multiplier, isMultiDamage);
		attachedCard = card;
	}

	public MonsterCardMoveInfo(AbstractMonster.Intent intent, AbstractMonsterCard card) {
		this(intent, -1, 0, false, card);
	}
}
