package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.powers.CopycatFormPower;
import basemod.abstracts.CustomCard;
import basemod.helpers.BaseModCardTags;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class CopycatForm extends CustomCard {
	private static final String RAW_ID = "CopycatForm";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 3;
	private static final CardType TYPE = CardType.POWER;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 4;
	private static final int UPGRADE_BONUS = 2;

	public CopycatForm() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
		tags.add(BaseModCardTags.FORM);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new ApplyPowerAction(p, p, new CopycatFormPower(p, magicNumber)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new CopycatForm();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}