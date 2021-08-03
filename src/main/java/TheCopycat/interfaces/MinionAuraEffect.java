package TheCopycat.interfaces;

import com.megacrit.cardcrawl.cards.AbstractCard;

public interface MinionAuraEffect {
	default float atDamageModify(float damage, AbstractCard c) {
		return damage;
	}
}
