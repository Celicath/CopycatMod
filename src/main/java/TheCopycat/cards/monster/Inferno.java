package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.DamageAllEnemiesAction;
import com.megacrit.cardcrawl.actions.unique.ApotheosisAction;
import com.megacrit.cardcrawl.actions.unique.BurnIncreaseAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Hexaghost;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.vfx.combat.ScreenOnFireEffect;

public class Inferno extends AbstractMonsterCard {
	private static final String RAW_ID = "Inferno";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 3;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

	private static final int DAMAGE = 2;
	private static final int UPGRADE_BONUS = 1;
	private static final int ASC4_BONUS = 1;
	private static final int HITS = 6;
	private static final int POISON = 6;

	public Inferno() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Hexaghost.ID, 6);
		baseDamage = DAMAGE;
		if (AbstractDungeon.ascensionLevel >= 4) {
			baseDamage += ASC4_BONUS;
		}
		baseMagicNumber = magicNumber = POISON;
		isMultiDamage = true;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new VFXAction(p, new ScreenOnFireEffect(), 1.0F));

		for (int i = 0; i < HITS; ++i) {
			addToBot(new DamageAllEnemiesAction(p, multiDamage, damageTypeForTurn, AbstractGameAction.AttackEffect.FIRE));
		}
		addToBot(new WaitAction(0.5f));
		for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
			addToBot(new ApplyPowerAction(mo, p, new PoisonPower(mo, p, magicNumber), magicNumber, false, AbstractGameAction.AttackEffect.NONE));
		}

		addToBot(new ApotheosisAction());
	}

	@Override
	public AbstractCard makeCopy() {
		return new Inferno();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(isAlly ? AbstractMonster.Intent.ATTACK : AbstractMonster.Intent.ATTACK_DEBUFF, baseDamage, HITS, true, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		addToBot(new VFXAction(owner, new ScreenOnFireEffect(), 1.0F));

		for (int i = 0; i < HITS; ++i) {
			addToBot(new DamageAction(target, new DamageInfo(owner, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
		}
		addToBot(new ApplyPowerAction(target, owner, new PoisonPower(target, owner, magicNumber), magicNumber, false, AbstractGameAction.AttackEffect.NONE));
		if (!isAlly) {
			addToBot(new BurnIncreaseAction());
		}
		if (owner instanceof Hexaghost) {
			addToBot(new ChangeStateAction(owner, "Deactivate"));
		}
	}
}
