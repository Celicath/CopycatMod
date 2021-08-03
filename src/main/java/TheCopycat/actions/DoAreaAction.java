package TheCopycat.actions;

import TheCopycat.interfaces.AreaAction;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.core.AbstractCreature;

import java.util.ArrayList;
import java.util.Collections;

public class DoAreaAction extends AbstractGameAction {
	AreaAction action;

	public DoAreaAction(AreaAction action) {
		this.action = action;
	}

	public void update() {
		ArrayList<AbstractCreature> group = BetterFriendlyMinionsUtils.getAllyList();
		Collections.reverse(group);

		for (AbstractCreature c : group) {
			if (c != null) {
				addToTop(action.action(c));
			}
		}
		isDone = true;
	}
}
