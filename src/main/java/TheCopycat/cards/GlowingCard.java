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
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = -2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 6;
	private static final int UPGRADE_BONUS = 2;

	public GlowingCard() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
	}

	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
		return false;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
	}

	public void activateInCombat() {
		if (AbstractDungeon.player != null) {
			flash();
			addToTop(new GainBlockAction(AbstractDungeon.player, magicNumber));
		}
	}

	public void activateOutOfCombat() {
		if (AbstractDungeon.player != null) {
			flash();
			AbstractDungeon.player.heal(magicNumber, true);
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new GlowingCard();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
