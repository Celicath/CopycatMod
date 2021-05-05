package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.DoAreaAction;
import TheCopycat.crossovers.BetterFriendlyMinions;
import TheCopycat.crossovers.DTModCrossover;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.stances.AbstractStance;
import com.megacrit.cardcrawl.stances.NeutralStance;

import java.util.ArrayList;
import java.util.HashMap;

public class BeatUp extends CustomCard implements HoverMonsterCard {
	private static final String RAW_ID = "BeatUp";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	public HashMap<AbstractCreature, Integer> damageMap = new HashMap<>();
	static NeutralStance neutralStance = new NeutralStance();

	public BeatUp() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
		baseDamage = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DoAreaAction(c -> new DamageAction(m, new DamageInfo(c, damageMap.get(c), damageTypeForTurn), getRandomEffect()) {
			@Override
			public void update() {
				if (p != c && duration == Settings.ACTION_DUR_XFAST) {
					c.useFastAttackAnimation();
				}
				super.update();
				if (isDone) {
					onUnhoverMonster();
				}
			}
		}));
	}

	private AbstractGameAction.AttackEffect getRandomEffect() {
		AbstractGameAction.AttackEffect[] effects = AbstractGameAction.AttackEffect.values();
		int r = MathUtils.random(0, 8);
		return effects[r >= 7 ? r + 1 : r];
	}

	@Override
	public AbstractCard makeCopy() {
		return new BeatUp();
	}

	@Override
	public void calculateCardDamage(AbstractMonster mo) {
		damageMap.clear();
		super.calculateCardDamage(mo);
		damageMap.put(AbstractDungeon.player, damage);

		int totalDamage = damage;
		ArrayList<AbstractMonster> monsters = BetterFriendlyMinions.getMinionList();

		if (monsters != null) {
			for (AbstractMonster m : monsters) {
				if (m != null) {
					int d = calculateDamageAsOthers(m, mo);
					totalDamage += d;
					damageMap.put(m, d);
				}
			}
		}

		if (CopycatModMain.isDragonTamerLoaded) {
			AbstractPlayer p = DTModCrossover.getLivingDragon();
			if (p != null) {
				int d = calculateDamageAsOthers(p, mo);
				totalDamage += d;
				damageMap.put(p, d);
			}
		}

		rawDescription = DESCRIPTION + EXTENDED_DESCRIPTION[0] + totalDamage + EXTENDED_DESCRIPTION[1];
		initializeDescription();
	}

	int calculateDamageAsOthers(AbstractCreature source, AbstractMonster target) {
		ArrayList<AbstractPower> powers = AbstractDungeon.player.powers;
		AbstractStance stance = AbstractDungeon.player.stance;
		AbstractDungeon.player.powers = source.powers;
		AbstractDungeon.player.stance = source instanceof AbstractPlayer ? ((AbstractPlayer) source).stance : neutralStance;
		super.calculateCardDamage(target);
		AbstractDungeon.player.powers = powers;
		AbstractDungeon.player.stance = stance;
		return damage;
	}

	@Override
	public void onUnhoverMonster() {
		rawDescription = DESCRIPTION;
		initializeDescription();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
