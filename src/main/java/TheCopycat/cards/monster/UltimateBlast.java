package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.GremlinWizard;

public class UltimateBlast extends AbstractMonsterCard {
	private static final String RAW_ID = "UltimateBlast";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 25;
	private static final int UPGRADE_BONUS = 5;
	private static final int ASC2_BONUS = 5;

	public UltimateBlast() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, GremlinWizard.ID, 1);
		baseDamage = POWER;
		if (AbstractDungeon.ascensionLevel >= 2) {
			baseDamage += ASC2_BONUS;
		}
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
	}

	public void unExhaust() {
		exhaust = false;
		rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public AbstractCard makeStatEquivalentCopy() {
		UltimateBlast c = (UltimateBlast) super.makeStatEquivalentCopy();
		if (!exhaust) {
			c.unExhaust();
		}
		return c;
	}

	@Override
	public AbstractCard makeCopy() {
		return new UltimateBlast();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}
}
