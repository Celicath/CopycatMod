package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.actions.common.RemoveSpecificPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class TurnFlightPower extends AbstractPower {
	public static final String RAW_ID = "TurnFlightPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

	public TurnFlightPower(AbstractCreature owner, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = owner;
		this.amount = amount;
		this.updateDescription();
		this.type = PowerType.BUFF;
		this.isTurnBased = true;
		this.loadRegion("flight");
		this.priority = 50;
	}

	@Override
	public void updateDescription() {
		if (amount == 1) {
			description = DESCRIPTIONS[0];
		} else {
			description = DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
		}
	}

	@Override
	public void atEndOfRound() {
		flash();
		if (amount == 0) {
			addToBot(new RemoveSpecificPowerAction(owner, owner, this));
		} else {
			addToBot(new ReducePowerAction(owner, owner, this, 1));// 51
		}
	}

	@Override
	public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
		return type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS ? damage / 2.0F : damage;
	}
}
