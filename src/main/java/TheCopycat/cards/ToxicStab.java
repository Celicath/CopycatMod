package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.BowlingFlaskAction;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.PoisonPower;

public class ToxicStab extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "ToxicStab";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 6;
	private static final int UPGRADE_BONUS = 2;
	private static final int POISON = 3;
	private static final int UPGRADE_POISON = 1;

	boolean reversed;

	public ToxicStab() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

		baseDamage = POWER;
		baseMagicNumber = magicNumber = POISON;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (m.hasPower(PoisonPower.POWER_ID)) {
			addToBot(new BowlingFlaskAction(m, damage, 1, p));
			addToBot(new DamageAction(m, new DamageInfo(p, magicNumber, damageTypeForTurn), AbstractGameAction.AttackEffect.POISON));
		} else {
			addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
			addToBot(new ApplyPowerAction(m, p, new PoisonPower(m, p, magicNumber), magicNumber));
		}
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		if (m.hasPower(PoisonPower.POWER_ID) && !reversed) {
			reversed = true;
			rawDescription = cardStrings.EXTENDED_DESCRIPTION[0];
			initializeDescription();
		}
	}

	@Override
	public void onUnhoverMonster() {
		if (reversed) {
			reversed = false;
			rawDescription = DESCRIPTION;
			initializeDescription();
		}
	}

	@Override
	public void onMoveToDiscard() {
		onUnhoverMonster();
	}

	@Override
	public AbstractCard makeCopy() {
		return new ToxicStab();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
			upgradeMagicNumber(UPGRADE_POISON);
		}
	}
}
