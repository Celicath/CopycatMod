package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInDrawPileAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.GremlinWizard;

public class Charging extends AbstractMonsterCard {
	private static final String RAW_ID = "Charging";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 0;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.SELF;

	public static int useCount = 0;

	public static UltimateBlast previewCard = new UltimateBlast();
	public static UltimateBlast previewCardPlus = new UltimateBlast() {
		{
			this.upgrade();
		}
	};

	public Charging() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, GremlinWizard.ID, 2);
		cardsToPreview = previewCard;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		useCount++;
		if (useCount == 1) {
			addToBot(new TalkAction(true, GremlinWizard.DIALOG[1], 1.0F, 3.0F));
		} else {
			addToBot(new TalkAction(true, GremlinWizard.DIALOG[2], 1.0F, 3.0F));
			UltimateBlast c = (UltimateBlast) cardsToPreview.makeCopy();
			c.cost = 0;
			c.costForTurn = 0;
			c.isCostModified = true;
			c.unExhaust();
			addToBot(new MakeTempCardInDrawPileAction(c, 1, false, true));
			useCount = 0;
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new Charging();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			cardsToPreview = previewCardPlus;
			rawDescription = cardStrings.UPGRADE_DESCRIPTION;
			initializeDescription();
		}
	}
}
