package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
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
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 25;
	private static final int UPGRADE_BONUS = 5;
	private static final int ASC2_BONUS = 5;
	private static final int ASC2_UPGRADE_BONUS = 7;

	public UltimateBlast() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, GremlinWizard.ID, 1);
		baseDamage = POWER;
		if (AbstractDungeon.ascensionLevel >= 2) {
			baseDamage += ASC2_BONUS;
			cost = costForTurn = 3;
		}
		isEthereal = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
	}

	@Override
	public AbstractCard makeCopy() {
		return new UltimateBlast();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS + (AbstractDungeon.ascensionLevel >= 2 ? ASC2_UPGRADE_BONUS : 0));
		}
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(AbstractMonster.Intent.ATTACK, baseDamage, 0, false, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		addToBot(new DamageAction(target, new DamageInfo(owner, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.FIRE));
	}
}
