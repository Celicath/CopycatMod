package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.Maw;

public class MultiStabbing2 extends AbstractMonsterCard {
	private static final String RAW_ID = "MultiStabbing2";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 2;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 2;

	public MultiStabbing2() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Maw.ID, 5);
		baseDamage = POWER;
		isInnate = true;
	}

	@Override
	public void applyPowers() {
		baseMagicNumber = magicNumber = GameActionManager.turn + 1;
		super.applyPowers();
		rawDescription = DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public void onMoveToDiscard() {
		rawDescription = DESCRIPTION;
		initializeDescription();
	}

	@Override
	public void calculateCardDamage(AbstractMonster mo) {
		baseMagicNumber = magicNumber = GameActionManager.turn + 1;
		super.calculateCardDamage(mo);
		rawDescription = DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		for (int i = 0; i < magicNumber; i++) {
			addToBot(new SFXAction("MONSTER_BOOK_STAB_" + MathUtils.random(0, 3)));
			addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL, false, true));
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new MultiStabbing2();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}
}
