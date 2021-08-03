package TheCopycat.actions;

import TheCopycat.powers.VenomologyPower;
import basemod.Pair;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.PoisonPower;

import java.util.ArrayList;
import java.util.ListIterator;

public class VenomologyActivateAction extends AbstractGameAction {
	public static VenomologyActivateAction lastAction = null;
	public static ArrayList<Pair<AbstractCreature, PoisonPower>> poisonPowers = new ArrayList<>();

	public VenomologyActivateAction(AbstractCreature target, AbstractCreature source, int amount) {
		poisonPowers.add(new Pair<>(source, new PoisonPower(target, source, amount)));
	}

	@Override
	public void update() {
		if (lastAction == this) {
			VenomologyPower.disabled = true;
			for (ListIterator<Pair<AbstractCreature, PoisonPower>> it = poisonPowers.listIterator(poisonPowers.size()); it.hasPrevious(); ) {
				Pair<AbstractCreature, PoisonPower> pair = it.previous();
				PoisonPower pp = pair.getValue();
				addToTop(new ApplyPowerAction(pp.owner, pair.getKey(), pp));
			}
			poisonPowers.clear();
			addToBot(new SuperDelayAction(() -> VenomologyPower.disabled = false));
		}
		isDone = true;
	}
}
