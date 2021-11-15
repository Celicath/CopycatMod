package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.powers.EnemyHexPower;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Chosen;
import com.megacrit.cardcrawl.powers.HexPower;

public class Hex extends AbstractMonsterCard {
	private static final String RAW_ID = "Hex";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 1;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 15;
	private static final int UPGRADE_BONUS = 5;

	public Hex() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Chosen.ID, 4);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new ApplyPowerAction(m, p, new EnemyHexPower(m, magicNumber)));
	}

	@Override
	public AbstractCard makeCopy() {
		return new Hex();
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
		return new MonsterCardMoveInfo(AbstractMonster.Intent.STRONG_DEBUFF, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		addToBot(new TalkAction(owner, Chosen.DIALOG[0]));
		addToBot(new ChangeStateAction(owner, "ATTACK"));
		addToBot(new WaitAction(0.2F));
		if (target instanceof AbstractMonster) {
			addToBot(new ApplyPowerAction(target, owner, new EnemyHexPower((AbstractMonster) target, magicNumber)));
		} else {
			addToBot(new ApplyPowerAction(AbstractDungeon.player, owner, new HexPower(target, 1)));
		}
	}
}
