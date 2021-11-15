package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class GlowingCard extends CustomCard {
	private static final String RAW_ID = "GlowingCard";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	public GlowingCard() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseBlock = POWER;
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new GainBlockAction(p, block));
	}

	public void activateInCombat() {
		if (AbstractDungeon.player != null) {
			flash();
			addToTop(new GainBlockAction(AbstractDungeon.player, magicNumber));
		}
	}

	public void activateOutOfCombat() {
	}

	@Override
	public AbstractCard makeCopy() {
		return new GlowingCard();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeBlock(UPGRADE_BONUS);
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
