package TheCopycat.powers;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.OnDiscardPower;
import TheCopycat.interfaces.OnEvokePower;
import TheCopycat.vfx.TextEffect;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.stances.AbstractStance;

public class CopycatFormPower extends AbstractPower implements OnDiscardPower, OnEvokePower {
	public static final String RAW_ID = "CopycatFormPower";
	public static final String POWER_ID = CopycatModMain.makeID(RAW_ID);
	private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
	public static final String NAME = powerStrings.NAME;
	public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;

	public CopycatFormPower(AbstractPlayer p, int amount) {
		this.name = NAME;
		this.ID = POWER_ID;
		this.owner = p;
		this.amount = amount;
		this.updateDescription();
		this.loadRegion("infinitegreen");
		this.type = PowerType.BUFF;
	}

	@Override
	public void updateDescription() {
		description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1] + amount + DESCRIPTIONS[2];
	}

	void activate(int messageIndex, AbstractGameAction.AttackEffect effect) {
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				flash();
				isDone = true;
			}
		});
		if (!TextEffect.messageIndexSet.contains(messageIndex)) {
			addToBot(new VFXAction(new TextEffect(owner.hb.cX, owner.hb.cY, messageIndex, DESCRIPTIONS[messageIndex])));
		}
		addToBot(new GainBlockAction(owner, amount, Settings.FAST_MODE));
		addToBot(new DamageAllEnemiesAction(null, DamageInfo.createDamageMatrix(amount, true), DamageInfo.DamageType.THORNS, effect));
	}

	@Override
	public void onDiscard() {
		activate(3, AbstractGameAction.AttackEffect.SLASH_HORIZONTAL);
	}

	@Override
	public void onApplyPower(AbstractPower power, AbstractCreature target, AbstractCreature source) {
		if (source == owner && power instanceof PoisonPower && !target.hasPower(ArtifactPower.POWER_ID)) {
			activate(4, AbstractGameAction.AttackEffect.POISON);
		}
	}

	@Override
	public void onChannel(AbstractOrb orb) {
		activate(5, AbstractGameAction.AttackEffect.LIGHTNING);
	}

	@Override
	public void onEvoke() {
		activate(5, AbstractGameAction.AttackEffect.LIGHTNING);
	}

	@Override
	public void onScry() {
		activate(6, AbstractGameAction.AttackEffect.BLUNT_LIGHT);
	}

	@Override
	public void onChangeStance(AbstractStance oldStance, AbstractStance newStance) {
		activate(7, AbstractGameAction.AttackEffect.SLASH_VERTICAL);
	}
}
