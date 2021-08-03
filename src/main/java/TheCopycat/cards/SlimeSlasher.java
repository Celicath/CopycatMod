package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SlimeSlasherAction;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.utils.GameLogicUtils;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class SlimeSlasher extends CustomCard {
	private static final String RAW_ID = "SlimeSlasher";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ALL_ENEMY;

	private static final int POWER = 8;
	private static final int UPGRADE_BONUS = 4;
	public static final int FATAL_BONUS_PER = 2;

	public SlimeSlasher() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);

		baseDamage = POWER;
		misc = baseDamage * FATAL_BONUS_PER;
		isMultiDamage = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new SlimeSlasherAction(this));
	}

	@Override
	public AbstractCard makeCopy() {
		return new SlimeSlasher();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeDamage(UPGRADE_BONUS);
			misc += UPGRADE_BONUS * FATAL_BONUS_PER;
		}
	}

	public void recalculateDamage() {
		baseDamage = misc / FATAL_BONUS_PER;
		initializeDescription();
	}

	public int calculateSingleTargetDamage(AbstractMonster m) {
		isMultiDamage = false;
		calculateCardDamage(m);
		int result = damage;
		isMultiDamage = true;
		return result;
	}

	@Override
	public String getMetricID() {
		String id = cardID + GameLogicUtils.metricIdSeparator + misc;
		if (upgraded) {
			id = id + "+";
			if (timesUpgraded > 0) {
				id = id + timesUpgraded;
			}
		}

		return id;
	}

	public static SlimeSlasher createFromMetricID(String id) {
		int upgradeCount = 0;
		int index = id.lastIndexOf('+');
		if (index != -1) {
			try {
				upgradeCount = Integer.parseInt(id.substring(index + 1));
				id = id.substring(0, index);
			} catch (NumberFormatException ignore) {
			}
		}

		String[] tokens = id.split(GameLogicUtils.metricIdSeparator, -1);

		if (tokens[0].equals(ID)) {
			SlimeSlasher s = new SlimeSlasher();
			for (int i = 0; i < upgradeCount; i++) {
				s.upgrade();
			}
			s.misc = Integer.parseInt(tokens[1]);
			s.setDamageFromMisc();
			return s;
		} else {
			return null;
		}
	}

	public void setDamageFromMisc() {
		baseDamage = misc / SlimeSlasher.FATAL_BONUS_PER;
	}
}
