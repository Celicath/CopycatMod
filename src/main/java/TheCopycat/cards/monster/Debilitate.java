package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.ending.CorruptHeart;
import com.megacrit.cardcrawl.powers.*;

public class Debilitate extends AbstractMonsterCard {
	private static final String RAW_ID = "Debilitate";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 2;
	private static final int UPGRADE_BONUS = 1;

	public Debilitate() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, CorruptHeart.ID, 3);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new ApplyPowerAction(p, p, new StrengthPower(p, magicNumber), magicNumber));
		addToBot(new ApplyPowerAction(p, p, new DexterityPower(p, magicNumber), magicNumber));
		addToBot(new ApplyPowerAction(m, p, new PoisonPower(m, p, magicNumber)));
		addToBot(new ApplyPowerAction(m, p, new VulnerablePower(m, magicNumber, false)));
		addToBot(new ApplyPowerAction(m, p, new WeakPower(m, magicNumber, false)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Debilitate();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
