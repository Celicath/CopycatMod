package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.MindControlAction;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;

public class MindControl extends CustomCard {
	private static final String RAW_ID = "MindControl";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = -1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 15;
	private static final int UPGRADE_BONUS = 5;

	public MindControl() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
	}

	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		boolean canUse = super.canUse(p, m);
		if (!canUse) {
			return false;
		}

		int effect = EnergyPanel.totalCount;
		if (p.hasRelic(ChemicalX.ID)) {
			effect += 2;
		}
		if (m == null) {
			for (AbstractMonster mo : AbstractDungeon.getMonsters().monsters) {
				if (mo.currentHealth <= magicNumber * effect) {
					return true;
				}
			}
			return false;
		}
		if (m.currentHealth > magicNumber * effect) {
			canUse = false;
			cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[0];
		}

		return canUse;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new MindControlAction(p, m, magicNumber, freeToPlayOnce, energyOnUse));
	}

	@Override
	public AbstractCard makeCopy() {
		return new MindControl();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
