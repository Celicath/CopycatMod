package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Champ;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.vfx.combat.InflameEffect;

public class ChampsAnger extends AbstractMonsterCard {
	private static final String RAW_ID = "ChampsAnger";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 6;
	private static final int UPGRADE_BONUS = 2;

	public ChampsAnger() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Champ.ID, 7);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new TalkAction(true, Champ.DIALOG[MathUtils.random(4, 5)], 1.5F, 2.5F));
		addToBot(new VFXAction(p, new InflameEffect(p), 0.25F));
		addToBot(new VFXAction(p, new InflameEffect(p), 0.25F));
		addToBot(new VFXAction(p, new InflameEffect(p), 0.25F));
		addToBot(new RemoveDebuffsAction(p));
		addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new ChampsAnger();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
