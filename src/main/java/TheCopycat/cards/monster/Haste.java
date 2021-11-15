package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.HealAction;
import com.megacrit.cardcrawl.actions.unique.RemoveDebuffsAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.TimeEater;

public class Haste extends AbstractMonsterCard {
	private static final String RAW_ID = "Haste";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 10;
	private static final int UPGRADE_BONUS = 5;

	public Haste() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, TimeEater.ID, 5);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new TalkAction(true, TimeEater.DIALOG[1], 0.5F, 2.0F));
		addToBot(new HealAction(p, p, p.maxHealth * magicNumber / 100));
		addToBot(new RemoveDebuffsAction(p));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Haste();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
		}
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(AbstractMonster.Intent.BUFF, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		addToBot(new TalkAction(owner, TimeEater.DIALOG[1], 0.5F, 2.0F));
		addToBot(new HealAction(owner, owner, owner.maxHealth * magicNumber / 100));
		addToBot(new RemoveDebuffsAction(owner));
	}
}
