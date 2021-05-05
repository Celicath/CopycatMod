package TheCopycat.interfaces;

import com.megacrit.cardcrawl.monsters.AbstractMonster;

public interface HoverMonsterCard {
	default void onHoverMonster(AbstractMonster m) {
	}

	default void onUnhoverMonster() {
	}
}
