package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;

public class Haste2 extends AbstractMonsterCard {
	private static final String RAW_ID = "Haste2";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 3;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 10;
	private static final int UPGRADE_BONUS = 5;
	private static final int BLOCK = 32;

	public Haste2() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, TimeEater.ID, 5);
		baseMagicNumber = magicNumber = POWER;
		baseBlock = BLOCK;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new TalkAction(true, TimeEater.DIALOG[1], 0.5F, 2.0F));
		addToBot(new HealAction(p, p, p.maxHealth * magicNumber / 100));
		addToBot(new GainBlockAction(p, p, block));
		addToBot(new RemoveDebuffsAction(p));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Haste2();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
