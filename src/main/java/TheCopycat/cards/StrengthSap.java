package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.DoAreaAction;
import TheCopycat.interfaces.TargetAllyCard;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.GainStrengthPower;
import com.megacrit.cardcrawl.powers.LoseStrengthPower;
import com.megacrit.cardcrawl.powers.StrengthPower;

public class StrengthSap extends CustomCard implements TargetAllyCard {
	private static final String RAW_ID = "StrengthSap";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 1;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.COMMON;
	private static final CardTarget TARGET = CardTarget.SELF_AND_ENEMY;

	private static final int POWER = 2;
	private static final int UPGRADE_BONUS = 1;

	public StrengthSap() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new DoAreaAction(c -> new AbstractGameAction() {
			@Override
			public void update() {
				if (m != null && !m.hasPower(ArtifactPower.POWER_ID)) {
					addToTop(new ApplyPowerAction(c, c, new LoseStrengthPower(c, magicNumber), magicNumber));
					addToTop(new ApplyPowerAction(c, c, new StrengthPower(c, magicNumber), magicNumber));
					addToTop(new ApplyPowerAction(m, c, new GainStrengthPower(m, magicNumber), magicNumber));
				}
				addToTop(new ApplyPowerAction(m, c, new StrengthPower(m, -magicNumber), -magicNumber));

				isDone = true;
			}
		}));
	}

	@Override
	public AbstractCard makeCopy() {
		return new StrengthSap();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
