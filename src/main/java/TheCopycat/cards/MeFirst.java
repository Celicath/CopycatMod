package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.MeFirstAction;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MeFirst extends CustomCard {
	private static final String RAW_ID = "MeFirst";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int DAMAGE = 9;
	private static final int UPGRADE_DAMAGE = 3;
	private static final int BLOCK = 9;
	private static final int UPGRADE_BLOCK = 3;
	private static final int MAGIC = 3;
	private static final int UPGRADE_MAGIC = 1;

	public MeFirst() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseDamage = DAMAGE;
		baseBlock = BLOCK;
		baseMagicNumber = magicNumber = MAGIC;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new MeFirstAction(m, new DamageInfo(p, damage, damageTypeForTurn), block, magicNumber));
	}

	@Override
	public AbstractCard makeCopy() {
		return new MeFirst();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_DAMAGE);
			upgradeBlock(UPGRADE_BLOCK);
			upgradeMagicNumber(UPGRADE_MAGIC);
		}
	}
}
