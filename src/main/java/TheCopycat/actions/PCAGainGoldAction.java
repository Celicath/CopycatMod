package TheCopycat.actions;

import TheCopycat.interfaces.PostCombatActivateAction;
import com.megacrit.cardcrawl.actions.common.GainGoldAction;

public class PCAGainGoldAction extends GainGoldAction implements PostCombatActivateAction {
	public PCAGainGoldAction(int amount) {
		super(amount);
	}
}
