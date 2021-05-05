package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.BookOfStabbing;

public class MultiStabbing extends AbstractMonsterCard {
	private static final String RAW_ID = "MultiStabbing";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.SPECIAL;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 6;
	private static final int UPGRADE_BONUS = 3;
	private static final int ASC3_BONUS = 1;

	public MultiStabbing() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, BookOfStabbing.ID, 1);
		baseDamage = POWER;
		if (AbstractDungeon.ascensionLevel >= 3) {
			baseDamage += ASC3_BONUS;
		}
		baseMagicNumber = magicNumber = 1;
		isInnate = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		for (int i = 0; i < magicNumber; i++) {
			addToBot(new SFXAction("MONSTER_BOOK_STAB_" + MathUtils.random(0, 3)));
			addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.SLASH_VERTICAL, false, true));
		}
		upgradeMagicNumber(1);
		applyPowers();
	}

	@Override
	public AbstractCard makeCopy() {
		return new MultiStabbing();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}
}
