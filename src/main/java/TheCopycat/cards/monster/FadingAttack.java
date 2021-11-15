package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.Transient;
import com.megacrit.cardcrawl.vfx.combat.WeightyImpactEffect;

public class FadingAttack extends AbstractMonsterCard {
	private static final String RAW_ID = "FadingAttack";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 3;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 10;
	private static final int UPGRADE_BONUS = 10;
	private static final int TURN_MULTIPLIER = 10;

	public FadingAttack() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Transient.ID, 1);
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void applyPowers() {
		baseDamage = magicNumber + TURN_MULTIPLIER * GameActionManager.turn;
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
		baseDamage = magicNumber + TURN_MULTIPLIER * GameActionManager.turn;
		super.calculateCardDamage(mo);
		rawDescription = DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (m != null) {
			this.addToBot(new VFXAction(new WeightyImpactEffect(m.hb.cX, m.hb.cY)));
		}
		addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
	}

	@Override
	public AbstractCard makeCopy() {
		return new FadingAttack();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}

	@Override
	public void monsterTurnApplyPowers(AbstractMonster owner, AbstractCreature target) {
		baseDamage = magicNumber + TURN_MULTIPLIER * GameActionManager.turn;
		super.monsterTurnApplyPowers(owner, target);
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(AbstractMonster.Intent.ATTACK, baseDamage, 0, false, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		addToBot(new DamageAction(target, new DamageInfo(owner, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
	}
}
