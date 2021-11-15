package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.powers.TurnFlightPower;
import TheCopycat.utils.MonsterCardMoveInfo;
import basemod.ReflectionHacks;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Byrd;
import com.megacrit.cardcrawl.powers.FlightPower;

public class Fly extends AbstractMonsterCard {
	private static final String RAW_ID = "Fly";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 1;
	private static final CardType TYPE = CardType.POWER;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;
	private static final int NEW_COST = 0;

	public Fly() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Byrd.ID, 2);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new ApplyPowerAction(p, p, new TurnFlightPower(p, 1)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Fly();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeBaseCost(NEW_COST);
		}
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(AbstractMonster.Intent.BUFF, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		if (owner instanceof Byrd) {
			addToBot(new ChangeStateAction(owner, "FLYING"));
			if (!ReflectionHacks.<Boolean>getPrivate(owner, Byrd.class, "isFlying")) {
				ReflectionHacks.setPrivate(owner, Byrd.class, "isFlying", true);
				addToBot(new ApplyPowerAction(owner, owner, new FlightPower(owner, ReflectionHacks.getPrivate(owner, Byrd.class, "flightAmt"))));
			} else {
				addToBot(new ApplyPowerAction(owner, owner, new FlightPower(owner, 0)));
			}
		} else {
			addToBot(new ApplyPowerAction(owner, owner, new TurnFlightPower(owner, 1)));
		}
	}
}
