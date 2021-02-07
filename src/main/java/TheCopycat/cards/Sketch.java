package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.evacipated.cardcrawl.mod.stslib.StSLib;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

public class Sketch extends CustomCard {
	private static final String RAW_ID = "Sketch";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	public Sketch() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		exhaust = true;

		tags.add(CharacterEnum.CustomTags.COPYCAT_MIMIC);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new MakeTempCardInHandAction(CopycatModMain.getEnemyLastMoveCard(m).makeCopy(), true));
		AbstractCard thisCard = this;
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				AbstractCard card = StSLib.getMasterDeckEquivalent(thisCard);
				if (card != null) {
					AbstractDungeon.player.masterDeck.removeCard(card);
					AbstractDungeon.topLevelEffects.add(new ShowCardAndObtainEffect(CopycatModMain.getEnemyLastMoveCard(m).makeCopy(), Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));
				}
				isDone = true;
			}
		});
	}

	@Override
	public void calculateCardDamage(AbstractMonster mo) {
		super.calculateCardDamage(mo);
		CopycatModMain.getEnemyLastMoveCard(mo).resetAttributes();
	}

	@Override
	public AbstractCard makeCopy() {
		return new Sketch();
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
