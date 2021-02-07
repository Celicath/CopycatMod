package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.unique.PoisonLoseHpAction;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;

public class EnemyHexPower extends AbstractPower {
	public static final String RAW_ID = "EnemyHexPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

	AbstractMonster m;

	public EnemyHexPower(AbstractMonster m, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = m;
		this.m = m;
		this.amount = amount;
		this.updateDescription();
		this.loadRegion("hex");
		this.type = PowerType.DEBUFF;
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
	}

	@Override
	public void atEndOfTurn(boolean isPlayer) {
		if (m.getIntentBaseDmg() < 0) {
			flash();
			addToBot(new PoisonLoseHpAction(owner, owner, amount, AbstractGameAction.AttackEffect.POISON));
		}
	}
}
