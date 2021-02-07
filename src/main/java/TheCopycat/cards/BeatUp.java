package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.DoAreaAction;
import TheCopycat.crossovers.DTModCrossover;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
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
import kobting.friendlyminions.characters.AbstractPlayerWithMinions;
import kobting.friendlyminions.patches.PlayerAddFieldsPatch;

import java.util.ArrayList;
import java.util.HashMap;

public class BeatUp extends CustomCard {
	private static final String RAW_ID = "BeatUp";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 7;
	private static final int UPGRADE_BONUS = 3;

	public HashMap<AbstractCreature, Integer> damageMap = new HashMap<>();

	public BeatUp() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
		baseDamage = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DoAreaAction(c -> new DamageAction(m, new DamageInfo(c, damageMap.get(c), damageTypeForTurn)) {
			@Override
			public void update() {
				if (p != c && duration == Settings.ACTION_DUR_XFAST) {
					c.useFastAttackAnimation();
				}
				super.update();
			}
		}));
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

		ArrayList<AbstractMonster> monsters = (AbstractDungeon.player instanceof AbstractPlayerWithMinions) ?
				((AbstractPlayerWithMinions) AbstractDungeon.player).minions.monsters :
				PlayerAddFieldsPatch.f_minions.get(AbstractDungeon.player).monsters;

		if (monsters != null) {
			for (AbstractMonster m : monsters) {
				if (m != null) {
					damageMap.put(m, calculateDamageAsOthers(m, mo));
				}
			}
		}

		if (CopycatModMain.isDragonTamerLoaded) {
			AbstractPlayer p = DTModCrossover.getLivingDragon();
			if (p != null) {
				damageMap.put(p, calculateDamageAsOthers(p, mo));
			}
		}
	}

	int calculateDamageAsOthers(AbstractCreature source, AbstractMonster target) {
		ArrayList<AbstractPower> powers = AbstractDungeon.player.powers;
		AbstractDungeon.player.powers = source.powers;
		calculateCardDamage(target);
		AbstractDungeon.player.powers = powers;
		return damage;
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
		}
	}
}
