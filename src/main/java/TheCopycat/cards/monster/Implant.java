package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.beyond.WrithingMass;
import com.megacrit.cardcrawl.vfx.combat.FlyingOrbEffect;

public class Implant extends AbstractMonsterCard {
	private static final String RAW_ID = "Implant";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	private static final int COST = 0;
	private static final CardType TYPE = CardType.SKILL;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int POWER = 3;
	private static final int UPGRADE_BONUS = 1;

	public Implant() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, WrithingMass.ID, 4);
		baseMagicNumber = magicNumber = POWER;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		if (m != null) {
			this.addToBot(new VFXAction(new FlyingOrbEffect(m.hb.cX, m.hb.cY), 0.3f));
			addToBot(new AbstractGameAction() {
				@Override
				public void update() {
					int prev = m.maxHealth;
					m.decreaseMaxHealth(magicNumber);
					int amount = prev - m.maxHealth;
					p.increaseMaxHp(amount, true);
					isDone = true;
				}
			});
		}
	}

	@Override
	public AbstractCard makeCopy() {
		return new Implant();
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
		Hitbox tmp = AbstractDungeon.player.hb;
		AbstractDungeon.player.hb = owner.hb;
		this.addToBot(new VFXAction(new FlyingOrbEffect(target.hb.cX, target.hb.cY), 0.3f));
		AbstractDungeon.player.hb = tmp;

		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				int prev = target.maxHealth;
				target.decreaseMaxHealth(magicNumber);
				int amount = prev - target.maxHealth;
				owner.increaseMaxHp(amount, true);
				isDone = true;
			}
		});
	}
}
