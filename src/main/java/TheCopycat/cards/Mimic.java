package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class Mimic extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "Mimic";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 0;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.BASIC;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	public Mimic() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		exhaust = true;

		tags.add(CharacterEnum.CustomTags.COPYCAT_MIMIC);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new MakeTempCardInHandAction(CopycatModMain.getEnemyLastMoveCard(m).makeCopy(), true));
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		CopycatModMain.getEnemyLastMoveCard(m).resetAttributes();
	}

	@Override
	public AbstractCard makeCopy() {
		return new Mimic();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			selfRetain = true;
			rawDescription = cardStrings.UPGRADE_DESCRIPTION;
			initializeDescription();
		}
	}
}
