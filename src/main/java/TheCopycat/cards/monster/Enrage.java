package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.animations.TalkAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.GremlinNob;
import com.megacrit.cardcrawl.powers.AngerPower;

public class Enrage extends AbstractMonsterCard {
	private static final String RAW_ID = "Enrage";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 2;
	private static final CardType TYPE = CardType.POWER;
	private static final CardRarity RARITY = CardRarity.UNCOMMON;
	private static final CardTarget TARGET = CardTarget.SELF;
	private static final int NEW_COST = 1;

	public Enrage() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, GremlinNob.ID, 3);
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		playSfx();
		addToBot(new TalkAction(true, GremlinNob.DIALOG[0], 1.0F, 3.0F));
		addToBot(new ApplyPowerAction(p, p, new AngerPower(p, 1), 1));
	}

	private void playSfx() {
		int roll = MathUtils.random(2);
		if (roll == 0) {
			addToBot(new SFXAction("VO_GREMLINNOB_1A"));
		} else if (roll == 1) {
			addToBot(new SFXAction("VO_GREMLINNOB_1B"));
		} else {
			addToBot(new SFXAction("VO_GREMLINNOB_1C"));
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new Enrage();
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
		playSfx();
		addToBot(new TalkAction(owner, GremlinNob.DIALOG[0], 0.75F, 2.5F));
		addToBot(new ApplyPowerAction(owner, owner, new AngerPower(owner, 1), 1));
	}
}
