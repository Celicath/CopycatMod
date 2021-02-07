package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.DoAreaAction;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MetallicizePower;

import java.util.HashMap;

public class AreaMetallicize extends CustomCard {
	private static final String RAW_ID = "AreaMetallicize";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.POWER;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 3;
	private static final int UPGRADE_BONUS = 1;

	public HashMap<AbstractCreature, Integer> damageMap = new HashMap<>();

	public AreaMetallicize() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DoAreaAction(c -> new ApplyPowerAction(c, c, new MetallicizePower(c, magicNumber), magicNumber)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new AreaMetallicize();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}