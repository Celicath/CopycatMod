package TheCopycat.cards;

import TheCopycat.CopycatModMain;
import TheCopycat.actions.CreateSubstituteAction;
import TheCopycat.friendlyminions.SubstituteMinion;
import TheCopycat.patches.CharacterEnum;
import basemod.abstracts.CustomCard;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.IntangiblePlayerPower;
import com.megacrit.cardcrawl.relics.TungstenRod;

public class Substitute extends CustomCard {
	private static final String RAW_ID = "Substitute";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	public static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String IMG = CopycatModMain.GetCardPath(RAW_ID);
	private static final int COST = 2;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardColor COLOR = CharacterEnum.CardColorEnum.COPYCAT_BLUE;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int NEW_COST = 1;

	public Substitute() {
		super(ID, NAME, IMG, COST, DESCRIPTION, TYPE, COLOR, RARITY, TARGET);
		isEthereal = true;
		tags.add(CardTags.HEALING);
	}

	public static int amount(AbstractPlayer p) {
		return Math.max(p.maxHealth / 4, 1);
	}

	public boolean canUse(AbstractPlayer p, AbstractMonster m) {
		boolean canUse = super.canUse(p, m);
		if (!canUse) {
			return false;
		}

		if (!SubstituteMinion.instance.isDead) {
			canUse = false;
			cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[3];
		} else {
			int amount = amount(p);
			if (p.hasRelic(TungstenRod.ID)) amount--;
			if (p.hasPower(IntangiblePlayerPower.POWER_ID) && amount > 1) amount = 1;
			if (p.currentHealth <= amount) {
				canUse = false;
				cantUseMessage = cardStrings.EXTENDED_DESCRIPTION[2];
			}
		}

		return canUse;
	}

	@Override
	public void applyPowers() {
		super.applyPowers();
		rawDescription = DESCRIPTION + cardStrings.EXTENDED_DESCRIPTION[0] + amount(AbstractDungeon.player) + cardStrings.EXTENDED_DESCRIPTION[1];
		initializeDescription();
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		int amount = amount(p);
		addToBot(new LoseHPAction(p, p, amount));
		addToBot(new CreateSubstituteAction(amount));
	}

	@Override
	public void onMoveToDiscard() {
		rawDescription = DESCRIPTION;
		initializeDescription();
	}

	@Override
	public AbstractCard makeCopy() {
		return new Substitute();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeBaseCost(NEW_COST);
		}
	}
}
