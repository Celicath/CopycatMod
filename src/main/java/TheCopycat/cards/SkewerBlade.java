package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SkewerBladeAction;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

import java.util.ArrayList;

public class SkewerBlade extends CustomCard {
	private static final String RAW_ID = "SkewerBlade";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = -1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	public SkewerBlade() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseDamage = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (cost == -1) {
			addToBot(new SkewerBladeAction(this, p, m, damage, damageTypeForTurn, freeToPlayOnce, energyOnUse));
		} else {
			for (int i = 0; i < this.magicNumber; ++i) {
				this.addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
			}
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new SkewerBlade();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}

	public void fixEffect(int effectCount) {
		cost = costForTurn = 1;
		baseMagicNumber = magicNumber = effectCount;
		rawDescription = baseMagicNumber == 1 ? EXTENDED_DESCRIPTION[1] : EXTENDED_DESCRIPTION[0];
		this.initializeDescription();
		initializeDescription();
		flash();
	}

	public AbstractCard makeStatEquivalentCopy() {
		AbstractCard card = super.makeStatEquivalentCopy();
		card.magicNumber = magicNumber;
		card.description = new ArrayList<>(description);
		return card;
	}
}
