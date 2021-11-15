package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.CopycatSuicideAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class HallucinationPower extends AbstractPower {
	public static final String RAW_ID = "HallucinationPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

	public HallucinationPower(AbstractMonster owner, int amount) {
		this.name = powerStrings.NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.loadRegion("int");
	}

	public void updateDescription() {
		if (amount == 1) {
			description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
		} else {
			description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[2];
		}
	}

	public void atStartOfTurn() {
		addToBot(new ReducePowerAction(owner, owner, this, 1));
		if (amount == 1) {
			addToBot(new CopycatSuicideAction((AbstractMonster) owner));
		}
	}
}
