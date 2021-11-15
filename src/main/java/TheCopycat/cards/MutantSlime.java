package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.SummonCopycatMinionAction;
import TheCopycat.friendlyminions.MirrorMinion;
import TheCopycat.friendlyminions.PetSlime;
import TheCopycat.patches.CharacterEnum;
import TheCopycat.powers.MutantEchoPower;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MutantSlime extends CustomCard {
	private static final String RAW_ID = "MutantSlime";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 2;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 15;
	private static final int UPGRADE_BONUS = 5;

	public MutantSlime() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		PetSlime slime = new PetSlime(new Color(0.5f, 0.5f, 1.0f, 1.0f));
		MirrorMinion minion = new MirrorMinion(slime.name, slime, magicNumber);
		addToBot(new SummonCopycatMinionAction(minion));
		addToBot(new ApplyPowerAction(minion, minion, new MutantEchoPower(minion)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new MutantSlime();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}
}
