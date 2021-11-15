package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.BetterFriendlyMinionsUtils;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.animations.FastShakeAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.ChangeStateAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.city.Snecko;
import com.megacrit.cardcrawl.powers.*;
import com.megacrit.cardcrawl.vfx.combat.IntimidateEffect;

public class PerplexingEye extends AbstractMonsterCard {
	private static final String RAW_ID = "PerplexingEye";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String UPGRADE_DESCRIPTION = cardStrings.UPGRADE_DESCRIPTION;
	private static final int COST = 1;
	private static final CardType TYPE = CardType.POWER;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.SELF;

	private static final int POWER = 1;
	private static final int UPGRADE_BONUS = 1;

	public PerplexingEye() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Snecko.ID, 1);
		baseMagicNumber = magicNumber = POWER;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		addToBot(new ApplyPowerAction(p, p, new ConfusionPower(p)));
		addToBot(new ApplyPowerAction(p, p, new DrawPower(p, magicNumber), magicNumber));
	}

	@Override
	public AbstractCard makeCopy() {
		return new PerplexingEye();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeMagicNumber(UPGRADE_BONUS);
			rawDescription = cardStrings.UPGRADE_DESCRIPTION;
			initializeDescription();
		}
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		return new MonsterCardMoveInfo(AbstractMonster.Intent.STRONG_DEBUFF, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {

		addToBot(new ChangeStateAction(owner, "ATTACK"));
		addToBot(new SFXAction("MONSTER_SNECKO_GLARE"));
		addToBot(new VFXAction(owner, new IntimidateEffect(this.hb.cX, this.hb.cY), 0.5F));
		addToBot(new FastShakeAction(target, 1.0F, 1.0F));
		if (isAlly) {
			int debuff = BetterFriendlyMinionsUtils.minionAiRng.random(39);
			switch (debuff / 10) {
				case 0:
					addToBot(new ApplyPowerAction(target, owner, new WeakPower(target, debuff % 10 + 1, false)));
					break;
				case 1:
					addToBot(new ApplyPowerAction(target, owner, new VulnerablePower(target, debuff % 10 + 1, false)));
					break;
				case 2:
					addToBot(new ApplyPowerAction(target, owner, new PoisonPower(target, owner, debuff % 10 + 2)));
					break;
				case 3:
					addToBot(new ApplyPowerAction(target, owner, new StrengthPower(target, debuff % 10 / 4 + 2)));
					break;
			}
		} else {
			addToBot(new ApplyPowerAction(target, owner, new ConfusionPower(AbstractDungeon.player)));
		}
	}
}
