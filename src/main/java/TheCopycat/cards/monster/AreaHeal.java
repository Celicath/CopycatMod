package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.DoAreaAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Healer;

public class AreaHeal extends AbstractMonsterCard {
	private static final String RAW_ID = "AreaHeal";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int HEAL = 8;
	private static final int UPGRADE_BONUS = 3;

	public AreaHeal() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Healer.ID, 2);
		baseMagicNumber = magicNumber = HEAL;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DoAreaAction(c -> new HealAction(c, c, magicNumber, Settings.ACTION_DUR_XFAST)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new AreaHeal();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
