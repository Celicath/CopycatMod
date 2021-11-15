package TheCopycat.utils;

import com.megacrit.cardcrawl.monsters.AbstractMonster;
import kobting.friendlyminions.enums.MonsterIntentEnum;

import static com.megacrit.cardcrawl.monsters.AbstractMonster.Intent.*;

public class GameLogicUtils {
	/**
	 * idSpparator used in special Metric ID cards.
	 */
	public static String metricIdSeparator = "_#_";

	/**
	 * Check if an enemy's intent matches intentMode.
	 *
	 * @param m          Monster to check
	 * @param intentMode 0 = Block, 1 = Attack, 2 = Buff, 3 = Debuff
	 */
	public static boolean checkIntent(AbstractMonster m, int intentMode) {
		if (m == null) {
			return true;
		}
		switch (intentMode) {
			case 0:
				return m.intent == ATTACK_DEFEND || m.intent == DEFEND || m.intent == DEFEND_DEBUFF || m.intent == DEFEND_BUFF || m.intent == MonsterIntentEnum.ATTACK_MINION_DEFEND;
			case 1:
				return m.intent == ATTACK || m.intent == ATTACK_BUFF || m.intent == ATTACK_DEBUFF || m.intent == ATTACK_DEFEND || m.getIntentBaseDmg() >= 0 ||
					m.intent == MonsterIntentEnum.ATTACK_MINION ||
					m.intent == MonsterIntentEnum.ATTACK_MINION_BUFF ||
					m.intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF ||
					m.intent == MonsterIntentEnum.ATTACK_MINION_DEFEND;
			case 2:
				return m.intent == ATTACK_BUFF || m.intent == BUFF || m.intent == DEFEND_BUFF || m.intent == MAGIC || m.intent == MonsterIntentEnum.ATTACK_MINION_BUFF;
			case 3:
				return m.intent == ATTACK_DEBUFF || m.intent == DEBUFF || m.intent == STRONG_DEBUFF || m.intent == DEFEND_DEBUFF || m.intent == MAGIC || m.intent == MonsterIntentEnum.ATTACK_MINION_DEBUFF;
			default:
				return true;
		}
	}
}
