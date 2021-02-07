package TheCopycat.interfaces;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

public interface AreaAction {
	AbstractGameAction action(AbstractCreature creature);
}
