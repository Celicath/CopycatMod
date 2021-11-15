package TheCopycat.cards.monster;

import TheCopycat.CopycatModMain;
import TheCopycat.interfaces.HoverMonsterCard;
import TheCopycat.utils.MonsterCardMoveInfo;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.exordium.Hexaghost;
import com.megacrit.cardcrawl.vfx.combat.GhostIgniteEffect;
import com.megacrit.cardcrawl.vfx.combat.HeartMegaDebuffEffect;

public class Divider extends AbstractMonsterCard implements HoverMonsterCard {
	private static final String RAW_ID = "Divider";
	public static final String ID = CopycatModMain.makeID(RAW_ID);
	private static final CardStrings cardStrings = CardCrawlGame.languagePack.getCardStrings(ID);
	public static final String NAME = cardStrings.NAME;
	public static final String DESCRIPTION = cardStrings.DESCRIPTION;
	public static final String[] EXTENDED_DESCRIPTION = cardStrings.EXTENDED_DESCRIPTION;
	private static final int COST = 4;
	private static final CardType TYPE = CardType.ATTACK;
	private static final CardRarity RARITY = CardRarity.RARE;
	private static final CardTarget TARGET = CardTarget.ENEMY;

	private static final int DAMAGE_BONUS = 1;
	private static final int NEW_COST = 3;
	private static final int HITS = 6;
	private static final int DIV = 12;

	public Divider() {
		super(ID, NAME, COST, DESCRIPTION, TYPE, RARITY, TARGET, Hexaghost.ID, 1);
		baseMagicNumber = magicNumber = DAMAGE_BONUS;
		exhaust = true;
	}

	@Override
	public void use(AbstractPlayer p, AbstractMonster m) {
		calculateCardDamage(m);
		for (int i = 0; i < HITS; i++) {
			addToBot(new VFXAction(p, new GhostIgniteEffect(m.hb.cX + MathUtils.random(-80.0F, 80.0F) * Settings.scale, m.hb.cY + MathUtils.random(-80.0F, 80.0F) * Settings.scale), 0.05F));
			if (MathUtils.randomBoolean()) {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));
			} else {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));
			}

			addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
		}
		addToBot(new AbstractGameAction() {
			@Override
			public void update() {
				rawDescription = DESCRIPTION;
				initializeDescription();
				isDone = true;
			}
		});
	}

	@Override
	public void calculateCardDamage(AbstractMonster m) {
		if (m != null) {
			baseDamage = m.currentHealth / DIV + magicNumber;
		} else {
			baseDamage = -1;
		}
		super.calculateCardDamage(m);
	}

	@Override
	public AbstractCard makeCopy() {
		return new Divider();
	}

	@Override
	public void upgrade() {
		if (!upgraded) {
			upgradeName();
			upgradeBaseCost(NEW_COST);
		}
	}

	@Override
	public void onHoverMonster(AbstractMonster m) {
		rawDescription = EXTENDED_DESCRIPTION[0];
		initializeDescription();
	}

	@Override
	public void onUnhoverMonster() {
		rawDescription = DESCRIPTION;
		initializeDescription();
	}

	@Override
	public void monsterTurnApplyPowers(AbstractMonster owner, AbstractCreature target) {
		if (target != null) {
			baseDamage = target.currentHealth / DIV + magicNumber;
		} else {
			baseDamage = -1;
		}
		super.monsterTurnApplyPowers(owner, target);
	}

	@Override
	public MonsterCardMoveInfo createMoveInfo(boolean isAlly) {
		// TODO: how to make this work with friendly minion?
		return new MonsterCardMoveInfo(AbstractMonster.Intent.ATTACK, baseDamage, HITS, true, this);
	}

	@Override
	public void monsterTakeTurn(AbstractMonster owner, AbstractCreature target, boolean isAlly) {
		if (!isAlly) {
			addToBot(new VFXAction(new HeartMegaDebuffEffect()));
		}

		for (int i = 0; i < HITS; i++) {
			addToBot(new VFXAction(owner, new GhostIgniteEffect(target.hb.cX + MathUtils.random(-80.0F, 80.0F) * Settings.scale, target.hb.cY + MathUtils.random(-80.0F, 80.0F) * Settings.scale), 0.05F));
			if (MathUtils.randomBoolean()) {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_1", 0.3F));
			} else {
				addToBot(new SFXAction("GHOST_ORB_IGNITE_2", 0.3F));
			}

			addToBot(new DamageAction(target, new DamageInfo(owner, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_HEAVY, true));
		}
	}
}
